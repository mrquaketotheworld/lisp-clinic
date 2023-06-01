(ns utils.format.time
  (:import [java.time LocalDate]))

(defn parse-date [date]
  (LocalDate/parse date))
