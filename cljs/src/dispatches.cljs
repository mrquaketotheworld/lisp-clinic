(ns dispatches
  (:require [re-frame.core :as rf]))

(defn init-db-sync []
  (rf/dispatch-sync [:init-db]))

(defn hide-modal []
  (rf/dispatch [:modal-active? false]))

(defn search-patients []
  (rf/dispatch [:search-patients]))

(defn delete-patient [mid]
  (rf/dispatch [:delete-patient mid]))

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

(defn remove-ajax-success []
  (rf/dispatch [:remove-ajax-success]))

(defn remove-ajax-error []
  (rf/dispatch [:remove-ajax-error]))

(defn get-cities []
  (rf/dispatch [:get-cities]))

(defn add-patient-form []
  (rf/dispatch [:add-patient-form]))

(defn edit-patient-form [mid]
  (rf/dispatch [:edit-patient-form mid]))

