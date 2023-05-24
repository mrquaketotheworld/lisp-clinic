(ns api.patient
  (:require
   [db.models.patient :as patient]
   [utils.validation.patient :as validation-patient]
   [utils.format.patient :as format-patient]
   [utils.format.message :as message]))

(defn add [request]
  (let [patient-form (:body request)
        formatted-patient-form (format-patient/format-patient-form patient-form)]
    (if (patient/does-exist? (:mid formatted-patient-form))
      (message/error "Patient already exists")
      (if (validation-patient/is-patient-form-valid? formatted-patient-form)
        (do (patient/add formatted-patient-form)
            (message/success))
        (message/error "Validation error")))))

(defn delete [request]
  (patient/delete (:mid (:body request)))
  (message/success))

(defn edit [request]
  (message/success))

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
