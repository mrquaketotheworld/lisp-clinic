(ns utils.validation.patient.search-form
  (:require [clojure.spec.alpha :as spec]))

(def SEARCH-MAXLENGTH 128)
(def CITY-MAXLENGTH 128)
(spec/def ::gender #(or (= % "Male") (= % "Female") (= % "")))
(spec/def ::city #(<= (count %) CITY-MAXLENGTH))
(spec/def ::search #(<= (count %) SEARCH-MAXLENGTH))
(spec/def ::offset int?)
(spec/def ::limit int?)
(spec/def ::age-bottom int?)
(spec/def ::age-top int?)
(spec/def ::patient-search (spec/keys :req-un [::gender ::search ::city ::age-bottom ::age-top
                                               ::limit ::offset]))

(defn is-patient-search-form-valid? [patient-search]
  (spec/valid? ::patient-search patient-search))
