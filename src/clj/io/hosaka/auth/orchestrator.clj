(ns io.hosaka.auth.orchestrator
  (:require [com.stuartsierra.component :as component]
            [clojure.string :as s]
            [manifold.deferred :as d]
            [io.hosaka.auth.oauth :as oauth]))

(defrecord Orchestrator [oauth]
  component/Lifecycle

  (start [this]
    this)

  (stop [this]
    this))

(defn new-orchestrator []
  (component/using
   (map->Orchestrator {})
   [:oauth]))

(defn get-redirect-url [{:keys [oauth]}]
  (oauth/get-redirect-url oauth))

(defn get-token [{:keys [oauth]} code]
  (oauth/get-token oauth code))

