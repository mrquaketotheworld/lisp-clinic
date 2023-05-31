(ns views.pagination)

(defn pagination []
  [:nav.pagination
   [:a.pagination-previous.is-disabled "Previous"]
   [:a.pagination-next "Next page"]
   [:ul.pagination-list
    [:li
     [:a.pagination-link.is-current 1]]
    [:li
     [:a.pagination-link 2]]
    [:li
     [:a.pagination-link 3]]]])

