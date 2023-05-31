(ns subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :is-modal-active?
 (fn [db _]
   (:is-modal-active? db)))


