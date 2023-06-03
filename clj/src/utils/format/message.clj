(ns utils.format.message)

(def APPLICATION-JSON {"Content-Type" "application/json"})

(defn error [value]
  {:status 400
   :headers APPLICATION-JSON
   :body {:error value}})

(defmulti success type)
(defmethod success java.lang.String [value]
  (success {:message value}))
(defmethod success :default [value]
  {:status 200
   :headers APPLICATION-JSON
   :body value})
