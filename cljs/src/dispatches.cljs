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

(defn fill-edit-patient [mid]
  (rf/dispatch [:fill-edit-patient mid]))

(defn on-patient-form-change [field-key value]
  (rf/dispatch [:form-change :patient field-key value]))

(defn on-filter-search-form-change [field-key value]
  (rf/dispatch [:form-change :filter-search field-key value]))

(defn trim-form-patient []
  (rf/dispatch [:trim-form :patient]))

(defn trim-form-filter-search []
  (rf/dispatch [:trim-form :filter-search]))

(defn add-edit-patient []
  (rf/dispatch [:add-edit-patient]))

(defn clear-patient []
  (rf/dispatch [:clear-patient]))

(defn remove-ajax-success []
  (rf/dispatch [:remove-ajax-success]))

(defn remove-ajax-error []
  (rf/dispatch [:remove-ajax-error]))

(defn patient-form-mode [mode ]
  (rf/dispatch [:patient-form-mode mode]))

