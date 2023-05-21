(ns clj.src.config
  (:require [clojure.edn :as edn]))

(defn load-config [filename]
  (edn/read-string (slurp filename)))

(def config (load-config "config.edn"))
(def dbtype (:dbtype config))
(def dbname (:dbname config))
(def host (:host config))
(def user (:user config))
(def password (:password config))

