(ns events
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]
            [db :refer [check-spec-interceptor]]))

(def DEFAULT-ERROR-MESSAGE "Oops... Sorry, try to reload the page")

(rf/reg-event-db
 :init-db
 check-spec-interceptor
 (fn []
   {:modal-active? false
    :loading? true}))

(rf/reg-event-db
 :modal-active?
 check-spec-interceptor
 (fn [db event]
   (assoc db :modal-active? (second event))))

(rf/reg-event-fx
 :search-patients
 check-spec-interceptor
 (fn [{:keys [db]}]
   {:db (assoc db :loading? true)
    :http-xhrio {:method :get
                 :uri "/api/patient/search"
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:fetch-patients-success]
                 :on-failure [:fetch-patients-error]}}))

(rf/reg-event-db
 :fetch-patients-success
 check-spec-interceptor
 (fn [db [_ patients]]
   (assoc db :patients patients :loading? false)))

(rf/reg-event-db
 :fetch-patients-error
 check-spec-interceptor
 (fn [db]
   (assoc db :patients-fetch-error DEFAULT-ERROR-MESSAGE :loading? false)))

(rf/reg-event-fx
 :delete-patient
 check-spec-interceptor
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
 check-spec-interceptor
 (fn [db [_ mid]]
   (assoc db :patients (filter #(not= (:mid %) mid) (:patients db)) :loading? false)))

(rf/reg-event-db
 :fetch-delete-patient-error
 check-spec-interceptor
 (fn [db]
   (assoc db :patients-fetch-error DEFAULT-ERROR-MESSAGE :loading? false)))

