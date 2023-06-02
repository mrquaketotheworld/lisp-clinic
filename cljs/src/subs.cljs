(ns subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :modal-active?
 (fn [db]
   (:modal-active? db)))

(rf/reg-sub
 :loading?
 (fn [db]
   (:loading? db)))

(rf/reg-sub
 :patients
 (fn [db]
   (:patients db)))

(rf/reg-sub
 :ajax-error
 (fn [db]
   (:ajax-error db)))

(rf/reg-sub
 :patient
 (fn [db]
   (:patient db)))
