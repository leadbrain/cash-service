(ns cash-service.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as ring-json]
            [ring.util.response :as res]))

(defroutes app-routes
  (GET "/" []
       "Hello World")

  (POST "/api/v0.1/data/" request
        (res/response {:item (get-in request [:body "item"])}))

  (GET "/api/v0.1/data/" []
       (res/response {:result "OK"}))

  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (ring-json/wrap-json-body)
      (ring-json/wrap-json-response)
      (wrap-defaults (assoc site-defaults :security {:anti-forgery false}))))
