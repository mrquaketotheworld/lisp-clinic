(ns core ; TODO test from scratch
  (:require [reagent.dom]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [events]
            [subs]
            [views.app :refer [app]]
            [dispatches :refer [init-db-sync search-patients get-cities]]))

(def test-temp "")
(defn render []
  (reagent.dom/render [app] (js/document.getElementById "app")))

(defn ^:dev/after-load clear-cache-and-render! []
  (rf/clear-subscription-cache!)
  (render))

(defn -main []
  (init-db-sync)
  (search-patients)
  (get-cities)
  (render))
