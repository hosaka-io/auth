(ns io.hosaka.auth.prod
  (:require [io.hosaka.auth.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
