(ns config
  (:require [utils.file.interact :as file]))

(def config (file/load-edn "config.edn"))
(def db {:dbtype (:dbtype config)
         :dbname (:dbname config)
         :host (:host config)
         :user (:user config)
         :password (:password config)})
