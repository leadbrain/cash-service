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

(defn getCategoryType [id]
  ((getItem id) :type))

(defn increaseMoney [id money]
  (let [balance (+ ((getItem id) :money) money)]
    (update-category-money! {:id id :money balance})))

(defn decreaseMoney [id money]
  (let [balance (- ((getItem id) :money) money)]
    (update-category-money! {:id id :money balance})))

(defn delete [id]
  (delete-category! {:id id}))

(defn setItem [item]
  (-> (set-category<! item) first val))

(defn swap [from to]
  (update-category-money! {:id to :money (+ ((getItem from) :money) ((getItem to) :money))})
  (update-category-money! {:id from :money 0}))

(defn sameType? [one two]
  (= ((getItem one) :type) ((getItem two) :type)))
