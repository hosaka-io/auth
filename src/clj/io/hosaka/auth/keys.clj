(ns io.hosaka.auth.keys
  (:require [clj-crypto.core :as crypto]
            [buddy.sign.jwt :as jwt]
            [com.stuartsierra.component :as component]
            [clj-time.core :as time]))

(defrecord Keys [key env]
  component/Lifecycle

  (start [this]
    (assoc
     this
     :key
     (crypto/decode-private-key {:algorithm "ECDSA"
                                 :bytes (crypto/decode-base64 (:service-key env))})))

  (stop [this]
    (assoc this :key nil)))

(defn new-keys [env]
  (map->Keys {:env (select-keys env [:service-key :service-id])}))

(defn create-token [{:keys [env key]} claims]
  (let [n (time/now)]
    (jwt/sign
     (assoc claims
            :iss (:service-id env)
            :iat n
            :jti (str (java.util.UUID/randomUUID))
            :exp (time/plus n (time/hours 1)))
     key
     {:alg :es256 :header {:kid (:service-id env)}})))

