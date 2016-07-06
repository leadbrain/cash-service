(ns cash_service.category
  (:require [cash_service.db-handler :refer :all]))

(defn destroy []
  (drop-category-table!))

(defn getList []
  (get-category))

(defn contain? [id]
  (not-empty (filter #(= id (% :id)) (getList))))

(defn init []
  (create-category-table!)
  (if-not (contain? 1)
    (set-category<! {:name "none" :type "in" :money 0})))

(defn getItem [id]
    (first (get-category-by-id {:id id})))

(defn increaseMoney [id money]
  (let [balance (+ ((getItem id) :money) money)]
    (update-category-money! {:id id :money balance})))

(defn decreaseMoney [id money]
  (let [balance (- ((getItem id) :money) money)]
    (update-category-money! {:id id :money balance})))

(defn deleteProcess [id]
  (increaseMoney 1 ((getItem id) :money))
  (delete-category! {:id id}))

(defn delete [id]
  (if-not (= id 1)
    (deleteProcess id)))

(defn setItem [item]
  (set-category<! item))
