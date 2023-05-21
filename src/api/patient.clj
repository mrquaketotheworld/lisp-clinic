(ns api.patient
  (:require [db.credentials :refer [db]]
            [clojure.java.jdbc :as jdbc]))

(defn add [request]
  (println (:body request))
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body {:hello "world" :x 5}})

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
