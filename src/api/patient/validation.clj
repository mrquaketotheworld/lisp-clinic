(ns api.patient.validation
  (:require [clojure.spec.alpha :as spec]))

(defn valid-ddmmyyyy? [date]
  (try (let [format (java.time.format.DateTimeFormatter/ofPattern "dd-MM-yyyy")]
         (java.time.LocalDate/parse date format)
         true)
       (catch java.time.format.DateTimeParseException e false)))

(def NAME-MAXLENGTH 128)
(def CITY-MAXLENGTH 128)
(def STREET-MAXLENGTH 128)
(def MID-LENGTH 12)
(spec/def ::first-name #(<= (count %) NAME-MAXLENGTH))
(spec/def ::last-name #(<= (count %) NAME-MAXLENGTH))
(spec/def ::gender #(or (= % "Male") (= % "Female")))
(spec/def ::birth valid-ddmmyyyy?)
(spec/def ::city #(<= (count %) CITY-MAXLENGTH))
(spec/def ::street #(<= (count %) STREET-MAXLENGTH))
(spec/def ::house int?)
(spec/def ::mid #(= (count %) MID-LENGTH))
(spec/def ::patient (spec/keys :req-un [::first-name ::last-name ::gender ::birth ::city ::street
                                        ::house ::mid]))

(defn is-add-handler-valid? [patient]
  (spec/valid? ::patient patient))

