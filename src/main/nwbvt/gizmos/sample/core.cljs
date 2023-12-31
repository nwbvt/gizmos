(ns nwbvt.gizmos.sample.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [struct.core :as st]
            [nwbvt.gizmos.core :as gizmos]))

(rf/reg-event-fx
  ::initialize-db
  (fn [cfx event]
    {:db {::show-page 0}}))

(rf/reg-event-db
  ::change-page
  (fn [db [_ new-page]]
    (println "On page" new-page)
    (assoc db ::show-page new-page)))

(rf/reg-sub
  ::show-page
  (fn [db]
    (::show-page db)))

(rf/reg-event-fx
  ::close-my-modal
  (fn [_ _]
    (println "Modal closed")
    {}))

(rf/reg-event-fx
  ::submit-form
  (fn [_ [_ form]]
    (println "Got form" form)
    {}))

(def schema
  {:username [st/string [st/min-count 8] st/required]
   :sex [{:message "must be male or female" :validate #{"m" "f"}}]})

(defn main
  []
  [:section.section
   [:div.container
    [:div.columns.is-multiline
     [:div.column.is-half.has-text-centered
      [:section.hero>div.hero-body
       [:p.title "Pager"]
       [:p.subtitle "A simple paging device"]]
      [:textarea.textarea.is-family-code {:read-only true :rows 1
                                          :value "(gizmos/pager 10 6 ::change-page)"}]
      [:br]
      [:div
       (gizmos/pager 10 6 ::change-page)
       [:section.hero>div.hero-body>p.title "On Page " @(rf/subscribe [::show-page])]]]
     [:div.column.is-half.has-text-centered
      [:section.hero>div.hero-body
       [:p.title "Modal"]
       [:p.subtitle "A popup modal"]]
      [:textarea.textarea.is-family-code {:read-only true :rows 7
                                          :value
"(gizmos/modal ::my-modal
               :title \"My Modal\"
               :body [:div \"This is a modal\"]
               :footer [:button.button.is-primary
                        {:on-click #(gizmos/close-modal ::my-modal)}
                        \"Close\"]
               :on-close ::close-my-modal)"}]
      
      [:br]
      (gizmos/modal ::my-modal
                    :title "My Modal"
                    :body [:div "This is a modal"]
                    :footer [:button.button.is-primary {:on-click #(gizmos/close-modal ::my-modal)} "Close"]
                    :on-close [::close-my-modal])
      [:button.button {:on-click #(gizmos/launch-modal ::my-modal)} "Launch Modal"]]
     [:div.column.is-half.has-text-centered
      [:section.hero>div.hero-body
       [:p.title "Message Board"]
       [:p.subtitle "A message board for alerts and things"]]
      [:textarea.textarea.is-family-code {:read-only true :rows 1
                                          :value "(gizmos/message-board ::sample-board)"}]
      [:br]
      (gizmos/message-board ::sample-board :max-messages 3)
      [:div.section
       [:button.button {:on-click #(gizmos/info-message ::sample-board "This is an informational message")} "Add Info Message"]
       [:button.button {:on-click #(gizmos/warn-message ::sample-board "This is a warning message")} "Add Warning Message"]
       [:button.button {:on-click #(gizmos/error-message ::sample-board "This is an error message")} "Add Error Message"]
       [:button.button {:on-click #(gizmos/info-message ::sample-board "This is an temporary message" :fade 3 :keep-for 3)} "Add Temp Message"]
       [:button.button {:on-click #(gizmos/warn-message ::sample-board "This is keyed message" :id :message-key)} "Add Keyed Message"]
       ]]
     [:div.column.is-half
      [:section.hero.has-text-centered>div.hero-body
       [:p.title "Form"]
       [:p.subtitle "A form for user input"]]
      [:textarea.textarea.is-family-code {:read-only true :rows 10
                                          :value 
"(gizmos/form ::sample-form
              {:username {:type :text :label \"Username\"
                          :options {:placeholder \"Enter your name\"}}
               :sex {:type :select :label \"Sex\"
                     :choices [{:label \"Please choose\" :value :none}
                               {:label \"Male\" :value :m}
                               {:label \"Female\" :value :f}]}
               :submit {:type :submit :label \"Submit Form\"}}
              ::submit-form
              schema)"}]
      [:br]
      (gizmos/form ::sample-form
                   {:username {:type :text :label "Username" :options {:placeholder "Enter your name"}}
                    :sex {:type :select :label "Sex" :choices [{:label "Please choose" :value :none}
                                                               {:label "Male" :value :m}
                                                               {:label "Female" :value :f}]}
                    :submit {:type :submit :label "Submit Form"}}
                   ::submit-form
                   schema)]]]])

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [main] root-el)))

(defn init []
  (rf/dispatch [::initialize-db])
  (mount-root))
