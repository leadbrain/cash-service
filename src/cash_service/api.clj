(ns cash-service.api
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [cash-service.data :as data]
            [cash-service.api-handler :refer :all]
            [schema.core :as s]))

(s/defschema DataResponse
  {:input_time s/Int
   :item s/Str
   :money s/Int
   :category s/Int
   :account s/Int})

(s/defschema Result
  {:result (s/enum :OK :Error)})

(s/defschema DataRequest
  {:item s/Str
   :money s/Int
   :category s/Int
   :account s/Int
   (s/optional-key :input_time) s/Int})

(s/defschema CategoryRequest
  {:name s/Str
   :type (s/enum :in :out)
   :money s/Int})

(s/defschema AccountRequest
  {:name s/Str
   :balance s/Int})

(s/defschema PutRequest
  {:id s/Int})

(defroutes app
  (api
    {:swagger
     {:ui "/apidoc"
      :spec "/swagger.json"
      :data {:info {:title "Cash-service"
                    :description "Cash service Api"}
             :tags [{:name "data", :description "data apis"}
                    {:name "category", :description "category apis"}
                    {:name "account", :description "account apis"}
                    {:name "balance", :description "balance apis"}]}}}

    (context "/api/v0.1" []
             (context "/data" []
                      :tags ["data"]
                      (GET "/" []
                           :return [DataResponse]
                           (ok (getData)))

                      (POST "/" []
                            :return Result
                            :body [data DataRequest]
                            (ok (setDataReq data))))

             (context "/category" []
                      :tags ["category"]
                      (GET "/" []
                         (ok (getCategory)))

                      (POST "/" []
                            :body [data CategoryRequest]
                            (ok (setCategory data)))

                      (context "/:id" [id]
                             (DELETE "/" [id]
                                     (ok (deleteCategory id)))

                             (PUT "/" [id]
                                  :body [data PutRequest]
                                  (ok (putCategory id (data :id))))))

             (context "/account" []
                      :tags ["account"]
                      (GET "/" []
                           (ok (getAccountList)))

                      (POST "/" request
                            :body [data AccountRequest]
                            (ok (setAccount data)))

                      (context "/:id" [id]
                               (GET "/" [id]
                                    (ok (getAccount id)))
                               (DELETE "/" [id]
                                       (ok (deleteAccount id)))
                               (PUT "/" [id]
                                    :body [data PutRequest]
                                    (ok (putAccount id (data :id))))))

             (GET "/balance/" []
                  :tags ["balance"]
                  (ok (getBalance))))))
