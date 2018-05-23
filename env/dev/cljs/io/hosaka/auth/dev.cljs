(ns ^:figwheel-no-load io.hosaka.auth.dev
  (:require
    [io.hosaka.auth.core :as core]
    [re-frisk.core :as re-frisk]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(re-frisk/enable-re-frisk!)

(core/init!)
