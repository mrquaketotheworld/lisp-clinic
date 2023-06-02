(ns db
  (:require [cljs.spec.alpha :as spec]
            [re-frame.core :as rf]))

(spec/def ::firstname string?)
(spec/def ::lastname string?)
(spec/def ::gender string?)
(spec/def ::birth string?)
(spec/def ::city string?)
(spec/def ::street string?)
(spec/def ::house string?)
(spec/def ::mid string?)
(spec/def ::patient (spec/keys :opt-un [::firstname ::lastname ::gender ::birth ::city ::street
                                        ::house ::mid]))

(spec/def ::modal-active? boolean?)
(spec/def ::patients seqable?)
(spec/def ::ajax-error string?)
(spec/def ::db (spec/keys :opt-un [::patient ::modal-active? ::patients ::ajax-error]))

(defn check-and-throw [a-spec db]
  (when-not (spec/valid? a-spec db)
    (throw (ex-info (str "Spec check failed: " (spec/explain-str a-spec db)) {}))))

(def check-spec-interceptor (rf/after (partial check-and-throw ::db)))
