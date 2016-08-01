(ns cash-service.balance
  (:require [yesql.core :refer [defqueries]]
            [clojure.core.async :refer [<!]]
            [cash-service.db-configure :refer [db-spec]]))

(defqueries "cash_service/sql/balance.sql"
  {:connection db-spec})

(defn init []
  (create-balance-table!)
  (set-balance<! {:money 0}))

(defn destroy []
  (drop-balance-table!))

(defn getItem []
  (first (get-balance)))

(defn increaseMoney [money]
  (let [id ((getItem) :id)
        balance (+ ((getItem) :money) money)]
    (update-balance! {:id id :money balance})))

(defn decreaseMoney [money]
  (let [id ((getItem) :id)
        balance (- ((getItem) :money) money)]
    (update-balance! {:id id :money balance})))
