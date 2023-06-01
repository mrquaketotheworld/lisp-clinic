(ns utils.validation.patient.new-form
  (:require [clojure.spec.alpha :as spec]
            [utils.validation.error :as error :refer [valid-input? MID-LENGTH]]
            [utils.format.time :as time]))

(spec/def ::firstname valid-input?)
(spec/def ::lastname valid-input?)
(spec/def ::gender #(or (= % "Male") (= % "Female")))
(spec/def ::birth #(time/parse-date %))
(spec/def ::city valid-input?)
(spec/def ::street valid-input?)
(spec/def ::house valid-input?)
(spec/def ::mid #(= (count %) MID-LENGTH))
(spec/def ::patient (spec/keys :req-un [::firstname ::lastname ::gender ::birth ::city ::street
                                        ::house ::mid]))

(defn validate [patient]
  (error/validate ::patient patient))



