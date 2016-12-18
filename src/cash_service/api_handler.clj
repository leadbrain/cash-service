(ns cash-service.api-handler
  (:require [compojure.core :refer :all]
            [cash-service.data :as data]
            [cash-service.category :as category]
            [cash-service.balance :as balance]
            [cash-service.account :as account]))

(declare setData)
(declare swapAccount)
(declare swapCategory)

(defn getData []
  (data/getList))

(defn validateData? [type id]
  (case type
    "category" (category/contain? id)
    "account" (account/contain? id)
    "default" false))

(defn setDataReq [data]
  (if (and (validateData? (name (data :from_type)) (data :from_id))
           (validateData? (name (data :to_type)) (data :to_id)))
    (setData (assoc data
               :from_type (name (data :from_type))
               :to_type (name (data :to_type)))))
  (if (and (validateData? (name (data :from_type)) (data :from_id))
           (validateData? (name (data :to_type)) (data :to_id)))
    {:result "OK"}
    {:result "Error"}))

(defn getCategory []
  (category/getList))

(defn setCategory [data]
  {:result "OK" :id (category/setItem (assoc data :type (name (data :type))))})

(defn deleteCategory [id]
  (if (not (data/anyCategory? id))
    (category/delete id))
  (if (not (data/anyCategory? id))
    {:result "OK"}
    {:result "Error"}))

(defn putCategory [old new]
  (if (swapCategory old new)
    {:result "OK"}
    {:result "Error"}))

(defn getAccountList []
  (account/getAccounts))

(defn setAccount [data]
  {:result "OK" :id (account/addAccount (assoc data :type (name (data :type))))})

(defn getAccount [id]
  (merge {:result "OK"} (account/getAccount id)))

(defn deleteAccount [id]
  (if (not (data/anyAccount? id))
    (account/deleteAccount id))
  (if (not (data/anyAccount? id))
    {:result "OK"}
    {:result "Error"}))

(defn putAccount [old new]
  (if (swapAccount old new)
    {:result "OK"}
    {:result "Error"}))

(defn getBalance []
  (dissoc (balance/getItem) :id))


(defn- increaseMoney [accountId money]
  (if (= (account/getAccountType accountId) "debt")
    (account/decreaseBalance accountId money)
    (account/increaseBalance accountId money)))


(defn- decreaseMoney [accountId money]
  (if (= (account/getAccountType accountId) "debt")
    (account/increaseBalance accountId money)
    (account/decreaseBalance accountId money)))

(defn- updateBalance []
  (balance/setBalance (apply + (map #(% :balance) (into [] (filter #(= (% :type) "asset") (account/getAccounts)))))
                      (apply + (map #(% :balance) (into [] (filter #(= (% :type) "debt") (account/getAccounts)))))))

(defn- spend? [data]
  (if (and (= (data :from_type) "account") (= (data :to_type) "category"))
    (or (and (= (account/getAccountType (data :from_id)) "asset")
             (= (category/getCategoryType (data :to_id)) "out"))
        (and (= (account/getAccountType (data :from_id)) "debt")
             (= (category/getCategoryType (data :to_id)) "out")))
    false))

(defn- spend [data]
  (decreaseMoney (data :from_id) (data :amount))
  (updateBalance)
  (category/increaseMoney (data :to_id) (data :amount)))

(defn- income? [data]
  (if (and (= (data :from_type) "category") (= (data :to_type) "account"))
    (or (and (= (category/getCategoryType (data :from_id)) "in")
             (= (account/getAccountType (data :to_id)) "asset"))
        (and (= (category/getCategoryType (data :from_id)) "in")
             (= (account/getAccountType (data :to_id)) "debt")))
    false))

(defn- income [data]
  (increaseMoney (data :to_id) (data :amount))
  (updateBalance)
  (category/increaseMoney (data :from_id) (data :amount)))

(defn- transfer? [data]
  (and (= (data :from_type) "account") (= (data :to_type) "account")))

(defn- transfer [data]
  (increaseMoney (data :to_id) (data :amount))
  (decreaseMoney (data :from_id) (data :amount))
  (updateBalance))



(defn- setData [item]
  (data/setItem item)
  (if (spend? item)
    (spend item))
  (if (income? item)
    (income item))
  (if (transfer? item)
    (transfer item)))

(defn- updateAccount [from to]
  (data/updateAccount from to)
  (account/swap from to))

(defn- swapAccount [from to]
  (let [success (and (account/contain? from) (account/contain? to))]
    (if success
      (updateAccount from to))
    success))

(defn- updateCategory [from to]
  (data/updateCategory from to)
  (category/swap from to))

(defn- swapCategory [from to]
  (let [success (and (category/contain? from)
                    (category/contain? to)
                    (category/sameType? from to))]
    (if success
      (updateCategory from to))
    success))
