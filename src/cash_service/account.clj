(ns cash-service.account
  (:require [yesql.core :refer [defqueries]]
            [clojure.core.async :refer [<!]]
            [cash-service.db-configure :refer [db-spec]]))

(defqueries "cash_service/sql/account.sql"
  {:connection db-spec})

(defn init []
  (create-table!))

(defn destroy []
  (drop-table!))

(defn addAccount [account]
  (-> (insert-account<! account) first val))

(defn getAccounts []
  (get-accounts))

(defn getAccount [id]
  (first (get-account {:id id})))

(defn getAccountType [id]
  ((getAccount id) :type))

(defn increaseBalance [id money]
  (let [balance (+ ((getAccount id) :balance) money)]
    (update-account! {:id id :balance balance})))

(defn decreaseBalance [id money]
  (let [balance (- ((getAccount id) :balance) money)]
    (update-account! {:id id :balance balance})))

(defn contain? [id]
  (not-empty (get-account {:id id})))

(defn swap [from to]
  (increaseBalance to ((getAccount from) :balance)))

(defn deleteAccount [id]
  (delete-account! {:id id}))
