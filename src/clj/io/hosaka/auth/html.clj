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
   [:title "Hosaka Auth"]
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css "/assets/css/bootstrap.min.css")
   (include-css (if (env :dev) "/assets/css/main.css" "/assets/css/main.min.css"))])

(defn loading-page []
  (html5
   (head)
   [:body {:class "body-container"}
    mount-target
    [:script {:src "https://code.jquery.com/jquery-3.3.1.slim.min.js"
              :integrity "sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
              :crossorigin"anonymous"}]
    [:script {:src "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"
              :integrity "sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49"
              :crossorigin "anonymous"}]
    [:script {:src "https://code.jquery.com/jquery-3.3.1.slim.min.js"
              :integrity "sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
              :crossorigin "anonymous"}]
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
