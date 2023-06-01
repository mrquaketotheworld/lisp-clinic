(ns subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :modal-active?
 (fn [db _]
   (:modal-active? db)))

(rf/reg-sub
 :loading?
 (fn [db _]
   (:loading? db)))

(rf/reg-sub
 :patients
 (fn [db _]
   (:patients db)))





