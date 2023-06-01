(ns utils.format.time
  (:import [java.time LocalDate]))

(defn parse-date [date]
  (try
    (LocalDate/parse date)
    (catch Exception e)))
