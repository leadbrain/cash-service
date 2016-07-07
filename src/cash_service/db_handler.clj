(ns cash-service.db-handler
  (:require [yesql.core :refer [defqueries]]
            [clojure.core.async :refer [<!]]))

(def db-spec {:classname "org.h2.Driver"
              :subprotocol "h2:file"
              :subname "./db/data"
              :user "test"
              :password ""})

(defqueries "cash_service/query.sql"
  {:connection db-spec})
