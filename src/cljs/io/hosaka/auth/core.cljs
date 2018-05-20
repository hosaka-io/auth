(ns io.hosaka.auth.core
    (:require [reagent.core :as reagent :refer [atom]]
              [re-frame.core :as re-frame]
              [bidi.bidi :as bidi]
              [accountant.core :as accountant]
              [io.hosaka.auth.routes :as routes]
              [io.hosaka.auth.handlers :refer [reg-handlers]]
              [io.hosaka.auth.subs :refer [reg-subs]]
              [io.hosaka.auth.views :refer [main-panel]]
              ))

(defn accountant-configuration [routes]
  {:nav-handler
   (fn [path]
     (re-frame/dispatch [:nav (bidi/match-route routes path)]))
   :path-exists?
   (fn [path]
     (boolean (bidi/match-route routes path)))})

(defn ^:export init! []
  (reg-handlers)
  (reg-subs)
  (accountant/configure-navigation! (accountant-configuration routes/app-routes))
  (re-frame/dispatch-sync [:initialize])
  (accountant/dispatch-current!)
  (reagent/render [main-panel] (.getElementById js/document "app")))

(comment
  (defonce page (atom #'home-page))

  (defn current-page []
    [:div [@page]])

  (secretary/defroute "/" []
    (reset! page #'home-page))

  (secretary/defroute "/about" []
    (reset! page #'about-page))

  ;; -------------------------
  ;; Initialize app

  (defn mount-root []
    (reagent/render [current-page] (.getElementById js/document "app")))

  (defn init! []
    (accountant/configure-navigation!
     {:nav-handler
      (fn [path]
        (secretary/dispatch! path))
      :path-exists?
      (fn [path]
        (secretary/locate-route path))})
    (accountant/dispatch-current!)
    (mount-root)))
