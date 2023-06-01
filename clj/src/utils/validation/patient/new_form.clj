(ns utils.validation.patient.new-form
  (:require [clojure.spec.alpha :as spec]
            [utils.format.time :as time]))

(def NAME-MAXLENGTH 128)
(def CITY-MAXLENGTH 128)
(def STREET-MAXLENGTH 128)
(def MID-LENGTH 12)
(spec/def ::first-name #(<= (count %) NAME-MAXLENGTH))
(spec/def ::last-name #(<= (count %) NAME-MAXLENGTH))
(spec/def ::gender #(or (= % "Male") (= % "Female")))
(spec/def ::birth #(time/parse-date %))
(spec/def ::city #(<= (count %) CITY-MAXLENGTH))
(spec/def ::street #(<= (count %) STREET-MAXLENGTH))
(spec/def ::house int?)
(spec/def ::mid #(= (count %) MID-LENGTH))
(spec/def ::patient (spec/keys :req-un [::first-name ::last-name ::gender ::birth ::city ::street
                                        ::house ::mid]))

(defn is-patient-form-valid? [patient]
  (spec/valid? ::patient patient))
