(ns cash-service.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as ring-json]
            [ring.util.response :as res]
            [cheshire.core :as json]
            [cash-service.db-handler :refer :all]
            [clj-http.client :as http]))

(defn init []
  (create-data-table!)
  (create-category-table!))

(defn destroy []
  (drop-table!)
  (drop-category-table!))

(defn get-category-val []
  (conj (get-category) {:id 0 :name "none"}))

(defn get-category-list []
  (conj (get-category) {:id 0 :name "none"}))

(defn contain-category? [id]
  (not-empty (filter #(= id (% :id)) (get-category-list))))

(defn get-category-name [id]
  ((first (filter #(= id (% :id)) (get-category-list))) :name))

(defn convert-id-to-caterory [item]
  (assoc item :category (get-category-name (item :category))))

(defn get-data-list []
  (map convert-id-to-caterory (get-data)))

(defn currentTime []
  (quot (System/currentTimeMillis) 1000))

(defn validate [data]
  (if (get-in data [:input_time])
    (assoc data :create_time (currentTime))
    (assoc data :input_time (currentTime) :create_time (currentTime))))

(defn setData [data]
  (set-data<! (validate data) ))

(defn setDefaultCategory [item]
  (assoc item :category 0))

(defn updateDataArray [array]
  (doseq [item array] (update-data-category! item)))

(defn deleteCategory [id]
  (updateDataArray (map #(assoc % :category 0) (get-data-by-category {:category id})))
  (delete-category! {:id id}))

(defroutes app-routes
  (GET "/" []
       "Hello")

  (GET "/bob" []
       (let [response (http/get "https://www.infraware.net/ajax/boards/GetRestaurantmenuImage")]
            (res/redirect (:url (json/parse-string (:body response) true)))))

  (POST "/api/v0.1/data/" request
        (let [id (get-in request [:body :category])]
          (if (contain-category? id)
            (setData (get-in request [:body])))
          (if (contain-category? id)
            (res/response {:result "OK"})
            (res/response {:result "Error"}))))

  (GET "/api/v0.1/data/" []
       (res/response (get-data-list)))

  (POST "/api/v0.1/category/" request
        (res/response {:result "OK" :id (-> (get-in request [:body]) set-category<! first val)}))

  (GET "/api/v0.1/category/" []
       (res/response (get-category-val)))

  (DELETE "/api/v0.1/category/:id/" [id]
          (deleteCategory id)
          (res/response {:result "OK"}))

  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (ring-json/wrap-json-body {:keywords? true})
      (ring-json/wrap-json-response)
      (wrap-defaults (assoc site-defaults :security {:anti-forgery false}))))
