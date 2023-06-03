(ns views.header
  (:require [re-frame.core :as rf]
            [dispatches :refer [on-filter-search-form-change trim-form-filter-search]]))

(defn on-input-change [field-key]
  #(on-filter-search-form-change field-key (.. % -target -value)))

(defn select-age [select-name]
  (let [keyword-select-name (keyword select-name)]
    [:div.control.has-icons-left
     [:div.select
      [:select {:name select-name
                :value (keyword-select-name @(rf/subscribe [:filter-search]))
                :on-change (on-input-change keyword-select-name)}
       (map (fn [i] [:option {:value i :key i} i]) (range 101))]]
     [:div.icon.is-small.is-left
      [:i.fa-solid.fa-list-ol]]]))

(defn select-gender []
  [:div.field.mr-4
   [:label.label "Gender"]
   [:div.control.has-icons-left
    [:div.select
     [:select {:name "gender" :value (:gender @(rf/subscribe [:filter-search]))
               :on-change (on-input-change :gender)}
      [:option {:value ""} "All"]
      [:option {:value "Male"} "Male"]
      [:option {:value "Female"} "Female"]]]
    [:div.icon.is-small.is-left
     [:i.fa-solid.fa-person-circle-question]]]])

(defn select-city []
  [:div.control.has-icons-left
   [:div.select
    [:select {:name "city" :value (:city @(rf/subscribe [:filter-search]))
              :on-change (on-input-change :city)}
     [:option {:value ""} "All"]]]
   [:div.icon.is-small.is-left
    [:i.fa-solid.fa-tree-city]]])

(defn header []
  [:<>
   [:div.level
    [:p.level-item
     [:img.logo {:src "images/lispclinic.png"}]]]
   [:div.columns.box
    [:div.column
     [:div.field.is-grouped
      [select-gender]
      [:div.field.mr-4 [:label.label "Age"] [:div.field.is-grouped
                                             [select-age "age-bottom"]
                                             [select-age "age-top"]]]
      [:div.field
       [:label.label "City"]
       [:div.control.has-icons-left
        [select-city]
        [:div.icon.is-small.is-left
         [:i.fa-solid.fa-tree-city]]]]]]
    [:div.column.is-half
     [:div.field
      [:label.label "Search"]
      [:div.field.is-grouped
       [:div.control.is-expanded.has-icons-left
        [:input.input {:type "search" :name "search"
                       :placeholder (str "John Doe, 381293...")
                       :value (:search @(rf/subscribe [:filter-search]))
                       :on-change (on-input-change :search)
                       :on-blur trim-form-filter-search}]
        [:div.icon.is-small.is-left
         [:i.fa-sharp.fa-solid.fa-keyboard]]]
       [:div.control
        [:button.button.is-primary
         [:span.icon.is-small [:i.fa-solid.fa-magnifying-glass]]
         [:span "Go"]]]]]]]])

