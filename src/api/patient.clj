(ns api.patient
  (:require [db.credentials :refer [db]]
            [clojure.java.jdbc :as jdbc]))

(defn add [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body "add"})

(defn delete [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body "delete"})

(defn edit [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body "edit"})

(defn get [request]
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
