(ns io.hosaka.auth
  (:require [config.core :refer [env]]
            [clojure.tools.nrepl.server :as nrepl]
            [com.stuartsierra.component :as component]
            [manifold.deferred :as d]
            [clojure.tools.logging :as log]
            [io.hosaka.auth.handler :refer [new-handler]]
            [io.hosaka.auth.keys :refer [new-keys]]
            [io.hosaka.auth.oauth :refer [new-oauth]]
            [io.hosaka.auth.orchestrator :refer [new-orchestrator]]
            [io.hosaka.auth.user :refer [new-user]]
            [io.hosaka.common.server :refer [new-server]]
            )
  (:gen-class))

(defn init-system [env]
  (component/system-map
   :orchestrator (new-orchestrator)
   :oauth (new-oauth env)
   :handler (new-handler)
   :server (new-server env)
   :keys (new-keys env)
   :user (new-user env)
   ))

(defonce system (atom {}))
(defonce repl (atom nil))

(defn -main [& args]
  (let [semaphore (d/deferred)]
    (reset! system (init-system env))

    (swap! system component/start)
    (reset! repl (if-let [nrepl-port (:nrepl-port env)] (nrepl/start-server :port nrepl-port) nil))
    (log/info "Auth Service booted")
    (deref semaphore)
    (log/info "Auth Service going down")
    (component/stop @system)
    (swap! repl (fn [server] (do (if server (nrepl/stop-server server)) nil)))

    (shutdown-agents)
    ))

