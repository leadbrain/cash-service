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
  (app (json-request :post "/api/v0.1/data/" data)))

(defn check [response expect]
  (is (= (:status response) 200))
  (is (= (json/parse-string (:body response) true) expect)))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (= (:body response) "Hello"))))

  (testing "data post"
    (let [response (makeSetDataResponse {:input_time 1464787030
                             :item "test"
                             :money 3000
                             :category 1})]
      (check response {:result "OK"})))

  (testing "data get"
    (let [response (app (mock/request :get "/api/v0.1/data/"))]
      (check response [{:input_time 1464787030
                        :item "test"
                        :money 3000
                        :category "none"}])))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))

(deftest test-balance
  (testing "set balance"
    (balance/init)
    (is (= ((balance/getItem) :money) 0))
    (balance/decreaseMoney 2000)
    (is (= ((balance/getItem) :money) -2000))))


(deftest test-balance-api
  (testing "balance"
    (let [response (app (mock/request :get "/api/v0.1/balance/"))]
      (check response {:money 0}))

    (makeSetDataResponse {:input_time 1464787030
                        :item "test"
                        :money 3000
                        :category 1})

    (let [response (app (mock/request :get "/api/v0.1/balance/"))]
      (check response {:money 3000}))

    (makeSetDataResponse {:input_time 1464787040
                        :item "test"
                        :money 2000
                        :category 1})

    (let [response (app (mock/request :get "/api/v0.1/balance/"))]
      (check response {:money 5000}))

    (app (json-request :post "/api/v0.1/category/" {:name "cate1" :type :out :money 0}))

    (makeSetDataResponse {:input_time 1464787040
                        :item "test"
                        :money 2000
                        :category 2})

    (let [response (app (mock/request :get "/api/v0.1/balance/"))]
      (check response {:money 3000}))))




(deftest test-category
  (testing "add"
    (let [response (app (json-request :post "/api/v0.1/category/" {:name "cate1" :type "out" :money 0}))]
      (check response {:result "OK" :id 2}))

    (let [response (app (mock/request :get "/api/v0.1/category/"))]
      (check response [{:id 1 :name "none" :type "in" :money 0}
                       {:id 2 :name "cate1" :type "out" :money 0}])))

  (testing "error input"
    (let [response (makeSetDataResponse {:input_time 1464787030
                                         :item "test"
                                         :money 2000
                                         :category 0})]
      (check response {:result "Error"})))

  (testing "input and update"
    (let [response (makeSetDataResponse {:input_time 1464787040
                                         :item "test"
                                         :money 2000
                                         :category 2})]
      (check response {:result "OK"}))

    (let [response (app (mock/request :get "/api/v0.1/data/"))]
      (check response [{:input_time 1464787040
                        :item "test"
                        :money 2000
                        :category "cate1"}]))

    (let [response (app (mock/request :get "/api/v0.1/balance/"))]
      (check response {:money -2000}))

    (let [response (app (mock/request :get "/api/v0.1/category/"))]
      (check response [{:id 1 :name "none" :type "in" :money 0}
                       {:id 2 :name "cate1" :type "out" :money 2000}]))

    (let [response (app (mock/request :delete "/api/v0.1/category/2/"))]
      (check response {:result "OK"}))

    (let [response (app (mock/request :get "/api/v0.1/category/"))]
      (check response [{:id 1 :name "none" :type "in" :money 2000}]))

    (let [response (app (mock/request :get "/api/v0.1/balance/"))]
      (check response {:money 2000}))

    (let [response (app (mock/request :get "/api/v0.1/data/"))]
      (check response [{:input_time 1464787040
                        :item "test"
                        :money 2000
                        :category "none"}]))))
