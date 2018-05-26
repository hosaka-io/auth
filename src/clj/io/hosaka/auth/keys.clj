(ns io.hosaka.auth.keys
  (:require [clj-crypto.core :as crypto]
            [buddy.sign.jwt :as jwt]
            [com.stuartsierra.component :as component]
            [clj-time.core :as time]))

(defrecord Keys [key env token]
  component/Lifecycle

  (start [this]
    (assoc
     this
     :token (atom nil)
     :key (crypto/decode-private-key {:algorithm "ECDSA"
                                 :bytes (crypto/decode-base64 (:service-key env))})))

  (stop [this]
    (assoc this :key nil :token nil)))

(defn new-keys [env]
  (map->Keys {:env (select-keys env [:service-key :service-id])}))

(defn create-token
  ([keys claims] (create-token keys claims (time/now)))
  ([{:keys [env key]} claims n]
   (jwt/sign
    (assoc claims
           :iss (:service-id env)
           :iat n
           :jti (str (java.util.UUID/randomUUID))
           :exp (time/plus n (time/hours 8)))
    key
    {:alg :es256 :header {:kid (:service-id env)}})))

(defn get-service-token [keys]
  (:token
   (swap!
    (:token keys)
    (fn [token]
      (let [n (clj-time.core/now)]
        (if (or
             (nil? token)
             (clj-time.core/after? n (:exp token)))
          (hash-map
           :token (create-token keys {:sub (-> keys :env :service-id)} n)
           :exp (time/plus n (time/hours 7)))
          token))))))

