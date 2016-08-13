(ns cash-service.data-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cash-service.data :refer :all]
            [cheshire.core :as json]))

(defn fixture [f]
  (init)
  (f)
  (destroy))

(use-fixtures :each fixture)

(deftest data-test
  (testing "add data"
    (is (empty (getList)))
    (setItem {:item "test" :money 3000 :category 1 :account 1})
    (is (= (count (getList)) 1))
    (let [data (first (getList))]
      (is (= (data :item) "test"))
      (is (= (data :money) 3000))
      (is (= (data :category) 1))
      (is (= (data :account) 1))))

  (testing "get by category"
    (setItem {:item "test2" :money 2000 :category 2 :account 2})
    (let [data (first (getByCategory 2))]
      (is (= (data :item) "test2")))
    (let [data (first (getByCategory 1))]
      (is (= (data :item) "test"))))

  (testing "update category"
    (is (= (count (getByCategory 1)) 1))
    (updateCategory 2 1)
    (is (= (count (getByCategory 1)) 2)))

  (testing "any ?"
    (is (anyAccount? 1))
    (is (not (anyAccount? 3)))
    (is (anyCategory? 1))
    (is (not (anyCategory? 2)))))





