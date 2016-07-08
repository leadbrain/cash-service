(ns cash-service.balance
  (:require [cash-service.db-handler :refer :all]))

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
