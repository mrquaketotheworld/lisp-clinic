(ns core ; TODO test from scratch
  (:require [reagent.dom]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [events]
            [subs]
            [views.app :refer [app]]
            [dispatches :refer [search-patients get-cities]]))

(defn render []
  (reagent.dom/render [app] (js/document.getElementById "app")))

(defn ^:dev/after-load clear-cache-and-render! []
  (rf/clear-subscription-cache!)
  (render))

(defn -main []
  (rf/dispatch-sync [:init-db])
  (search-patients)
  (get-cities)
  (render))
