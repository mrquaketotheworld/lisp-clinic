(ns db.credentials
  (:require [config :refer [dbtype dbname host user password]]))

(def db {:dbtype dbtype
         :dbname dbname
         :host host
         :user user
         :password password})
