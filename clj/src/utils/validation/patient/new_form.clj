(ns utils.validation.patient.new-form
  (:require [clojure.spec.alpha :as spec]
            [utils.format.time :as time]))

(def NAME-MAXLENGTH 128)
(def CITY-MAXLENGTH 128)
(def STREET-MAXLENGTH 128)
(def MID-LENGTH 12)
(spec/def ::firstname #(<= (count %) NAME-MAXLENGTH))
(spec/def ::lastname #(<= (count %) NAME-MAXLENGTH))
(spec/def ::gender #(or (= % "Male") (= % "Female")))
(spec/def ::birth #(time/parse-date %))
(spec/def ::city #(<= (count %) CITY-MAXLENGTH))
(spec/def ::street #(<= (count %) STREET-MAXLENGTH))
(spec/def ::house int?)
(spec/def ::mid #(= (count %) MID-LENGTH))
(spec/def ::patient (spec/keys :req-un [::firstname ::lastname ::gender ::birth ::city ::street
                                        ::house ::mid]))

(defn max-length [max]
  (str "Max length is: " max))

(def errors
  {:firstname (max-length NAME-MAXLENGTH)
   :lastname (max-length NAME-MAXLENGTH)
   :gender "Can be Male or Female"
   :birth "Format should be yyyy-mm-dd"
   :city (max-length CITY-MAXLENGTH)
   :street (max-length STREET-MAXLENGTH)
   :house "Should be a number"
   :mid (str "Length should be: " MID-LENGTH)})

(defn validate [patient]
  (let [explained-data (::spec/problems (spec/explain-data ::patient patient))
        error (reduce (fn [acc problem]
                        (let [key-error (first (:path problem))]
                          (if key-error
                            (assoc acc key-error (key-error errors))
                            (assoc acc :some-of-keys-are-not-valid (keys (:val problem)))))) {}
                      explained-data)]
    (when (seq error) error)))



