(ns utils.format.message)

(defn error [value]
  {:status 400
   :headers {"Content-Type" "application/json"}
   :body {:error value}})
