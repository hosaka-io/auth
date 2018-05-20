(ns io.hosaka.auth
  (:require [config.core :refer [env]]
            [com.stuartsierra.component :as component]
            [manifold.deferred :as d]
            [clojure.tools.logging :as log]
            [io.hosaka.common.server :refer [new-server]]
            [io.hosaka.auth.handler :refer [new-handler]])
  (:gen-class))

(defonce system (atom {}))

(defn init-system [env]
  (component/system-map
   :handler (new-handler)
   :server (new-server env)
   ))

(defn -main [& args]
  (let [semaphore (d/deferred)]
    (reset! system (init-system env))

    (swap! system component/start)
    (log/info "Auth Service booted")
    (deref semaphore)
    (log/info "Auth Service going down")
    (component/stop @system)

    (shutdown-agents)
    ))

