(ns nwbvt.gizmos.sample.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
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

(defn main
  []
  [:section.section
   [:div.container
    [:div.columns
     [:div.column.is-one-half.has-text-centered
      [:section.hero>div.hero-body
       [:p.title "Pager"]
       [:p.subtitle "A simple paging device"]]
      [:textarea.textarea.is-family-code {:read-only true :rows 1}
       "(gizmos/pager 10 6 ::change-page)"]
      [:br]
      [:div
       (gizmos/pager 10 6 ::change-page)
       [:section.hero>div.hero-body>p.title "On Page " @(rf/subscribe [::show-page])]]]
     [:div.column.is-one-half.has-text-centered
      [:section.hero>div.hero-body
       [:p.title "Modal"]
       [:p.subtitle "A popup modal"]]
      [:textarea.textarea.is-family-code {:read-only true :rows 7}
"(gizmos/modal ::my-modal
               :title \"My Modal\"
               :body [:div \"This is a modal\"]
               :footer [:button.button.is-primary
                        {:on-click #(gizmos/close-modal ::my-modal)}
                        \"Close\"]
               :on-close ::close-my-modal)"]
      
      [:br]
      (gizmos/modal ::my-modal
                    :title "My Modal"
                    :body [:div "This is a modal"]
                    :footer [:button.button.is-primary {:on-click #(gizmos/close-modal ::my-modal)} "Close"]
                    :on-close [::close-my-modal])
      [:button.button {:on-click #(gizmos/launch-modal ::my-modal)} "Launch Modal"]]]]])

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [main] root-el)))

(defn init []
  (rf/dispatch [::initialize-db])
  (mount-root))
