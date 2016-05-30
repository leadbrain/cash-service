(ns cash-service.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cash-service.handler :refer :all]
            [cheshire.core :as json]
            [ring.util.anti-forgery :as anti]))

(defn fixture [f]
  (destroy)
  (init)
  (f))

(use-fixtures :once fixture)

(defn json-request [method uri body]
  (-> (mock/request method uri (json/generate-string body))
      (mock/content-type "application/json")))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (= (:body response) "Hello"))))

  (testing "data post"
    (let [response (app (json-request :post "/api/v0.1/data/" {:item "test" :money 3000 :category 0}))]
      (is (= (:status response) 200))
      (is (= (:body response) (json/generate-string {:result "OK"})))))

  (testing "data get"
    (let [response (app (mock/request :get "/api/v0.1/data/"))]
      (is (= (:status response) 200))
      (is (= (:body response) (json/generate-string[{:item "test" :money 3000 :category 0}])))))

  (testing "category"
    (let [response (app (json-request :post "/api/v0.1/category/" {:name "cate1"}))]
      (is (= (:status response) 200))
      (is (= (:body response) (json/generate-string {:result "OK"}))))
    (let [response (app (mock/request :get "/api/v0.1/category/"))]
      (is (= (:status response) 200))
      (is (= (:body response) (json/generate-string [{:id 0 :name "none"} {:id 1 :name "cate1"}]))))
    (let [response (app (json-request :post "/api/v0.1/data/" {:item "test" :money 2000 :category 2}))]
      (is (= (:status response) 200))
      (is (= (:body response) (json/generate-string {:result "Error"}))))
    (let [response (app (json-request :post "/api/v0.1/data/" {:item "test" :money 2000 :category 1}))]
      (is (= (:status response) 200))
      (is (= (:body response) (json/generate-string {:result "OK"}))))
    (let [response (app (mock/request :get "/api/v0.1/data/"))]
      (is (= (:status response) 200))
      (is (= (:body response) (json/generate-string[{:item "test" :money 3000 :category 0}
                                                    {:item "test" :money 2000 :category 1}])))))


  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
