(ns myfactura.utils.translate)

(defn trans-sale
  [sale]
  {:items  (:sale/items sale)
   :tax-total (:sale/tax-total sale)
   :total (:sale/total sale)
   :subtotal (:sale/subtotal sale)
   :customer (:sale/customer sale)})

(defn trans-product
  [product]
  {:code     (:product/code product)
   :name     (:product/name product)
   :tags     (:product/tag  product)
   :price    (:product/price product)
   :tax-rate (:product/tax-rate product)})