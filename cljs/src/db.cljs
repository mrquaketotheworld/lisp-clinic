(ns db
  (:require [cljs.spec.alpha :as spec]
            [re-frame.core :as rf]))

(spec/def ::modal-active? boolean?)
(spec/def ::loading? boolean?)
(spec/def ::patients vector?)
(spec/def ::patients-fetch-error string?)
(spec/def ::db (spec/keys :opt-un [::modal-active? ::loading? ::patients ::patients-fetch-error]))

(defn check-and-throw [a-spec db]
  (when-not (spec/valid? a-spec db)
    (throw (ex-info (str "Spec check failed: " (spec/explain-str a-spec db)) {}))))

(def check-spec-interceptor (rf/after (partial check-and-throw ::db)))
