
(ns myfactura.core
  (:gen-class)
  (:require
   [malli.core :as m]
   [myfactura.schemas :refer [Product]]
   [malli.transform :as mt]
   [myfactura.utils.time :as t]
   [clojure.pprint :refer [print-table]]
   [myfactura.sales :as s]
   [clojure.data.json :as json]))

(def list-products (get (json/read-json (slurp "resources/products.json")) :products))

(defn print-products [list-products]
  (print-table list-products))

(defn get-product-by-code [code,list-products]
  (filter (fn [product] (= (get product :code) code)) list-products))

(defn selection-product-by-the-customer [sale list-products]
  (println "Ingrese el codigo del producto:")
  (let [code (read-line)]
    #_{:clj-kondo/ignore [:redundant-let]}
    (let [product (get-product-by-code code list-products)]
      (if (empty? product)
        (println "Producto no encontrado")
        (s/add-item sale  (m/decode Product (first product) mt/json-transformer) 1)))))

(defn selection-products
  [sale list-products]
  (let [select (selection-product-by-the-customer sale list-products)]
    (if (not-empty select)
      (selection-products select list-products))
    select))

(defn customer-input []
  (println "Ingrese el nombre del cliente:")
  (read-line))

(defn print-invoice
  [sale]
  (println "     INVOICE          ")
  (println "---------------------")
  (println "user: " (str (get sale :customer)))
  (println "---------------------")
  (println "Date:" (t/now))
  (println "---------------------")
  (println "Items:")
  (print-table (get sale :items))
  (println "-----------------------")
  (println "Tax-total :" (get sale :tax-total))
  (println  "Subtotal: " (get sale :subtotal))
  (println "Total" ":"   (get sale :total)))

(defn print-menu [list-products]
  (println "Bienenvido a My Factura")
  (print-products list-products)
  (let [customer (customer-input)
        sale (selection-products (s/new-sale) list-products)
        invoice (s/calculate-totals sale)]
    (print-invoice invoice)))
