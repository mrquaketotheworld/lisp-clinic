(ns core
  (:require [reagent.core :as reagent]
            [reagent.dom :as rdom]
            [re-frame.core :as rf]))

(defn ^:dev/after-load main []
  (js/console.log "hello world!!!"))
