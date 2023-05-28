(ns utils.file.interact
  (:require [clojure.edn :as edn]))

(defn load-edn [filename]
  (edn/read-string (slurp filename)))

