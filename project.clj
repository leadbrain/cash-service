(defproject cash-service "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [compojure "1.4.0"]
                 [org.clojure/core.async "0.2.374"]
                 [ring/ring-defaults "0.1.5"]
                 [ring/ring-json "0.4.0"]
                 [cheshire "5.5.0"]
                 [yesql "0.5.3"]
                 [com.h2database/h2 "1.4.191"]
                 [clj-http "3.0.1"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:init cash_service.handler/init
         :handler cash_service.handler/api-and-app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
