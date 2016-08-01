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
            [cash-service.balance :as balance]
            [cash-service.account :as account]))

(declare convertIdToCaterory)
(declare getDataList)
(declare setDefaultCategory)
(declare deleteCategory)
(declare setData)
(declare swapAccount)


(defroutes api-routes
  (context "/api/v0.1" []
           (context "/data" []
                    (GET "/" []
                         (res/response (getDataList)))
                    (POST "/" request
                          (let [categoryId (get-in request [:body :category])
                                accountId (get-in request [:body :account])]
                            (if (and (category/contain? categoryId) (account/contain? accountId))
                              (setData (request :body)))
                            (if (and (category/contain? categoryId) (account/contain? accountId))
                              (res/response {:result "OK"})
                              (res/response {:result "Error"})))))
           (context "/category" []
                    (GET "/" []
                         (res/response (category/getList)))
                    (POST "/" request
                          (res/response {:result "OK" :id (-> (request :body) category/setItem first val)}))
                    (DELETE "/:id/" [id]
                            (deleteCategory id)
                            (res/response {:result "OK"})))
           (context "/account" []
                    (POST "/" request
                          (res/response {:result "OK" :id (-> (request :body) account/addAccount)}))
                    (context "/:id" [id]
                             (GET "/" []
                                  (res/response (merge {:result "OK"} (account/getAccount id))))
                             (DELETE "/" []
                                     (if (not (data/anyAccount? id))
                                       (account/deleteAccount id))
                                     (if (not (data/anyAccount? id))
                                       (res/response {:result "OK"})
                                       (res/response {:result "Error"})))
                             (PUT "/" request
                                  (if (swapAccount id (get-in request [:body :id]))
                                    (res/response {:result "OK"})
                                    (res/response {:result "Error"})))))
           (GET "/balance/" []
                (res/response {:money ((balance/getItem) :money)}))

           (POST "/login/" [param :as request]
                 ; TO DO
                 (str param)
                 (res/redirect "/home")) ; temporary redirect for leadbrain
           ; It does not ready for json type yet.
           ;{body :body} (slurp body))

           (POST "/signup/" [param :as request]
                 (str param)
                 ; TO DO
                 (res/redirect "/home")))) ; temporary redirect for leadbrain
                  ; It does not ready for json type yet.
                  ;{body :body} (slurp body)))


(defn convertIdToCaterory [item]
  (assoc item :category ((category/getItem (item :category)) :name )))

(defn getDataList[]
  (data/getList))

(defn setDefaultCategory [item]
  (assoc item :category 1))

(defn deleteCategory [id]
  (data/updateByArray (map setDefaultCategory (data/getByCategory id)))
  (if (= ((category/getItem id) :type) "out")
    (balance/increaseMoney (* ((category/getItem id) :money) 2)))
  (category/delete id))

(defn increaseMoney [accountId money]
  (balance/increaseMoney money)
  (account/increaseBalance accountId money))

(defn decreaseMoney [accountId money]
  (balance/decreaseMoney money)
  (account/decreaseBalance accountId money))

(defn setData [item]
  (data/setItem item)
  (case ((category/getItem (item :category)) :type)
    "in" (increaseMoney (item :account) (item :money))
    "out" (decreaseMoney (item :account) (item :money))
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

(defn swapAccount [from to]
  (def success (and (account/contain? from) (account/contain? to)))
  (if success
    (data/updateAccount from to))
  success)
