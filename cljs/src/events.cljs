(ns events
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]))

(rf/reg-event-db
 :init-db
 (fn [_ _]
   {:modal-active? false
    :loading? true}))

(rf/reg-event-db
 :modal-active?
 (fn [db event]
   (assoc db :modal-active? (second event))))

(rf/reg-event-fx
 :search-patients
 (fn [{:keys [db]} _]
   {:db (assoc db :loading? true)
    :http-xhrio {:method :get
                 :uri "/api/patient/search"
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:fetch-patients-success]
                 :on-failure [:bad-http-result]}}))

(rf/reg-event-db
 :fetch-patients-success
 (fn [db [_ patients]]
   (assoc db :patients patients :loading? false)))