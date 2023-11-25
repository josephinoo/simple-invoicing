(ns myfactura.ion
  (:require
   [myfactura.database.config :as configdb]
   [myfactura.database.sale :as sale]
   [myfactura.coercion :as coercion]
   [myfactura.schemas :as schemas]
   [clojure.data.json :as json]))

(defn sale-by-id
  [{:keys [input]}]
  (let   [input-decode (coercion/decode-input myfactura.schemas/InputLambda (json/read-str input))]
    (-> (configdb/db)
        (sale/find-by-id (input-decode :id))
        (json/write-str))))

