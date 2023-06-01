(ns utils.validation.patient.new-form
  (:require [clojure.spec.alpha :as spec]
            [utils.validation.error :as error :refer [NAME-MAXLENGTH CITY-MAXLENGTH
                                                      MID-LENGTH STREET-MAXLENGTH HOUSE-MAX]]
            [utils.format.time :as time]))

(spec/def ::firstname #(<= (count %) NAME-MAXLENGTH))
(spec/def ::lastname #(<= (count %) NAME-MAXLENGTH))
(spec/def ::gender #(or (= % "Male") (= % "Female")))
(spec/def ::birth #(time/parse-date %))
(spec/def ::city #(<= (count %) CITY-MAXLENGTH))
(spec/def ::street #(<= (count %) STREET-MAXLENGTH))
(spec/def ::house #(< % HOUSE-MAX))
(spec/def ::mid #(= (count %) MID-LENGTH))
(spec/def ::patient (spec/keys :req-un [::firstname ::lastname ::gender ::birth ::city ::street
                                        ::house ::mid]))

(defn validate [patient]
  (error/validate ::patient patient))



