(ns cash-service.account-test
  (:require [clojure.test :refer :all]
            [cash-service.account :as account]))

(defn fixture [f]
  (account/init)
  (f)
  (account/destroy))

(use-fixtures :each fixture)

(deftest account-test
  (testing "add account"
    (def size (count (account/getAccounts)))
    (def id (account/addAccount {:name "woori bank" :type "asset" :balance 0}))
    (is (= (count (account/getAccounts)) (inc size)))
    (is (account/contain? id))
    (is (= (account/getAccount id) {:name "woori bank" :type "asset" :balance 0})))

  (testing "change balance"
    (def id (account/addAccount {:name "default" :type "asset" :balance 0}))
    (is (= ((account/getAccount id) :balance) 0))
    (account/increaseBalance id 1000)
    (is (= ((account/getAccount id) :balance) 1000))
    (account/decreaseBalance id 200)
    (is (= ((account/getAccount id) :balance) 800)))

  (testing "delete account"
    (def id (account/addAccount {:name "test" :type "asset" :balance 0}))
    (def size (count (account/getAccounts)))
    (account/deleteAccount id)
    (is (= (count (account/getAccounts)) (dec size)))
    (is (not (account/contain? id)))))

