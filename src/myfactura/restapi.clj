(ns myfactura.restapi
  (:require [clojure.data.json :as json]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [myfactura.sales :as s]
            [myfactura.database.product :as product]
            [myfactura.utils.translate :as t]
            [myfactura.database.sale :as sale]
            [myfactura.database.config :as configdb]
            [datomic.client.api :as d]
            [myfactura.utils.time :as time])
  (:import java.util.UUID))

(def coerce-body
  {:name ::coerce-body
   :leave
   (fn [context]
     (let [accepted         (get-in context [:request :accept :field] "application/json")
           response         (get context :response)
           body             (get response :body)
           coerced-body     (case accepted
                              "text/html"        body
                              "text/plain"       body
                              "application/edn"  (pr-str body)
                              "application/json" (json/write-str body))
           updated-response (assoc response
                                   :headers {"Content-Type" accepted}
                                   :body    coerced-body)]
       (assoc context :response updated-response)))})

(defn all-products [request]
  {:status 200 :body (product/get-alls (configdb/db))})

(defn create-sale [request]
  (let  [id (UUID/randomUUID)]
    (sale/create (configdb/conn)
                 {:number id
                  :date (time/now)
                  :items []
                  :subtotal 0M
                  :tax-total 0M
                  :total 0M
                  :customer "cliente"})
    {:status 201 :body (str id)}))

(defn get-sales [request]
  (let [db (configdb/db)]
    {:status 200 :body (sale/get-alls db)}))

(defn get-sale-by-id [request]
  (let [id (get-in request [:path-params :id])
        db
        (d/db (d/connect (d/client {:server-type :dev-local
                                    :system      "dev"
                                    :storage-dir "/Users/josdavil/storage"}) {:db-name "myfactura"}))
        sale-id (sale/find-by-id db (java.util.UUID/fromString id))]
    {:status 200 :body sale-id}))

(defn get-product-by-code [request]
  (let [code (get-in request [:path-params :code])]
    {:status 200 :body (product/find-by-id (configdb/db)(java.util.UUID/fromString code))}))

(defn add-item-to-sale [request]
  (let [conn (d/connect (d/client {:server-type :dev-local
                                   :system      "dev"
                                   :storage-dir "/Users/josdavil/storage"}) {:db-name "myfactura"})
        code (get-in request [:query-params :code])
        id  (get-in request [:path-params :id])
        product  (product/find-by-id (configdb/db) (java.util.UUID/fromString code))
        sale     (sale/find-by-id (configdb/db) (java.util.UUID/fromString id))
        sale-item-added (s/add-item sale (t/trans-product product) 1M)]
    (sale/insert-item conn sale-item-added product 1M)
    {:status 201 :body  product}))

(def routes
  (route/expand-routes
   #{["/products"                 :get  [all-products  coerce-body] :route-name :all-products]
     ["/products/:code"           :get  [get-product-by-code coerce-body] :route-name :get-product-by-code]
     ["/sales/:id/add-item"       :post [add-item-to-sale coerce-body] :route-name :add-item-in-sale]
     ["/sales/:id"                :get  [get-sale-by-id coerce-body] :route-name :get-sale-by-id]
     ["/sales"                    :post [create-sale coerce-body] :route-name :create-sale]
     ["/sales"                    :get  [get-sales coerce-body] :route-name :get-sales]}))

(def service-map
  {::http/routes routes
   ::http/type   :jetty
   ::http/port   8890})

(defn start []
  (http/start (http/create-server service-map)))
                                                                          ;; For interactive development
(defonce server (atom nil))

(defn start-dev []
  (reset! server
          (http/start (http/create-server
                       (assoc service-map
                              ::http/join? false)))))
(defn stop-dev []
  (http/stop @server))

(defn restart []
  (stop-dev)
  (start-dev))

(start-dev)
;; (restart)


