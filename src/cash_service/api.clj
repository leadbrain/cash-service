(ns cash-service.api
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [cash-service.data :as data]
            [cash-service.api-handler :refer :all]
            [schema.core :as s]))

(s/defschema DataResponse
  {:input_time s/Int
   :item s/Str
   :amount s/Int
   :from_type (s/enum :category :account)
   :from_id s/Int
   :to_type(s/enum :category :account)
   :to_id s/Int})

(s/defschema Result
  {:result (s/enum :OK :Error)})

(s/defschema DataRequest
  {:item s/Str
   :amount s/Int
   :from_type (s/enum :category :account)
   :from_id s/Int
   :to_type(s/enum :category :account)
   :to_id s/Int
   (s/optional-key :input_time) s/Int})

(s/defschema CategoryRequest
  {:name s/Str
   :type (s/enum :in :out)
   :money s/Int})

(s/defschema CategoryResponse
  {:id s/Int
   :name s/Str
   :type (s/enum :in :out)
   :money s/Int})

(s/defschema AccountRequest
  {:name s/Str
   :balance s/Int
   :type (s/enum :asset :debt)})

(s/defschema AccountResponse
  {:id s/Int
   :name s/Str
   :type (s/enum :asset :debt)
   :balance s/Int})

(s/defschema AccountSingleResponse
  {:result (s/enum :OK :Error)
   :name s/Str
   :type (s/enum :asset :debt)
   :balance s/Int})

(s/defschema AccountPutRequest
  {:id s/Int
   :money s/Int})

(s/defschema PutRequest
  {:id s/Int})

(s/defschema IdResult
  {:result (s/enum :OK :Error)
   :id s/Int})

(s/defschema BalanceResponse
  {:asset s/Int
   :debt s/Int})

(defroutes app
  (api
    {:swagger
     {:ui "/apidoc"
      :spec "/swagger.json"
      :data {:info {:title "Cash-service"
                    :description "Cash service Api"
                    :version "0.1"}
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
                           :return [CategoryResponse]
                           (ok (getCategory)))

                      (POST "/" []
                            :return IdResult
                            :body [data CategoryRequest]
                            (ok (setCategory data)))

                      (context "/:id" [id]
                             (DELETE "/" [id]
                                     :return Result
                                     (ok (deleteCategory id)))

                             (PUT "/" [id]
                                  :return Result
                                  :body [data PutRequest]
                                  (ok (putCategory id (data :id))))))

             (context "/account" []
                      :tags ["account"]
                      (GET "/" []
                           :return [AccountResponse]
                           (ok (getAccountList)))

                      (POST "/" request
                            :return IdResult
                            :body [data AccountRequest]
                            (ok (setAccount data)))

                      (context "/:id" [id]
                               (GET "/" [id]
                                    :return AccountSingleResponse
                                    (ok (getAccount id)))

                               (DELETE "/" [id]
                                       :return Result
                                       (ok (deleteAccount id)))

                               (PUT "/" [id]
                                    :return Result
                                    :body [data PutRequest]
                                    (ok (putAccount id (data :id))))))

             (GET "/balance/" []
                  :tags ["balance"]
                  :return BalanceResponse
                  (ok (getBalance))))))
