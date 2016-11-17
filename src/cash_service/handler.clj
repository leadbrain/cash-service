(ns cash-service.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as ring-json]
            [cheshire.core :as json]
            [cash-service.data :as data]
            [cash-service.category :as category]
            [cash-service.balance :as balance]
            [cash-service.account :as account]
            [cash-service.api-handler :refer [api-routes]]
            [cash-service.app-handler :refer [app-routes]]
            [cash-service.api :refer [app]]))

(defn init []
  (data/init)
  (category/init)
  (balance/init)
  (account/init))

(defn destroy []
  (data/destroy)
  (category/destroy)
  (balance/destroy)
  (account/destroy))

(def api-and-app
  (routes
    (-> api-routes
        (ring-json/wrap-json-body {:keywords? true})
        (ring-json/wrap-json-response)
        (wrap-defaults (assoc site-defaults :security {:anti-forgery false})))
    (-> app-routes)
    (-> app)
    (route/not-found "Not Found")))
