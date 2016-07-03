(ns cash-service.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as ring-json]
            [ring.util.response :as res]
            [cheshire.core :as json]
            [cash-service.data :as data]
            [cash-service.category :as category]
            [cash-service.balance :as balance]
            [clj-http.client :as http]
            [cash-service.auth :as auth]))

(defn init []
  (data/init)
  (category/init)
  (balance/init))

(defn destroy []
  (data/destroy)
  (category/destroy)
  (balance/destroy))

(defn convertIdToCaterory [item]
  (assoc item :category ((category/getItem (item :category)) :name )))

(defn getDataList[]
  (map convertIdToCaterory (data/getList)))

(defn setDefaultCategory [item]
  (assoc item :category 1))

(defn deleteCategory [id]
  (data/updateByArray (map setDefaultCategory (data/getByCategory id)))
  (if (= ((category/getItem id) :type) "out")
    (balance/increaseMoney (* ((category/getItem id) :money) 2)))
  (category/delete id))

(defn setData [item]
  (data/setItem item)
  (case ((category/getItem (item :category)) :type)
    "in" (balance/increaseMoney (item :money))
    "out" (balance/decreaseMoney (item :money))
    "default" (print (item :type)))
  (category/increaseMoney (item :category) (item :money)))


(defroutes app-routes
  (GET "/" []
       (res/redirect "/login"))

  (GET "/bob" []
       (let [response (http/get "https://www.infraware.net/ajax/boards/GetRestaurantmenuImage")]
            (res/redirect (:url (json/parse-string (response :body) true)))))

  (POST "/api/v0.1/data/" request
        (let [id (get-in request [:body :category])]
          (if (category/contain? id)
            (setData (request :body)))
          (if (category/contain? id)
            (res/response {:result "OK"})
            (res/response {:result "Error"}))))

  (GET "/api/v0.1/data/" []
       (res/response (getDataList)))

  (POST "/api/v0.1/category/" request
        (res/response {:result "OK" :id (-> (request :body) category/setItem first val)}))

  (GET "/api/v0.1/category/" []
       (res/response (category/getList)))

  (DELETE "/api/v0.1/category/:id/" [id]
          (deleteCategory id)
          (res/response {:result "OK"}))

  (GET "/api/v0.1/balance/" []
       (res/response {:money ((balance/getItem) :money)})))

(def app
  (routes
    (-> app-routes
        (ring-json/wrap-json-body {:keywords? true})
        (ring-json/wrap-json-response)
        (wrap-defaults (assoc site-defaults :security {:anti-forgery false})))
    (-> auth/auth-routes)
    (route/not-found "Not Found")))
