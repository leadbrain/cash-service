(ns cash-service.balance
  (:require [yesql.core :refer [defqueries]]
            [clojure.core.async :refer [<!]]
            [cash-service.db-configure :refer [db-spec]]))

(defqueries "cash_service/sql/balance.sql"
  {:connection db-spec})

(defn init []
  (create-balance-table!)
  (set-balance<! {:asset 0 :debt 0}))

(defn destroy []
  (drop-balance-table!))

(defn getItem []
  (first (get-balance)))

(defn setBalance [asset, debt]
  (let [id ((getItem) :id)]
    (update-asset! {:id id :asset asset})
    (update-debt! {:id id :debt debt})))
