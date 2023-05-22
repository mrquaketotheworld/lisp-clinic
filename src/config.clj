(ns config
  (:require [clojure.edn :as edn]))

(defn load-config [filename]
  (edn/read-string (slurp filename)))

(def config (load-config "config.edn"))
(def db {:dbtype (:dbtype config)
         :dbname (:dbname config)
         :host (:host config)
         :user (:user config)
         :password (:password config)})
