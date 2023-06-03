(ns subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :modal-active?
 (fn [db]
   (:modal-active? db)))

(rf/reg-sub
 :patients
 (fn [db]
   (:patients db)))

(rf/reg-sub
 :ajax-success
 (fn [db]
   (:ajax-success db)))

(rf/reg-sub
 :ajax-error
 (fn [db]
   (:ajax-error db)))

(rf/reg-sub
 :patient
 (fn [db]
   (:patient db)))

(rf/reg-sub
 :patient-form-mode
 (fn [db]
   (:patient-form-mode db)))

(rf/reg-sub
 :filter-search
 (fn [db]
   (:filter-search db)))

