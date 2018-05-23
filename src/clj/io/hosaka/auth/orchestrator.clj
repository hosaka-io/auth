(ns io.hosaka.auth.orchestrator
  (:require [com.stuartsierra.component :as component]
            [clojure.string :as s]
            [manifold.deferred :as d]
            [io.hosaka.auth.keys :as keys]
            [io.hosaka.auth.user :as user]
            [io.hosaka.auth.oauth :as oauth]))

(defrecord Orchestrator [oauth user keys]
  component/Lifecycle

  (start [this]
    this)

  (stop [this]
    this))

(defn new-orchestrator []
  (component/using
   (map->Orchestrator {})
   [:oauth :user :keys]))

(defn get-redirect-url [{:keys [oauth]}]
  (oauth/get-redirect-url oauth))

(defn get-token [{:keys [oauth user keys]} code]
  (d/let-flow [user_id (oauth/get-token oauth code)
               user_info (user/get-user user (-> user_id :jwt :email))]
     (keys/create-token
      keys
      {
       :sub (:id user_info)
       :name (:name user_info)
       :email (:username user_info)
       :roles (:roles user_info)})))

