(ns api.patient.patient
  (:require
   [db.models.patient :as patient]
   [api.patient.validation :as validation]))

(defn add [request]
  (let [patient-form (:body request)]
    (if (validation/is-patient-handler-valid? patient-form)
      (patient/add patient-form)
      {:status 400
       :headers {"Content-Type" "application/json"}
       :body {:error "Validation error"}})))

(defn delete [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body "delete"})

(defn edit [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body "edit"})

(defn get-by-id [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body "get"})

(defn search [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body "search"})

(defn get-all [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body "get-all"})
