(ns cash-service.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cash-service.handler :refer :all]
            [cheshire.core :as json]))

(defn json-request [method uri body]
  (-> (mock/request method uri (json/generate-string body))
      (mock/content-type "application/json")))

(defn check [response expect]
  (is (= (:status response) 200))
  (is (= (json/parse-stream (java.io.InputStreamReader. (response :body)) true) expect)))

(defn requestAndCheck [api type request expect]
  (let [response (api-and-app (json-request type api request))]
    (check response expect)))

(defn fixture [f]
  (init)
  (requestAndCheck "/api/v0.1/category/" :post {:name "none" :type :in :money 0} {:result "OK" :id 1})
  (requestAndCheck "/api/v0.1/account/" :post {:name "test", :type :asset :balance 0} {:result "OK" :id 1})
  (f)
  (destroy))

(use-fixtures :each fixture)

(deftest test-app
  (testing "data insert"
    (is (requestAndCheck "/api/v0.1/data/" :post
                         {:input_time 1464787030 :item "test" :amount 3000
                          :from_type "category" :from_id 1 :to_type "account" :to_id 1}
                         {:result "OK"}))

    (is (requestAndCheck "/api/v0.1/data/" :get {}
                         [{:input_time 1464787030 :item "test" :amount 3000
                           :from_type "category" :from_id 1 :to_type "account" :to_id 1}]))

    (is (requestAndCheck "/api/v0.1/account/1/" :get {}
                         {:result "OK" :name "test" :type "asset" :balance 3000}))

    (is (requestAndCheck "/api/v0.1/balance/" :get {}
                         {:money 3000})))


  (testing "not-found route"
    (let [response (api-and-app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))

(deftest test-balance-api
  (testing "balance"
    (is (requestAndCheck "/api/v0.1/account/" :post
                      {:name "test", :type :asset :balance 0}
                      {:result "OK" :id 2}))

    (is (requestAndCheck "/api/v0.1/balance/" :get {}
                     {:money 0}))

    (is (requestAndCheck "/api/v0.1/data/" :post
                      {:input_time 1464787030 :item "test" :amount 3000
                       :from_type "category" :from_id 1 :to_type "account" :to_id 1}
                      {:result "OK"}))

    (is (requestAndCheck "/api/v0.1/balance/" :get {}
                     {:money 3000}))

    (is (requestAndCheck "/api/v0.1/data/" :post
                      {:input_time 1464787040 :item "test" :amount 2000
                       :from_type "category" :from_id 1 :to_type "account" :to_id 1}
                      {:result "OK"}))

    (is (requestAndCheck "/api/v0.1/balance/" :get {}
                     {:money 5000}))

    (is (requestAndCheck "/api/v0.1/category/" :post
                      {:name "cate1" :type :out :money 0}
                      {:result "OK" :id 2}))

    (is (requestAndCheck "/api/v0.1/data/" :post
                      {:input_time 1464787040 :item "test" :amount 2000
                       :from_type "account" :from_id 1 :to_type "category" :to_id 2}
                      {:result "OK"}))

    (is (requestAndCheck "/api/v0.1/balance/" :get {}
                     {:money 3000}))))

(deftest test-category
  (testing "add"
    (is (requestAndCheck "/api/v0.1/category/" :post
                      {:name "cate1" :type :out :money 0}
                      {:result "OK" :id 2}))

    (is (requestAndCheck "/api/v0.1/category/" :get {}
                     [{:id 1 :name "none" :type "in" :money 0}
                      {:id 2 :name "cate1" :type "out" :money 0}])))

  (testing "error input"
    (is (requestAndCheck "/api/v0.1/data/" :post
                      {:input_time 1464787030 :item "test" :amount 2000
                       :from_type "account" :from_id 0 :to_type "category" :to_id 1}
                      {:result "Error"})))

  (testing "input and update"
    (is (requestAndCheck "/api/v0.1/data/" :post
                      {:input_time 1464787040 :item "test" :amount 2000
                       :from_type "account" :from_id 1 :to_type "category" :to_id 2}
                      {:result "OK"}))

    (is (requestAndCheck "/api/v0.1/data/" :get {}
                     [{:input_time 1464787040 :item "test" :amount 2000
                       :from_type "account" :from_id 1 :to_type "category" :to_id 2}]))

    (is (requestAndCheck "/api/v0.1/balance/" :get {}
                     {:money -2000}))

    (is (requestAndCheck "/api/v0.1/category/" :get {}
                     [{:id 1 :name "none" :type "in" :money 0}
                      {:id 2 :name "cate1" :type "out" :money 2000}]))

    (is (requestAndCheck "/api/v0.1/category/2/" :delete {}
                        {:result "Error"}))

    (is (requestAndCheck "/api/v0.1/category/" :post
                      {:name "cate2" :type :out :money 0}
                      {:result "OK" :id 3}))

    (is (requestAndCheck "/api/v0.1/category/2/" :put
                     {:id 3}
                     {:result "OK"}))

    (is (requestAndCheck "/api/v0.1/category/2/" :delete {}
                        {:result "OK"}))

    (is (requestAndCheck "/api/v0.1/category/" :get {}
                     [{:id 1 :name "none" :type "in" :money 0},
                      {:id 3 :name "cate2" :type "out" :money 2000}]))

    (is (requestAndCheck "/api/v0.1/data/" :get {}
                     [{:input_time 1464787040 :item "test" :amount 2000
                       :from_type"account" :from_id 1 :to_type "category" :to_id 3}]))))

(deftest test-account
  (testing "delete account"
    (is (requestAndCheck "/api/v0.1/category/" :post
                         {:name "cate1" :type :in :money 0}
                         {:result "OK" :id 2}))

    (is (requestAndCheck "/api/v0.1/account/" :post
                         {:name "account2", :type :asset :balance 0}
                         {:result "OK" :id 2}))

    (is (requestAndCheck "/api/v0.1/data/" :post
                         {:input_time 1464787040 :item "test" :amount 2000
                          :from_type "category" :from_id 1 :to_type "account" :to_id 1}
                         {:result "OK"}))

    (is (requestAndCheck "/api/v0.1/account/1/" :delete {}
                         {:result "Error"}))

    (is (requestAndCheck "/api/v0.1/account/1/" :put
                         {:id 2}
                         {:result "OK"}))

    (is (requestAndCheck "/api/v0.1/data/" :get {}
                         [{:input_time 1464787040 :item "test" :amount 2000
                           :from_type "category" :from_id 1 :to_type "account" :to_id 2}]))

    (is (requestAndCheck "/api/v0.1/account/1/" :delete {}
                         {:result "OK"}))

    (is (requestAndCheck "/api/v0.1/account/2/" :get {}
                         {:result "OK" :name "account2" :type "asset" :balance 2000})))

  (testing "debt"
    (is (requestAndCheck "/api/v0.1/category/" :post
                         {:name "cate out" :type "out" :money 0}
                         {:result "OK" :id 3}))

    (is (requestAndCheck "/api/v0.1/account/" :post
                         {:name "account debt" :type "debt" :balance 0}
                         {:result "OK" :id 3}))

    (is (requestAndCheck "/api/v0.1/data/" :post
                         {:input_time 1464787040 :item "test" :amount 3000
                          :from_type "account" :from_id 3 :to_type "category" :to_id 3}
                         {:result "OK"}))

    (is (requestAndCheck "/api/v0.1/account/3/" :get {}
                         {:result "OK" :name "account debt" :type "debt" :balance 3000}))

    (is (requestAndCheck "/api/v0.1/balance/" :get {}
                         {:money -1000}))

    (is (requestAndCheck "/api/v0.1/data/" :post
                         {:input_time 1464787040 :item "test" :amount 2000
                          :from_type "category" :from_id 2 :to_type "account" :to_id 3}
                         {:result "OK"}))

    (is (requestAndCheck "/api/v0.1/account/3/" :get {}
                         {:result "OK" :name "account debt" :type "debt" :balance 1000}))

    (is (requestAndCheck "/api/v0.1/balance/" :get {}
                         {:money 1000})))

  (testing "transfer"
    (is (requestAndCheck "/api/v0.1/account/" :post
                         {:name "account trans" :type "asset" :balance 0}
                         {:result "OK" :id 4}))

    (is (requestAndCheck "/api/v0.1/data/" :post
                         {:input_time 1464787040 :item "test" :amount 2000
                          :from_type "account" :from_id 2 :to_type "account" :to_id 4}
                         {:result "OK"}))

    (is (requestAndCheck "/api/v0.1/account/2/" :get {}
                         {:result "OK" :name "account2" :type "asset" :balance 0}))

    (is (requestAndCheck "/api/v0.1/account/4/" :get {}
                         {:result "OK" :name "account trans" :type "asset" :balance 2000}))

    (is (requestAndCheck "/api/v0.1/balance/" :get {}
                         {:money 1000}))

    (is (requestAndCheck "/api/v0.1/data/" :post
                         {:input_time 1464787040 :item "test" :amount 1000
                          :from_type "account" :from_id 3 :to_type "account" :to_id 2}
                         {:result "OK"}))

    (is (requestAndCheck "/api/v0.1/account/2/" :get {}
                         {:result "OK" :name "account2" :type "asset" :balance 1000}))

    (is (requestAndCheck "/api/v0.1/account/3/" :get {}
                         {:result "OK" :name "account debt" :type "debt" :balance 2000}))

    (is (requestAndCheck "/api/v0.1/balance/" :get {}
                         {:money 1000}))

    (is (requestAndCheck "/api/v0.1/data/" :post
                         {:input_time 1464787040 :item "test" :amount 2000
                          :from_type "account" :from_id 4 :to_type "account" :to_id 3}
                         {:result "OK"}))

    (is (requestAndCheck "/api/v0.1/account/4/" :get {}
                         {:result "OK" :name "account trans" :type "asset" :balance 0}))

    (is (requestAndCheck "/api/v0.1/account/3/" :get {}
                         {:result "OK" :name "account debt" :type "debt" :balance 0}))

    (is (requestAndCheck "/api/v0.1/balance/" :get {}
                         {:money 1000}))))
