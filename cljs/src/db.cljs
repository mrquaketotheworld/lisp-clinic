(ns db (:require [cljs.spec.alpha :as spec]
                 [re-frame.core :as rf]))

(spec/def ::firstname string?)
(spec/def ::lastname string?)
(spec/def ::gender string?)
(spec/def ::birth string?)
(spec/def ::city string?)
(spec/def ::street string?)
(spec/def ::house string?)
(spec/def ::mid string?)
(spec/def ::patient (spec/keys :req-un [::firstname ::lastname ::gender ::birth ::city ::street
                                        ::house ::mid]))

(spec/def ::age-bottom string?)
(spec/def ::age-top string?)
(spec/def ::search string?)
(spec/def ::offset string?)
(spec/def ::limit string?)
(spec/def ::filter-search (spec/keys :req-un [::age-bottom ::age-top ::gender ::city ::search
                                              ::offset ::limit]))

(spec/def ::cities (spec/coll-of string?))

(spec/def ::modal-active? boolean?)
(spec/def ::ajax-success string?)
(spec/def ::ajax-error string?)
(spec/def ::patient-form-mode #(or (= % "add") (= % "edit")))
(spec/def ::patients (spec/coll-of ::patient))
(spec/def ::db (spec/keys :req-un [::patients ::patient ::modal-active? ::patient-form-mode
                                   ::filter-search ::cities]
                          :opt-un [::ajax-success ::ajax-error]))

(defn check-and-throw [a-spec db]
  (when-not (spec/valid? a-spec db)
    (throw (ex-info (str "Spec check failed: " (spec/explain-str a-spec db)) {}))))

(def check-spec-interceptor (rf/after (partial check-and-throw ::db)))
