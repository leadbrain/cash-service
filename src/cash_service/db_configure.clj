(ns cash-service.db-configure)

(def db-spec {:classname "org.h2.Driver"
              :subprotocol "h2:file"
              :subname "./db/data"
              :user "test"
              :password ""})

(def db-spec-mysql {:classname "org.mysql.Driver"
              :subprotocol "mysql"
              :subname "//192.168.24.196/data"
              :user "root"
              :password "1234"})
