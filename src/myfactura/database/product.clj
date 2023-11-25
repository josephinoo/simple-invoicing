(ns myfactura.database.product
  (:require [datomic.client.api :as d]))

(defn get-alls [db]
  (d/q '[:find  (pull ?e [*])
         :where
         [?e :product/code ?code]] db))

(defn insert-one
  [conn {:keys [code name tag price tax-rate]}]
  (d/transact conn {:tx-data  [{:product/code  code
                                :product/name name
                                :product/tag tag
                                :product/price price
                                :product/tax-rate tax-rate}]}))

(defn find-by-id
  [db id]
  (-> (d/q '[:find (pull ?e [*])
             :in $ ?code
             :where
             [?e :product/code ?id]]
           db id)
      ffirst))