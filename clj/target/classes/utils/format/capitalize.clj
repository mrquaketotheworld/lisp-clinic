(ns utils.format.capitalize
  (:require [clojure.string :as string]))

(defn capitalize-words [value]
  (->> (string/split (str value) #"\b")
       (map string/capitalize)
       string/join))
