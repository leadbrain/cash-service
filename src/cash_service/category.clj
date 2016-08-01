(ns cash-service.category
  (:require [yesql.core :refer [defqueries]]
            [clojure.core.async :refer [<!]]
            [cash-service.db-configure :refer [db-spec]]))

(defqueries "cash_service/sql/category.sql"
  {:connection db-spec})

(defn destroy []
  (drop-category-table!))

(defn getList []
  (get-category))

(defn contain? [id]
  (not-empty (get-category-by-id {:id id})))

(defn init []
  (create-category-table!))

(defn getItem [id]
    (first (get-category-by-id {:id id})))

(defn increaseMoney [id money]
  (let [balance (+ ((getItem id) :money) money)]
    (update-category-money! {:id id :money balance})))

(defn decreaseMoney [id money]
  (let [balance (- ((getItem id) :money) money)]
    (update-category-money! {:id id :money balance})))

(defn deleteProcess [id]
  (delete-category! {:id id}))

(defn delete [id]
  (if-not (= id 1)
    (deleteProcess id)))

(defn setItem [item]
  (set-category<! item))

(defn swap [from to]
  (update-category-money! {:id to :money (+ ((getItem from) :money) ((getItem to) :money))}))

(defn sameType? [one two]
  (= ((getItem one) :type) ((getItem two) :type)))
