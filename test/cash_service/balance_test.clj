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
    (is (= (balance/getItem) {:id 1 :asset 0 :debt 0}))
    (balance/setBalance 2000 3000)
    (is (= (balance/getItem) {:id 1 :asset 2000 :debt 3000}))))
