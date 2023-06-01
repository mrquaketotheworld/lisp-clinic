(ns utils.format.message)

(def APPLICATION-JSON {"Content-Type" "application/json"})

(defn error [value]
  {:status 400
   :headers APPLICATION-JSON
   :body {:error value}})

(defn success
  ([] (success {:success true}))
  ([body]
   {:status 200
    :headers APPLICATION-JSON
    :body body}))
