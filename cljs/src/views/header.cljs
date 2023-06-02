(ns views.header)

(defn select-age [select-name default-option]
  [:div.control.has-icons-left
   [:div.select
    [:select {:name select-name :default-value default-option}
     (map (fn [i] [:option {:value i :key i} i]) (range 101))]]
   [:div.icon.is-small.is-left
    [:i.fa-solid.fa-list-ol]]])

(defn select-city []
  [:div.control.has-icons-left
   [:div.select
    [:select {:name "city"}
     [:option {:value ""} "All"]]]
   [:div.icon.is-small.is-left
    [:i.fa-solid.fa-globe]]])

(defn select-gender []
  [:div.field.mr-4
   [:label.label "Gender"]
   [:div.control.has-icons-left
    [:div.select
     [:select {:name "gender"}
      [:option {:value ""} "All"]
      [:option {:value "Male"} "Male"]
      [:option {:value "Female"} "Female"]]]
    [:div.icon.is-small.is-left
     [:i.fa-regular.fa-person-circle-question]]]])

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
                                             [select-age "age-bottom" "0"]
                                             [select-age "age-top" "100"]]]
      [:div.field [:label.label "City"] [select-city]]]]
    [:div.column.is-half
     [:div.field
      [:label.label "Search"]
      [:div.field.is-grouped
       [:div.control.is-expanded.has-icons-left
        [:input.input {:type "search" :name "search"
                       :placeholder (str "John Doe, 381293...")}]
        [:div.icon.is-small.is-left
         [:i.fa-sharp.fa-solid.fa-keyboard]]]
       [:div.control
        [:button.button.is-primary
         [:span.icon.is-small [:i.fa-solid.fa-magnifying-glass]]
         [:span "Go"]]]]]]]])

