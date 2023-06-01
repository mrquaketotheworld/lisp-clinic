(ns dispatches
  (:require [re-frame.core :as rf]))

(defn hide-modal []
  (rf/dispatch [:modal-active? false]))

(defn show-modal []
  (rf/dispatch [:modal-active? true]))

