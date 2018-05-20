(ns io.hosaka.auth.html
  (:require [hiccup.page :refer [include-js include-css html5]]
            [config.core :refer [env]]
            [yada.yada :as yada]
            [yada.resources.classpath-resource :refer [new-classpath-resource]]))


(def mount-target
  [:div#app
   [:h3 "ClojureScript has not been compiled!"]
   [:p "please run "
    [:b "lein figwheel"]
    " in order to start the compiler"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
   (head)
   [:body {:class "body-container"}
    mount-target
    (include-js "/assets/js/app.js")]))

(defn build-routes [o]
  ["" [
       ["assets/" (new-classpath-resource "public") ]
       [true (yada/resource {:methods
                             {:get
                              {:produces #{"text/html"}
                               :response (fn [ctx] (loading-page))
                               }}})]
       ]
   ])
