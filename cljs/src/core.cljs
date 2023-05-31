(ns core
  (:require [reagent.dom]
            [re-frame.core :as rf]
            [views]))


(defn render []
  (reagent.dom/render [views/app] (js/document.getElementById "app")))

(defn ^:dev/after-load clear-cache-and-render! []
  (rf/clear-subscription-cache!)
  (render))

(defn -main []
  (enable-console-print!)
  (rf/dispatch-sync [:init-db])
  (render))
