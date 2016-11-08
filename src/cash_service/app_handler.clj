;; File      : app_handler.clj
;; Author    : YW. Jang
;; Date      : 2016.07.02
;;
;; Copyright 2016, YW. Jang, All rights reserved.

(ns cash-service.app-handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as res]
            [clj-http.client :as http]
            [ring.middleware.json :as ring-json]
            [cheshire.core :as json]
            [clojure.java.io :as io]))

(defroutes app-routes
  (GET "/" []
    (res/redirect "/home"))

  (GET "/bob" []
    (let [response (http/get "https://www.infraware.net/ajax/boards/GetRestaurantmenuImage")]
    (res/redirect (:url (json/parse-string (response :body) true)))))

  (GET "/login" []
       (io/resource "public/login.html"))

  (GET "/home" []
       (io/resource "public/index.html"))

  (route/not-found (io/resource "404.html")))
