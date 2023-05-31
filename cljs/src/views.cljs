(ns views)

(defn select-age [select-name default-option]
  [:div.control.has-icons-left
   [:div.select
    [:select {:name select-name}
     [:option {:value "0"} default-option]
     (map (fn [i] [:option {:value i :key i} i]) (range 1 100))]]
   [:div.icon.is-small.is-left
    [:i.fa-solid.fa-list-ol]]])

(defn select-age-bottom []
  [select-age "age-bottom" "From"])

(defn select-age-top []
  [select-age "age-top" "Till"])

(defn select-city []
  [:div.control.has-icons-left
   [:div.select
    [:select {:name "city"}
     [:option {:value ""} "All"]]]
   [:div.icon.is-small.is-left
    [:i.fa-solid.fa-globe]]])

(defn input-text [label]
  [:div.field
   [:label.label {:for label} label]
   [:div.control
    [:input.input {:type "text" :id label :name (.toLowerCase label)
                   :placeholder (str "Enter " label)}]]])

(defn select-gender []
  [:div
   "Gender"
   [:div.control.has-icons-left
    [:div.select
     [:select {:name "Gender"}
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
     [select-gender]]
    [:div.column

     [:div "Age" [:div.field.is-grouped
                  [select-age-bottom]
                  [select-age-top]]]]

    [:div.column "City" [select-city]]

    [:div.column.is-two-fifths.is-align-self-flex-end
     [:div.field.is-grouped
      [:div.control.is-expanded
       [:input.input {:type "search" :name "search"
                      :placeholder (str "John Doe, 381293...")}]]
      [:div.control
       [:button.button.is-primary "Search"]]]]]])

(defn container []
  [header])

(defn app []
  [container])
