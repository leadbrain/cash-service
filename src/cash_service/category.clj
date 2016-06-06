(ns cash-service.category
  (:require [cash-service.db-handler :refer :all]))

(defn init []
  (create-category-table!))

(defn destroy []
  (drop-category-table!))

(defn getList []
  (conj (get-category) {:id 0 :name "none"}))

(defn contain? [id]
  (not-empty (filter #(= id (% :id)) (getList))))

(defn getName [id]
  ((first (filter #(= id (% :id)) (getList))) :name))

(defn delete [id]
  (delete-category! {:id id}))

(defn setItem [item]
  (set-category<! item))
