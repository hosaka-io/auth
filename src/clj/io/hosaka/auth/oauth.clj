(ns io.hosaka.auth.oauth
  (:require [com.stuartsierra.component :as component]
            [clojure.java.io :refer [reader]]
            [aleph.http :as http]
            [manifold.deferred :as d]
            [cheshire.core :as json]
            [buddy.sign.jwt :as jwt]
            [buddy.core.keys :as keys]
            [buddy.sign.jws :refer [decode-header]]
            [byte-streams :as bs]
            [cemerick.url :refer [url-encode]]))

(defn parse-stream [stream]
  (with-open [rdr (reader stream)]
    (json/parse-stream rdr true)))

(defn get-conf [url]
  (d/chain
   (http/get url)
   :body
   parse-stream))

(defrecord OAuth [env conf keys]
  component/Lifecycle

  (start [this]
    (assoc this
           :keys (atom {})
           :conf (-> env :oauth-configuration get-conf)))

  (stop [this]
    (assoc this
           :keys nil
           conf nil)))

(defn new-oauth [env]
  (let [oauth-keys (->> env keys (filter #(->> % name (re-matches #"^oauth-(.*)"))))]
    (map->OAuth {:env (select-keys env oauth-keys)})))

(defn create-token-post [code {:keys [oauth-client_id oauth-secret oauth-redirect]}]
  (str
   "code=" (url-encode code)
   "&client_id=" oauth-client_id
   "&client_secret=" oauth-secret
   "&redirect_uri=" (url-encode oauth-redirect)
   "&grant_type=authorization_code"))

(defn map-jwk [jwks]
  (apply
   hash-map
   (mapcat
    #(vector (:kid %1) (assoc %1 :key (keys/jwk->public-key %1)))
    jwks)))

(defn get-key [{:keys [keys conf]} kid]
  (if (contains? @keys kid)
    (d/success-deferred (get @keys kid))
    (d/chain
     conf               ;;Get oauth2 configuration
     :jwks_uri          ;;JWKs field
     #(http/get %)      ;;Get JWKs
     :body              ;;Response body
     parse-stream       ;;Body JSON -> map
     :keys              ;;Keys
     map-jwk            ;;Parse keys
     (fn [jwks]
       (swap! keys #(merge % jwks))
       (if (contains? jwks kid)
         (get jwks kid)
         (throw (Exception. (str "KID: " kid " not found"))))))))

(defn get-kid [jwt]
  (->
   jwt
   decode-header
   :kid))

(defn get-token [{:keys [conf env] :as oauth} code]
  (d/let-flow [token_endpoint (d/chain conf :token_endpoint)
               token-post (create-token-post code env)
               token (d/chain
                      (http/post token_endpoint
                                 {:body token-post
                                  :headers {"content-type" "application/x-www-form-urlencoded"}})
                      :body
                      parse-stream)
               key (->> token :id_token get-kid (get-key oauth))
               ]
    (assoc token
           :jwt (jwt/unsign
                 (:id_token token)
                 (:key key)
                 {:alg :rs256}))))

(defn get-redirect-url [{:keys [conf env] :as oauth}]
  (d/let-flow [authorization_endpoint (d/chain conf :authorization_endpoint)
               {:keys [oauth-client_id oauth-scopes oauth-redirect]} env]
    (str
     authorization_endpoint
     "?redirect_uri=" (url-encode oauth-redirect)
     "&response_type=code"
     "&client_id=" oauth-client_id
     "&scope=" oauth-scopes)))
