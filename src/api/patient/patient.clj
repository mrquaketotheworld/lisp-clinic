(ns api.patient.patient
  (:require [db.credentials :refer [db]]
            [clojure.java.jdbc :as jdbc]
            [api.patient.validation :as validation]))

(defn add [request]
  (let [body (:body request)
        {:keys [first-name last-name gender birth city street house mid]} body]
   (if (validation/is-add-handler-valid? body)
     {:status 200
      :headers {"Content-Type" "application/json"}
      :body {:is-valid? true}}
     {:status 400
      :headers {"Content-Type" "application/json"}
      :body {:is-valid? false}})))

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
