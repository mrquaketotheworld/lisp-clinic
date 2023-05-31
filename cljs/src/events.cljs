(ns events
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
  :init-db
  (fn [_ _]
    {:is-modal-active? false}))

(rf/reg-event-db
  :is-modal-active?
  (fn [db event]
    (assoc db :is-modal-active? (second event))))
