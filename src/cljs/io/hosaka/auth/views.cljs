(ns io.hosaka.auth.views
  (:require [re-frame.core :as re-frame]
            [io.hosaka.auth.routes :as routes]))


;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to auth"]
   [:div [:a {:href "/about"} "go to about page"]]])

(defn about-page []
  [:div [:h2 "About auth?"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn pages [page]
  [:div.page
   (case @page
     ::routes/root [home-page]
     ::routes/about [about-page]

     [:div (str "Page: " @page)]
     )])

(defn main-panel []
  (let [page (re-frame/subscribe [:page])]
    (fn []
         [pages page]
         )))
