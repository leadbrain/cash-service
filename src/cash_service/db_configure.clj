(ns cash-service.db-configure)

(def db-spec-h2 {:classname "org.h2.Driver"
              :subprotocol "h2:file"
              :subname "./db/data"
              :user "test"
              :password ""})

(def db-spec {:classname "org.mysql.Driver"
              :subprotocol "mysql"
              :subname "//localhost/data"
              :user "root"
              :password "1234"})
