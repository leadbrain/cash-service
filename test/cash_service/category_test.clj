(ns cash-service.category-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cash-service.category :as category]))

(defn fixture [f]
  (category/init)
  (f)
  (category/destroy))

(use-fixtures :each fixture)

(deftest test-category
  (testing "set category"
    (is (empty? (category/getList)))
    (category/setItem {:name "test" :money 0 :type "in"})
    (is (not-empty (category/getList)))
    (is (= (category/getItem 1) {:name "test" :money 0 :type "in"})))

  (testing "money change"
    (category/increaseMoney 1 3000)
    (is (= ((category/getItem 1) :money) 3000 ))
    (category/decreaseMoney 1 2000)
    (is (= ((category/getItem 1) :money) 1000 )))

  (testing "type"
    (category/setItem {:name "test2" :money 0 :type "out"})
    (category/setItem {:name "test3" :money 0 :type "in"})
    (is (category/sameType? 1 3))
    (is (not (category/sameType? 1 2))))

  (testing "swap"
    (category/swap 1 3)
    (is (= ((category/getItem 1) :money) 0))
    (is (= ((category/getItem 3) :money) 1000)))

  (testing "same type"
    (is (category/sameType? 1 3))
    (is (not (category/sameType? 1 2))))

  (testing "delete"
    (is (= (count (category/getList)) 3))
    (category/delete 1)
    (is (= (count (category/getList)) 2))
    (is (= (category/getItem 1) nil))))

