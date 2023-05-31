(ns dispatches
  (:require [re-frame.core :as rf]))

(defn hide-modal []
  (rf/dispatch [:is-modal-active? false]))

(defn show-modal []
  (rf/dispatch [:is-modal-active? true]))

