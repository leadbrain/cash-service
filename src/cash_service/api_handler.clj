;; File      : api_handler.clj
;; Author    : YW. Jang
;; Date      : 2016.07.04
;;
;; Copyright 2016, YW. Jang, All rights reserved.

(ns cash-service.api-handler
  (:require [compojure.core :refer :all]
            [cash-service.data :as data]
            [cash-service.category :as category]
            [cash-service.balance :as balance]
            [cash-service.account :as account]))

(declare setData)
(declare swapAccount)
(declare swapCategory)

(defn getData []
  (data/getList))

(defn setDataReq [data]
  (if (and (category/contain? (data :category)) (account/contain? (data :account)))
    (setData data ))
  (if (and (category/contain? (data :category)) (account/contain? (data :account)))
    {:result "OK"}
    {:result "Error"}))

(defn getCategory []
  (category/getList))

(defn setCategory [data]
  {:result "OK" :id (-> (assoc data :type (name (data :type))) category/setItem first val)})

(defn deleteCategory [id]
  (if (not (data/anyCategory? id))
    (category/delete id))
  (if (not (data/anyCategory? id))
    {:result "OK"}
    {:result "Error"}))

(defn putCategory [old new]
  (if (swapCategory old new)
    {:result "OK"}
    {:result "Error"}))

(defn getAccountList []
  (account/getAccounts))

(defn setAccount [data]
  {:result "OK" :id (account/addAccount data)})

(defn getAccount [id]
  (merge {:result "OK"} (account/getAccount id)))

(defn deleteAccount [id]
  (if (not (data/anyAccount? id))
    (account/deleteAccount id))
  (if (not (data/anyAccount? id))
    {:result "OK"}
    {:result "Error"}))

(defn putAccount [old new]
  (if (swapAccount old new)
    {:result "OK"}
    {:result "Error"}))

(defn getBalance []
  {:money ((balance/getItem) :money)})


(defn- increaseMoney [accountId money]
  (balance/increaseMoney money)
  (account/increaseBalance accountId money))

(defn- decreaseMoney [accountId money]
  (balance/decreaseMoney money)
  (account/decreaseBalance accountId money))

(defn- setData [item]
  (data/setItem item)
  (case ((category/getItem (item :category)) :type)
    "in" (increaseMoney (item :account) (item :money))
    "out" (decreaseMoney (item :account) (item :money))
    "default" (print (item :type)))
  (category/increaseMoney (item :category) (item :money)))

(defn- get-base-uri [request]
  (let [scheme (name (:scheme request))
      context (:context request)
      hostname (get (:headers request) "host")]
    (str scheme "://" hostname context)))

(defn- updateAccount [from to]
  (data/updateAccount from to)
  (account/swap from to))

(defn- swapAccount [from to]
  (def success (and (account/contain? from) (account/contain? to)))
  (if success
    (updateAccount from to))
  success)

(defn- updateCategory [from to]
  (data/updateCategory from to)
  (category/swap from to))

(defn- swapCategory [from to]
  (def success (and (category/contain? from) (category/contain? to) (category/sameType? from to)))
  (if success
    (updateCategory from to))
  success)
