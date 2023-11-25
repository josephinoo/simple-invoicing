(ns myfactura.coercion
  (:require
   [malli.core :as m]
   [malli.error :as me]
   [malli.transform :as mt]))

(defn decode-input
  [schema request]
  (let [decode (m/decode schema request mt/json-transformer)]
    (if (m/validate schema decode)
      decode
      (-> schema
          (m/explain request)
          (me/humanize)))))
