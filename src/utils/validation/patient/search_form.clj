(ns utils.validation.patient.search-form
  (:require [clojure.spec.alpha :as spec]))

(def NAME-MAXLENGTH 128)
(def CITY-MAXLENGTH 128)
(def MID-LENGTH 12)
(spec/def ::first-name #(<= (count %) NAME-MAXLENGTH))
(spec/def ::last-name #(<= (count %) NAME-MAXLENGTH))
(spec/def ::gender #(or (= % "Male") (= % "Female") (= % "")))
(spec/def ::city #(<= (count %) CITY-MAXLENGTH))
(spec/def ::mid #(<= (count %) MID-LENGTH))

(spec/def ::age-bottom int?)
(spec/def ::age-top int?)
(spec/def ::patient-search (spec/keys :req-un [::first-name ::last-name ::gender ::mid ::city
                                               ::age-bottom ::age-top]))

(defn is-patient-search-form-valid? [patient-search]
  (spec/valid? ::patient-search patient-search))
