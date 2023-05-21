(ns clj.src.db.credentials
  (:require [clj.src.config :refer [dbtype dbname host user password]]))

(def db {:dbtype dbtype
         :dbname dbname
         :host host
         :user user
         :password password})
