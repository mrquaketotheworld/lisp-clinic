(ns utils.format.time)

(defn get-iso-date [date]
  (-> date
      .toISOString
      (.split "T")
      first))

(defn current-date []
  (get-iso-date (js/Date.)))

(defn current-date-minus-100 []
  (let [current-date (js/Date.)]
    (-> current-date
        (.setFullYear (- (.getFullYear current-date) 100)))
    (get-iso-date current-date)))
