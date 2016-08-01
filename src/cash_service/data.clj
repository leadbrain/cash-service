(ns cash-service.data
  (:require [yesql.core :refer [defqueries]]
            [clojure.core.async :refer [<!]]
            [cash-service.db-configure :refer [db-spec]]))

(defqueries "cash_service/sql/data.sql"
  {:connection db-spec})

(defn init []
  (create-data-table!))

(defn destroy []
  (drop-data-table!))

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

(defn updateAccount [from to]
  (update-account! {:from from :to to}))

(defn anyAccount? [id]
  (not-empty (get-data-by-account {:account id})))
