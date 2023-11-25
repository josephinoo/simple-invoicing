(ns myfactura.utils.time
  (:import [java.util Date])
  (:gen-class))

(defn now []
  (Date.))