(ns myfactura.sales_test
  (:require [clojure.test :refer [deftest is]]
            [clojure.data.json :as json]
            [myfactura.database.config :as configdb]
            [malli.core :as m]
            [myfactura.ion :refer [sale-by-id]]
            [myfactura.schemas :as schemas]
            [myfactura.database.sale :refer [find-by-id]]
            [myfactura.sales :refer
             [add-item calculate-totals sales-report remove-item]]
            [myfactura.schemas :refer
             [Sale Product]]))

(deftest test-calculate-totals-check-the-total-and-tax-value
  (let [product {:code "001" :name "Laptop" :tags #{:tag1 :tag2} :price 1200M :tax-rate 0.12}
        sale (add-item (add-item {:items []
                                  :tax-total 0M
                                  :total 0M
                                  :subtotal 0M
                                  :customer ""} product 1) product 1)
        invoice (calculate-totals sale)]
    (is (= 2688.0 (get invoice :total)))
    (is (= 288.0 (get invoice :tax-total)))
    (is (= 2400M (get invoice :subtotal)))))

(deftest test-report-history-sales
  (let [history-sales [{:number-invoice "001"
                        :date "12/06/2021"
                        :customer "Juan Perez"
                        :tax-total 10.00M
                        :subtotal 100.00M
                        :total 110.00M}

                       {:number-invoice "002"
                        :date "13/06/2021"
                        :customer "Juan Perez"
                        :tax-total 10.00M
                        :subtotal 1000.00M
                        :total  1100.00M}]]
    (is true   (m/validate Sale history-sales))
    (is 1210.00M (get (sales-report history-sales) :total))
    (is 1100.00M (get (sales-report history-sales) :subtotal))
    (is 20.00M (get (sales-report history-sales) :tax-total))))

(deftest test-remove-item-check-if-you-delete-theitem-in-the-sale
  (let [product {:code "001"  :name "Laptop" :tags #{:tag1 :tag2} :price 1200M :tax-rate 0.12}
        product2 {:code "002" :name "Laptop2" :tags #{:tag1 :tag2} :price 1200M :tax-rate 0.12}
        sale (add-item (add-item {:items []
                                  :tax-total 0M
                                  :total 0M
                                  :subtotal 0M
                                  :customer ""} product2 1) product 1)]
    (is (not= (get sale :items) (get (remove-item sale product) :items)))))

(deftest json-test
  (let [json-string "{\"id\":\"db003b18-2452-480a-a147-a9e5295b9b45\"}"
        sale  (json/write-str(find-by-id (configdb/db) #uuid "db003b18-2452-480a-a147-a9e5295b9b45"))
        ion-input (sale-by-id {:input json-string})]
    (is (= sale ion-input))))


