(ns cash_service.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as ring-json]
            [cheshire.core :as json]
            [cash_service.data :as data]
            [cash_service.category :as category]
            [cash_service.balance :as balance]
            [cash_service.api_handler :refer [api-routes]]
            [cash_service.app_handler :refer [app-routes]]))

(defn init []
  (data/init)
  (category/init)
  (balance/init))

(defn destroy []
  (data/destroy)
  (category/destroy)
  (balance/destroy))

(def api-and-app
  (routes
    (-> api-routes
        (ring-json/wrap-json-body {:keywords? true})
        (ring-json/wrap-json-response)
        (wrap-defaults (assoc site-defaults :security {:anti-forgery false})))
    (-> app-routes)
    (route/not-found "Not Found")))
