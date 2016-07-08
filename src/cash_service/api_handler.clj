;; File      : api_handler.clj
;; Author    : YW. Jang
;; Date      : 2016.07.04
;;
;; Copyright 2016, YW. Jang, All rights reserved.

(ns cash-service.api-handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as res]
            [cheshire.core :as json]
            [cash-service.data :as data]
            [cash-service.category :as category]
            [cash-service.balance :as balance]))

(declare convertIdToCaterory)
(declare getDataList)
(declare setDefaultCategory)
(declare deleteCategory)
(declare setData)


(defroutes api-routes
  (GET "/api/v0.1/data/" []
       (res/response (getDataList)))

  (GET "/api/v0.1/balance/" []
       (res/response {:money ((balance/getItem) :money)}))

  (GET "/api/v0.1/category/" []
       (res/response (category/getList)))

  (POST "/api/v0.1/login" [param :as request]
        ; TO DO
        (str param)
        (res/redirect "/home")) ; temporary redirect for leadbrain
        ; It does not ready for json type yet.
        ;{body :body} (slurp body))

  (POST "/api/v0.1/signup" [param :as request]
        (str param)
        ; TO DO
        (res/redirect "/home")) ; temporary redirect for leadbrain
        ; It does not ready for json type yet.
        ;{body :body} (slurp body)))

  (POST "/api/v0.1/data/" request
        (let [id (get-in request [:body :category])]
          (if (category/contain? id)
            (setData (request :body)))
          (if (category/contain? id)
            (res/response {:result "OK"})
            (res/response {:result "Error"}))))

  (POST "/api/v0.1/category/" request
        (res/response {:result "OK" :id (-> (request :body) category/setItem first val)}))

  (DELETE "/api/v0.1/category/:id/" [id]
          (deleteCategory id)
          (res/response {:result "OK"})))

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

(defn get-base-uri [request]
  (let [scheme (name (:scheme request))
      context (:context request)
      hostname (get (:headers request) "host")]
    (str scheme "://" hostname context)))

(defn json-response [data & [status]]
  {:status  (or status 200)
   :headers {"Content-Type" "application/hal+json; charset=utf-8"}
   :body    (json/generate-string data)})
