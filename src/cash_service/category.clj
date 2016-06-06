(ns cash-service.category
  (:require [cash-service.db-handler :refer :all]))

(defn init []
  (create-category-table!))

(defn destroy []
  (drop-category-table!))

(defn getList []
  (conj (get-category) {:id 0 :name "none" :type "in"}))

(defn contain? [id]
  (not-empty (filter #(= id (% :id)) (getList))))

(defn getItem [id]
  (if (= id 0)
    {:name "none" :type "in"}
    (first (get-category-by-id {:id id}))))

(defn delete [id]
  (delete-category! {:id id}))

(defn setItem [item]
  (set-category<! item))
