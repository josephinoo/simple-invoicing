(ns myfactura.database.sale
  (:require [datomic.client.api :as d]
            [myfactura.database.product :as product]
            [myfactura.database.config :as configdb]))

(defn get-alls [db]
  (d/q '[:find  (pull ?e [*])
         :where
         [?e :sale/number ?code]] db))

(defn create
  [conn {:keys [number date items subtotal tax-total total customer]}]
  (d/transact conn {:tx-data  [{:sale/number number
                                :sale/date date
                                :sale/items items
                                :sale/subtotal subtotal
                                :sale/tax-total tax-total
                                :sale/total total
                                :sale/customer customer}]}))

(defn find-by-id
  [db id]
  (-> (d/q '[:find (pull ?e [[:sale/id :as :id]
                             {[:sale/items :as :items]
                              [{[:sale/item.product :as :product]
                                [[:product/code :as :code]
                                 [:product/name :as :name]
                                 [:product/tags :as :tags]
                                 [:product/price :as :price]
                                 [:product/tax-rate :as :tax-rate]]}
                               [:sale/item.quantity :as :quantity]]}
                             [:sale/subtotal :as :subtotal]
                             [:sale/tax-total :as :tax-total]
                             [:sale/total :as :total]
                             [:sale/customer :as :customer]])
             :in $ ?id
             :where
             [?e :sale/number ?number]]
           db id)
      ffirst))

(defn insert-item
  [conn sale product quantity]
  (d/transact conn {:tx-data  [{:sale/number (get sale :number)
                                :sale/items [{:sale/item.product [:product/code (:product/code product)]
                                              :sale/item.quantity quantity}]
                                :sale/subtotal  (:subtotal sale)
                                :sale/tax-total (:tax-total sale)
                                :sale/total     (:total sale)}]}))




