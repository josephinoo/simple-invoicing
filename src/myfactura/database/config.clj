(ns myfactura.database.config
  (:require [datomic.client.api :as d]))

(def db-name "myfactura")

(def get-client
  (memoize #(d/client {:server-type :dev-local
                       :system      "dev"
                       :storage-dir "/Users/josdavil/storage"})))
(def conn #(d/connect (get-client) {:db-name db-name}))

(defn db []
  (d/db (conn)))

(def product [{:db/ident         :product/id
               :db/valueType     :db.type/uuid
               :db/cardinality   :db.cardinality/one
               :db/unique        :db.unique/identity
               :db/doc "The code of the product."}

              {:db/ident       :product/name
               :db/valueType   :db.type/string
               :db/cardinality :db.cardinality/one
               :db/doc         "The name of the product."}

              {:db/ident       :product/tag
               :db/valueType   :db.type/keyword
               :db/cardinality :db.cardinality/many
               :db/doc         "The tag product"}

              {:db/ident       :product/price
               :db/valueType   :db.type/bigdec
               :db/cardinality :db.cardinality/one
               :db/doc         "Price of the product"}

              {:db/ident     :product/tax-rate
               :db/valueType  :db.type/bigdec
               :db/cardinality :db.cardinality/one
               :db/doc      "Tax rate of the product"}])

(def sale [{:db/ident :sale/id
            :db/valueType :db.type/uuid
            :db/unique :db.unique/identity
            :db/cardinality :db.cardinality/one
            :db/doc "The number of sale"}

           {:db/ident :sale/date
            :db/valueType :db.type/instant
            :db/cardinality :db.cardinality/one
            :db/doc "The date of sale"}

           {:db/ident  :sale/items
            :db/valueType :db.type/ref
            :db/isComponent true
            :db/cardinality :db.cardinality/many}

           {:db/ident  :sale/subtotal
            :db/valueType :db.type/bigdec
            :db/cardinality :db.cardinality/one}

           {:db/ident  :sale/tax-total
            :db/valueType :db.type/bigdec
            :db/cardinality :db.cardinality/one}

           {:db/ident  :sale/total
            :db/valueType :db.type/bigdec
            :db/cardinality :db.cardinality/one}

           {:db/ident  :sale/customer
            :db/valueType :db.type/string
            :db/cardinality :db.cardinality/one}
           {:db/ident  :sale/item.product
            :db/valueType :db.type/ref
            :db/cardinality :db.cardinality/one}

           {:db/ident  :sale/item.quantity
            :db/valueType :db.type/bigdec
            :db/cardinality :db.cardinality/one}])
