(ns io.hosaka.auth.user
  (:require [com.stuartsierra.component :as component]
            [io.hosaka.auth.keys :as keys]
            [clojure.java.io :refer [reader]]
            [aleph.http :as http]
            [manifold.deferred :as d]
            [cheshire.core :as json]
            [byte-streams :as bs]
            [cemerick.url :refer [url-encode]]))

(defrecord User [env keys]
  component/Lifecycle

  (start [this]
    this)

  (stop [this]
    this))

(defn new-user [env]
  (component/using
   (map->User {:env (select-keys env [:service-id :user-url])})
   [:keys]))

(defn parse-stream [stream]
  (with-open [rdr (reader stream)]
    (json/parse-stream rdr true)))

(defn get-user [{:keys [env keys]} email]
  (let [token (keys/get-service-token keys)]
    (d/chain
     (http/get (str (:user-url env) "users?login=" (url-encode email)) {:headers {"authorization" (str "Bearer: " token)}})`
     :body
     parse-stream)))
