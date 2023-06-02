(ns dispatches
  (:require [re-frame.core :as rf]))

(defn hide-modal []
  (rf/dispatch [:modal-active? false]))

(defn show-modal []
  (rf/dispatch [:modal-active? true]))

(defn search-patients []
  (rf/dispatch [:search-patients]))

(defn delete-patient [mid]
  (rf/dispatch [:delete-patient mid]))

(defn on-patient-form-change [field-key value]
  (rf/dispatch [:patient-form-change field-key value]))

(defn trim-form []
  (rf/dispatch [:trim-form]))

(defn add-patient []
  (rf/dispatch [:add-patient]))

(defn clear-patient []
  (rf/dispatch [:clear-patient]))

