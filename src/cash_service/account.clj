(ns cash-service.account
  (:require [yesql.core :refer [defqueries]]
            [clojure.core.async :refer [<!]]))

(def db-spec {:classname "org.h2.Driver"
              :subprotocol "h2:file"
              :subname "./db/data"
              :user "test"
              :password ""})

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

(defn increaseBalance [id money]
  (let [balance (+ ((getAccount id) :balance) money)]
    (update-account! {:id id :balance balance})))

(defn decreaseBalance [id money]
  (let [balance (- ((getAccount id) :balance) money)]
    (update-account! {:id id :balance balance})))

(defn contain? [id]
  (not-empty (get-account-by-id {:id id})))

(defn deleteAccount [id]
  (delete-account! {:id id}))
