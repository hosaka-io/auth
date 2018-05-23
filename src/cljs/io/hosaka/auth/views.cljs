(ns io.hosaka.auth.views
  (:require [re-frame.core :as re-frame]
            [io.hosaka.auth.routes :as routes]))


;; -------------------------
;; Views

(defn home-page []
   [:div.row.justify-content-center.pt-5
    [:div.col-10.col-lg.col-sm-8.col-md-6
     [:div.card.border-primary.mb-3
      [:div.card-header "Login"]
      [:div.card-body
       [:h4.card-title "Please click to login"]
       [:div.row.justify-content-center.pt-2
        [:a.btn.btn-primary.btn-lg
         {:href "/redirect"}
         "Login with Google"]]]]]])

(defn about-page []
  [:div [:h2 "About auth?"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn pages [page]
  [:div
   [:nav.navbar.navbar-expand-lg.navbar-dark.bg-primary.hosaka-nav
    [:a.navbar-brand {:href "/"}
     [:img {:src "/assets/images/hosaka.png"}]
     "Hosaka"]
    [:div.nav-item "Authentication"]]
   [:div.container.page
   (case @page
     ::routes/root [home-page]
     ::routes/about [about-page]

     [:div (str "Page: " @page)]
     )]])

(defn main-panel []
  (let [page (re-frame/subscribe [:page])]
    (fn []
         [pages page]
         )))
