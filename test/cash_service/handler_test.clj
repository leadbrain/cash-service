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
      (is (= (:body response) "Hello World"))))

  (testing "data post"
    (let [response (app (json-request :post "/api/v0.1/data/" {:item "test" :money 3000}))]
      (is (= (:status response) 200))
      (is (= (:body response) (json/generate-string {:result "OK"})))))

  (testing "data get"
    (let [response (app (mock/request :get "/api/v0.1/data/"))]
      (is (= (:status response) 200))
      (is (= (:body response) (json/generate-string[{:item "test" :money 3000}])))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
