(ns cash-service.balance
  (:require [cash-service.db-handler :refer :all]))

(defn init []
  (create-balance-table!)
  (set-balance<! {:money 0}))

(defn destroy []
  (drop-balance-table!))

(defn getItem []
  (first (get-balance)))

(defn setItem [money]
  (let [id ((getItem) :id) balance (+ money ((getItem) :money))]
    (update-balance! {:id id :money balance})))
