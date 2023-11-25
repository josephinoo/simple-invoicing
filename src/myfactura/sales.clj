(ns myfactura.sales
  (:require
   [myfactura.utils.time :as t]
   [malli.core :as m]
   [malli.transform :as mt]
   [myfactura.schemas :refer [Product Sale]])
  (:gen-class))

(defn calculate-totals
  [{:keys [items] :as sale}]

  (let [item-subtotal (fn [{:keys [quantity product]}]
                        (* quantity (:price product)))
        item-tax-amount (fn [{:keys [product]}]
                          (* (:tax-rate product) (:price product)))
        subtotal  (->> items
                       (map item-subtotal)
                       (reduce +))
        tax-total (->> items
                       (map item-tax-amount)
                       (reduce +))]
    (-> sale
        (update :tax-total + (- tax-total (:tax-total sale)))
        (update :subtotal  + (- subtotal (:subtotal sale)))
        (update :total  +  (- (+ tax-total subtotal) (:total sale))))))

(defn add-item
  [sale product quantity]
  (let [item {:product product :quantity quantity}]
    (-> (update sale :items conj item)
        (calculate-totals))))

(defn update-customer-name
  [customer sale]
  (update sale :customer str customer))

(defn sales-report
  [history-sales]
  {:date (str t/now)
   :subtotal (reduce  + (map :subtotal history-sales))
   :tax-total (reduce  + (map :tax-total history-sales))
   :total (reduce  + (map :total history-sales))})

(defn remove-item
  [sale product]
  (let [items (get sale :items)]
    (assoc sale :items (filter (fn [item] (not= (get item :product) product)) items))))


