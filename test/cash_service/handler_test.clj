(ns cash-service.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cash-service.handler :refer :all]
            [cash-service.balance :as balance]
            [cash-service.category :as category]
            [cheshire.core :as json]
            [ring.util.anti-forgery :as anti]))

(defn fixture [f]
  (init)
  (f)
  (destroy))

(use-fixtures :each fixture)

(defn json-request [method uri body]
  (-> (mock/request method uri (json/generate-string body))
      (mock/content-type "application/json")))

(defn makeSetDataResponse [data]
  (api-and-app (json-request :post "/api/v0.1/data/" data)))

(defn check [response expect]
  (is (= (:status response) 200))
  (is (= (json/parse-string (:body response) true) expect)))

(deftest test-app
  ;; YW.Jang is responsible for this.
  (testing "main route"
    (let [response (api-and-app (mock/request :get "/login"))]
      (is (= (:status response) 200))))
      ;(is (= (:body response) "Hello"))))

  (testing "data insert"
    (let [response (api-and-app (json-request :post "/api/v0.1/account/" {:name "test", :balance 0}))]
      (is (check response {:result "OK"
                        :id 1})))
    (let [response (makeSetDataResponse {:input_time 1464787030
                                         :item "test"
                                         :money 3000
                                         :category 1
                                         :account 1})]
      (is (check response {:result "OK"})))
    (let [response (api-and-app (mock/request :get "/api/v0.1/data/"))]
      (is (check response [{:input_time 1464787030
                        :item "test"
                        :money 3000
                        :category 1
                        :account 1}])))
    (let [response (api-and-app (mock/request :get "/api/v0.1/account/1/"))]
      (is (check response {:result "OK"
                       :name "test"
                       :balance 3000})))
    (let [response (api-and-app (mock/request :get "/api/v0.1/balance/"))]
      (is (check response {:money 3000}))))

  (testing "not-found route"
    (let [response (api-and-app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))

(deftest test-balance
  (testing "set balance"
    (balance/init)
    (is (= ((balance/getItem) :money) 0))
    (balance/decreaseMoney 2000)
    (is (= ((balance/getItem) :money) -2000))))


(deftest test-balance-api
  (testing "balance"
    (let [response (api-and-app (json-request :post "/api/v0.1/account/" {:name "test", :balance 0}))]
      (is (check response {:result "OK"
                           :id 1})))

    (let [response (api-and-app (mock/request :get "/api/v0.1/balance/"))]
      (is (check response {:money 0})))

    (makeSetDataResponse {:input_time 1464787030
                          :item "test"
                          :money 3000
                          :category 1
                          :account 1})

    (let [response (api-and-app (mock/request :get "/api/v0.1/balance/"))]
      (is (check response {:money 3000})))

    (makeSetDataResponse {:input_time 1464787040
                          :item "test"
                          :money 2000
                          :category 1
                          :account 1})

    (let [response (api-and-app (mock/request :get "/api/v0.1/balance/"))]
      (is (check response {:money 5000})))

    (api-and-app (json-request :post "/api/v0.1/category/" {:name "cate1" :type :out :money 0}))

    (makeSetDataResponse {:input_time 1464787040
                          :item "test"
                          :money 2000
                          :category 2
                          :account 1})

    (let [response (api-and-app (mock/request :get "/api/v0.1/balance/"))]
      (is (check response {:money 3000})))))




(deftest test-category
  (testing "add"
    (let [response (api-and-app (json-request :post "/api/v0.1/category/" {:name "cate1" :type "out" :money 0}))]
      (is (check response {:result "OK" :id 2})))

    (let [response (api-and-app (mock/request :get "/api/v0.1/category/"))]
      (is (check response [{:id 1 :name "none" :type "in" :money 0}
                       {:id 2 :name "cate1" :type "out" :money 0}])))
    (let [response (api-and-app (json-request :post "/api/v0.1/account/" {:name "test", :balance 0}))]
      (is (check response {:result "OK"
                        :id 1}))))

  (testing "error input"
    (let [response (makeSetDataResponse {:input_time 1464787030
                                         :item "test"
                                         :money 2000
                                         :category 0
                                         :account 1})]
      (is (check response {:result "Error"}))))

  (testing "input and update"
    (let [response (makeSetDataResponse {:input_time 1464787040
                                         :item "test"
                                         :money 2000
                                         :category 2
                                         :account 1})]
      (is (check response {:result "OK"})))

    (let [response (api-and-app (mock/request :get "/api/v0.1/data/"))]
      (is (check response [{:input_time 1464787040
                            :item "test"
                            :money 2000
                            :category 2
                            :account 1}])))

    (let [response (api-and-app (mock/request :get "/api/v0.1/balance/"))]
      (is (check response {:money -2000})))

    (let [response (api-and-app (mock/request :get "/api/v0.1/category/"))]
      (is (check response [{:id 1 :name "none" :type "in" :money 0}
                       {:id 2 :name "cate1" :type "out" :money 2000}])))

    (let [response (api-and-app (mock/request :delete "/api/v0.1/category/2/"))]
      (is (check response {:result "OK"})))

    (let [response (api-and-app (mock/request :get "/api/v0.1/category/"))]
      (is (check response [{:id 1 :name "none" :type "in" :money 2000}])))

    (let [response (api-and-app (mock/request :get "/api/v0.1/balance/"))]
      (is (check response {:money 2000})))

    (let [response (api-and-app (mock/request :get "/api/v0.1/data/"))]
      (is (check response [{:input_time 1464787040
                            :item "test"
                            :money 2000
                            :category 1
                            :account 1}])))))

(deftest test-account
  (testing "delete account"
    (let [response (api-and-app (json-request :post "/api/v0.1/account/" {:name "account1",
                                                                          :balance 0}))]
      (is (check response {:result "OK"
                           :id 1})))

    (let [response (api-and-app (json-request :post "/api/v0.1/account/" {:name "account2",
                                                                          :balance 0}))]
      (is (check response {:result "OK"
                           :id 2})))

    (let [response (makeSetDataResponse {:input_time 1464787040
                                         :item "test"
                                         :money 2000
                                         :category 1
                                         :account 1})]
      (is (check response {:result "OK"})))

    (let [response (api-and-app (mock/request :delete "/api/v0.1/account/1/"))]
      (is (check response {:result "Error"})))

    (let [response (api-and-app (json-request :put "/api/v0.1/account/1/" {:id 2}))]
      (is (check response {:result "OK"})))

        (let [response (api-and-app (mock/request :get "/api/v0.1/data/"))]
      (is (check response [{:input_time 1464787040
                            :item "test"
                            :money 2000
                            :category 1
                            :account 2}])))

    (let [response (api-and-app (mock/request :delete "/api/v0.1/account/1/"))]
      (is (check response {:result "OK"})))))
