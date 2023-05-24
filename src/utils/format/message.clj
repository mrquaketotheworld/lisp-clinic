(ns utils.format.message)

(defn error [value]
  {:status 400
   :headers {"Content-Type" "application/json"}
   :body {:error value}})

(defn success
  ([] (success {:success true}))
  ([body]
   {:status 200
    :headers {"Content-Type" "application/json"}
    :body body}))
