(ns utils.validation.patient.new-form
  (:require [clojure.spec.alpha :as spec])
  (:import [java.time LocalDate]))

(def NAME-MAXLENGTH 128)
(def CITY-MAXLENGTH 128)
(def STREET-MAXLENGTH 128)
(def MID-LENGTH 12)
(spec/def ::first-name #(<= (count %) NAME-MAXLENGTH))
(spec/def ::last-name #(<= (count %) NAME-MAXLENGTH))
(spec/def ::gender #(or (= % "Male") (= % "Female")))
(spec/def ::birth-day #(and (>= % 1) (<= % 31)))
(spec/def ::birth-month #(and (>= % 1) (<= % 12)))
(spec/def ::birth-year #(and (>= % 1900) (<= % (.getYear (LocalDate/now)))))
(spec/def ::city #(<= (count %) CITY-MAXLENGTH))
(spec/def ::street #(<= (count %) STREET-MAXLENGTH))
(spec/def ::house int?)
(spec/def ::mid #(= (count %) MID-LENGTH))
(spec/def ::patient (spec/keys :req-un [::first-name ::last-name ::gender ::birth-day ::birth-month
                                        ::birth-year ::city ::street ::house ::mid]))

(defn is-patient-form-valid? [patient]
  (spec/valid? ::patient patient))

