(ns config
  (:require [utils.file.interact :as file]))

(def config (file/load-edn "config.edn"))
(def db-config {:dbtype (:dbtype config)
                :dbname (:dbname config)
                :host (:host config)
                :user (:user config)
                :password (:password config)})

(defn set-config! [config]
  (alter-var-root (var db-config) (constantly config)))
