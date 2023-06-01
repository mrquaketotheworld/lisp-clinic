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
                 :on-failure [:fetch-patients-error]}}))

(rf/reg-event-db
 :fetch-patients-success
 (fn [db [_ patients]]
   (assoc db :patients patients :loading? false)))

(rf/reg-event-db
 :fetch-patients-error
 (fn [db]
   (assoc db :patients-fetch-error "Oops... Try reload the page please" :loading? false))) ; TODO error m

(rf/reg-event-fx
 :delete-patient
 (fn [{:keys [db]} [_ mid]]
   {:db (assoc db :loading? true)
    :http-xhrio {:method :delete
                 :uri (str "/api/patient/delete/" mid)
                 :body {}
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:fetch-delete-patient-success mid]
                 :on-failure [:fetch-delete-patient-error]}}))

(rf/reg-event-db
 :fetch-delete-patient-success
 (fn [db [_ mid]]
   (assoc db :patients (filter #(not= (:mid %) mid) (:patients db)) :loading? false)))

(rf/reg-event-db
 :fetch-delete-patient-error
 (fn [db]
   (assoc db :patients-fetch-error "Oops... Try reload the page please" :loading? false)))

