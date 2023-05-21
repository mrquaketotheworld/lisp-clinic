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
(spec/def :patient/first-name #(<= (count %) NAME-MAXLENGTH))
(spec/def :patient/last-name #(<= (count %) NAME-MAXLENGTH))
(spec/def :patient/gender #(or (= % "Male") (= % "Female")))
(spec/def :patient/birth valid-ddmmyyyy?)
(spec/def :patient/city #(<= (count %) CITY-MAXLENGTH))
(spec/def :patient/street #(<= (count %) STREET-MAXLENGTH))
(spec/def :patient/house int?)
(spec/def :patient/mid #(= (count %) MID-LENGTH))
(spec/def :patient/patient (spec/keys :req-un
                                      [:patient/first-name :patient/last-name :patient/gender
                                       :patient/birth :patient/city :patient/street :patient/house
                                       :patient/mid]))

(defn is-add-handler-valid? [patient]
  (spec/valid? :patient/patient patient))

