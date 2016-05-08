(ns cash-service.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as ring-json]
            [ring.util.response :as res]
            [cash-service.db-handler :refer :all]))

(defn init []
  (create-table!))

(defn destroy []
  (drop-table!))

(defroutes app-routes
  (GET "/" []
       "Hello World")

  (POST "/api/v0.1/data/" request
        (set-data<! (get-in request [:body]))
        (res/response {:result "OK"}))

  (GET "/api/v0.1/data/" []
       (res/response (get-data)))

  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (ring-json/wrap-json-body {:keywords? true})
      (ring-json/wrap-json-response)
      (wrap-defaults (assoc site-defaults :security {:anti-forgery false}))))
