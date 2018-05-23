(ns io.hosaka.auth.handler
  (:require
            [config.core :refer [env]]
            [io.hosaka.auth.html :as html]
            [com.stuartsierra.component :as component]
            [clojure.data.json :as json]
            [manifold.deferred :as d]
            [yada.yada :as yada]
            [io.hosaka.auth.orchestrator :as orchestrator]))


(defn redirect [orchestrator ctx]
  (d/chain
   (orchestrator/get-redirect-url orchestrator)
   #(assoc (:response ctx)
          :status 302
          :headers {"location" %})))

(defn token [orchestrator ctx]
  (let [code (-> ctx :parameters :query :code)]
    (d/chain
     (orchestrator/get-token orchestrator code)
     (fn [jwt]
       (assoc (:response ctx)
              :headers {
                        "Set-Cookie" (str "access_token=" jwt ";Path=/;domain=.hosaka.io;Max-Age=28800;")
                        "location" "/"}
              :status 302
              :body (hash-map :key jwt :code code))))))

(defn get-user-info [orchestrator ctx]
  (let [token (:cookies ctx)]
    (clojure.pprint/pprint ctx)
    {:user "name" :e "mail"})
  )

(defn build-routes [orchestrator]
  ["/" [
        ["api/user"
         (yada/resource
          {:methods
           {:get
            {:produces "application/json"
             :response (partial get-user-info orchestrator)}}})]
        ["redirect"
         (yada/resource
          {:methods
           {:get
            {:response (partial redirect orchestrator)
             :produces #{"text/html"}
             }}})]
        ["oauth2"
         (yada/resource
          {:parameters {:query {:code String}}
           :methods
           {:get
            {:response (partial token orchestrator)
             :produces "application/json"
             }}})]
        (html/build-routes orchestrator)
        ]])

(defrecord Handler [orchestrator routes]
  component/Lifecycle

  (start [this]
    (assoc this :routes (build-routes orchestrator)))

  (stop [this]
    (assoc this :routes nil)))


(defn new-handler []
  (component/using
   (map->Handler {})
   [:orchestrator]))


(comment
 (defroutes routes
   (GET "/" [] (loading-page))
   (GET "/about" [] (loading-page))
   (resources "/")
   (not-found "Not Found"))

 [io.hosaka.auth.middleware :refer [wrap-middleware]]
 (def app (wrap-middleware #'routes)))
