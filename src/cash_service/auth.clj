;; File      : auth.clj
;; Author    : YW. Jang
;; Date      : 2016.07.02
;;
;; Copyright 2016, YW. Jang, All rights reserved.

(ns cash-service.auth
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as res] ; temporary redirect for leadbrain
            [clojure.java.io :as io]))

(defroutes auth-routes
  (GET "/login" []
       (io/resource "public/login.html"))

  (GET "/home" []
       (io/resource "public/index.html"))

  (POST "/login" [param :as request]
       ; TO DO
       (str param)
       (res/redirect "/home")) ; temporary redirect for leadbrain
       ; It does not ready for json type yet.
       ;{body :body} (slurp body))

  (POST "/signup" [param :as request]
       (str param)
       ; TO DO
       (res/redirect "/home"))) ; temporary redirect for leadbrain
       ; It does not ready for json type yet.
       ;{body :body} (slurp body)))
