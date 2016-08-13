(ns cash-service.balance-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cash-service.balance :as balance]))

(defn fixture [f]
  (balance/init)
  (f)
  (balance/destroy))

(use-fixtures :each fixture)

(deftest test-balance
  (testing "set balance"
    (is (= ((balance/getItem) :money) 0))
    (balance/decreaseMoney 2000)
    (is (= ((balance/getItem) :money) -2000))
    (balance/increaseMoney 3000)
    (is (= ((balance/getItem) :money) 1000))))
