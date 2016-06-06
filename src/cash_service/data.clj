(ns cash-service.data
  (:require [cash-service.db-handler :refer :all]))

(defn init []
  (create-data-table!))

(defn destroy []
  (drop-table!))

(defn currentTime []
  (quot (System/currentTimeMillis) 1000))

(defn validate [data]
  (if (get-in data [:input_time])
    (assoc data :create_time (currentTime))
    (assoc data :input_time (currentTime) :create_time (currentTime))))

(defn setItem [data]
  (set-data<! (validate data) ))

(defn getList []
  (get-data))

(defn updateByArray [array]
  (doseq [item array] (update-data-category! item)))

(defn getByCategory [id]
  (get-data-by-category {:category id}))
