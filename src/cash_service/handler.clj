(ns cash-service.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as ring-json]
            [ring.util.response :as res]
            [cheshire.core :as json]
            [cash-service.data :as data]
            [cash-service.category :as category]
            [clj-http.client :as http]))

(defn init []
  (data/init)
  (category/init))

(defn destroy []
  (data/destroy)
  (category/destroy))

(defn convertIdToCaterory [item]
  (assoc item :category (category/getName (item :category))))

(defn getDataList[]
  (map convertIdToCaterory (data/getList)))

(defn setDefaultCategory [item]
  (assoc item :category 0))

(defn deleteCategory [id]
  (data/updateByArray (map #(assoc % :category 0) (data/getByCategory id)))
  (category/delete id))

(defroutes app-routes
  (GET "/" []
       "Hello")

  (GET "/bob" []
       (let [response (http/get "https://www.infraware.net/ajax/boards/GetRestaurantmenuImage")]
            (res/redirect (:url (json/parse-string (:body response) true)))))

  (POST "/api/v0.1/data/" request
        (let [id (get-in request [:body :category])]
          (if (category/contain? id)
            (data/setItem (get-in request [:body])))
          (if (category/contain? id)
            (res/response {:result "OK"})
            (res/response {:result "Error"}))))

  (GET "/api/v0.1/data/" []
       (res/response (getDataList)))

  (POST "/api/v0.1/category/" request
        (res/response {:result "OK" :id (-> (get-in request [:body]) category/setItem first val)}))

  (GET "/api/v0.1/category/" []
       (res/response (category/getList)))

  (DELETE "/api/v0.1/category/:id/" [id]
          (deleteCategory id)
          (res/response {:result "OK"}))

  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (ring-json/wrap-json-body {:keywords? true})
      (ring-json/wrap-json-response)
      (wrap-defaults (assoc site-defaults :security {:anti-forgery false}))))
