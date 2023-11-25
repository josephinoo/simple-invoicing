(ns myfactura.schemas
  (:gen-class))

(def Product
  [:and
   [:map
    [:code  string?]
    [:name   string?]
    [:tags [:set keyword?]]
    [:price  {:min 0} number?]
    [:tax-rate {:min 0} number?]]
   [:fn (fn [{:keys [price]}] (> price 0))]
   [:fn (fn [{:keys [tax-rate]}] (>= tax-rate 0))]])

(def Sale
  [:and
   [:map
    [:id    string?]
    [:date inst?]
    [:items [:vector Product]]
    [:subtotal {:min 0} number?]
    [:tax-total {:min 0} number?]
    [:total {:min 0} number?]
    [:customer string?]]
   [:fn (fn [{:keys [subtotal]}] (> subtotal 0))]
   [:fn (fn [{:keys [tax-total]}] (>= tax-total 0))]
   [:fn (fn [{:keys [total subtotal tax-total]}] (and (>= total 0) (= total (+ subtotal tax-total))))]])

(def InputLambda
  [:and
   [:map-of :keyword :string]])
