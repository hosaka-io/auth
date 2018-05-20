(ns io.hosaka.auth.handler
  (:require
            [config.core :refer [env]]
            [io.hosaka.auth.html :as html]
            [com.stuartsierra.component :as component]
            [clojure.data.json :as json]
            [manifold.deferred :as d]
            [yada.yada :as yada]



            ))


(defn build-routes [o]
  ["/" [
                      (html/build-routes o)
                      ]])

(defrecord Handler [orchestrator routes]
  component/Lifecycle

  (start [this]
    (assoc this :routes (build-routes orchestrator)))

  (stop [this]
    (assoc this :routes nil)))


(defn new-handler []
  (component/using
   (map->Handler {:orchestrator {}})
   []))


(comment
 (defroutes routes
   (GET "/" [] (loading-page))
   (GET "/about" [] (loading-page))
   (resources "/")
   (not-found "Not Found"))

 [io.hosaka.auth.middleware :refer [wrap-middleware]]
 (def app (wrap-middleware #'routes)))
