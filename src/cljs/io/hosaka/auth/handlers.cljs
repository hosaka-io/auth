(ns io.hosaka.auth.handlers
  (:require [re-frame.core :as re-frame]
            [io.hosaka.auth.routes :as routes]))

(def init-db
  {:page ::routes/root
   :route-params nil
   :user nil})

(defn reg-handlers []

  (re-frame/reg-event-db
   :initialize
   (fn [_ _]
     init-db))

  (re-frame/reg-event-db
   :nav
   (fn [db [_ {:keys [handler route-params]}]]
     (assoc db :page handler :route-params route-params)))


  )

