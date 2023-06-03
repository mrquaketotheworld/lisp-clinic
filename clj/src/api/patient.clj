(ns api.patient
  (:require
   [db.models.patient :as patient]
   [utils.validation.patient.new-form :as validation-patient-new-form]
   [utils.validation.patient.search-form :as validation-patient-search-form]
   [utils.format.patient :as format-patient]
   [utils.format.message :as message]
   [utils.validation.error :as error]))

(defn add-edit [request callback]
  (let [patient-form (:body request)
        formatted-patient-form (format-patient/format-patient-form patient-form)]
    (if-let [validation-error (validation-patient-new-form/validate formatted-patient-form)]
      (message/error validation-error)
      (let [patient-row (patient/get-by-mid (:mid formatted-patient-form))]
        (callback patient-row formatted-patient-form)))))

(defn add [request]
  (add-edit request (fn [patient-row formatted-patient-form]
                      (if patient-row
                        (message/error (:patient-exists error/errors))
                        (do (patient/add formatted-patient-form)
                            (message/success "Patient was successfully added"))))))

(defn delete [request]
  (patient/delete (:mid (:path-params request)))
  (message/success "Patient was successfully deleted"))

(defn edit [request]
  (add-edit request (fn [patient-row formatted-patient-form]
                      (if patient-row
                        (do (patient/edit formatted-patient-form)
                            (message/success "Patient was successfully edited"))
                        (message/error (:patient-doesnt-exist error/errors))))))

(defn get-by-mid [request]
  (if-let [patient (patient/get-by-mid (:mid (:path-params request)))]
    (message/success patient)
    (message/error (:patient-doesnt-exist error/errors))))

(defn search [{patient-form :params}]
  (let [formatted-patient-form (format-patient/format-patient-search-form patient-form)]
    (if-let [validation-error (validation-patient-search-form/validate formatted-patient-form)]
      (message/error validation-error)
      (message/success (patient/search formatted-patient-form)))))
