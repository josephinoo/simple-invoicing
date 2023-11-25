(ns myfactura.utils.csv
  (:gen-class)
  (:require
   [clojure.data.csv :as csv]
   [clojure.java.io :as io]))

(defn csv-data->maps [csv-data]
  (map zipmap
       (->> (first csv-data)
            (map keyword)
            repeat)
       (rest csv-data)))

(defn csv-to-maps
  [csv-file]
  (let [list-productos-from-csv (with-open [reader (io/reader csv-file)]
                                  (doall
                                   (csv/read-csv reader)))]
    (csv-data->maps list-productos-from-csv)))

(defn save-sale-to-a-file
  [csv-file-name sale]
  (let [line [[(get sale :date) (get sale :customer) (get sale :tax-total) (get sale :subtotal) (get sale :total)]]]
    (with-open [writer (io/writer csv-file-name)]
      (csv/write-csv writer line))))




