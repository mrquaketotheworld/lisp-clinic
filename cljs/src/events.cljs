(ns events
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]
            [db :refer [check-spec-interceptor]]))

(def DEFAULT-ERROR-MESSAGE "Oops... Sorry, something went wrong, try to reload the page")

(rf/reg-event-db
 :init-db
 check-spec-interceptor
 (fn []
   {:modal-active? false
    :loading? true
    :patient {:gender "Male"}}))

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
                 :on-success [:on-search-patients-success]
                 :on-failure [:on-search-patients-error]}}))

(rf/reg-event-db
 :on-search-patients-success
 check-spec-interceptor
 (fn [db [_ patients]]
   (assoc db :patients patients :loading? false)))

(rf/reg-event-db
 :on-search-patients-error
 check-spec-interceptor
 (fn [db]
   (assoc db :ajax-error DEFAULT-ERROR-MESSAGE :loading? false)))

(rf/reg-event-fx
 :delete-patient
 check-spec-interceptor
 (fn [{:keys [db]} [_ mid]]
   {:db (assoc db :loading? true)
    :http-xhrio {:method :delete
                 :uri (str "/api/patient/delete/" mid)
                 :body {}
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:on-delete-patient-success mid]
                 :on-failure [:on-delete-patient-error]}}))

(rf/reg-event-db
 :on-delete-patient-success
 check-spec-interceptor
 (fn [db [_ mid]]
   (assoc db :patients (filter #(not= (:mid %) mid) (:patients db)) :loading? false)))

(rf/reg-event-db
 :on-delete-patient-error
 check-spec-interceptor
 (fn [db]
   (assoc db :ajax-error DEFAULT-ERROR-MESSAGE :loading? false)))

(rf/reg-event-db
 :patient-form-change
 check-spec-interceptor
 (fn [db [_ field-key value]]
   (assoc-in db [:patient field-key] value)))

(rf/reg-event-db
 :trim-form
 check-spec-interceptor
 (fn [db]
   (assoc db :patient (reduce (fn [acc key-value]
        (assoc acc (first key-value) (.trim (second key-value)))) {} (:patient db)))))

#_(rf/reg-event-fx
 :add-patient
 check-spec-interceptor
 (fn [{:keys [db]}]
   {:db (assoc db :loading? true)
    :http-xhrio {:method :post
                 :uri "/api/patient/add"
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:add-patient-success]
                 :on-failure [:add-patient-error]}}))

