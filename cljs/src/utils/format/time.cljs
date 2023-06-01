(ns utils.format.time)

(defn current-date []
  (-> (js/Date.)
      .toISOString
      (.split "T")
      first))

