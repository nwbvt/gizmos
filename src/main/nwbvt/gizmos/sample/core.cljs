(ns nwbvt.gizmos.sample.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [nwbvt.gizmos.core :as gizmos]))

(rf/reg-event-fx
  ::initialize-db
  (fn [cfx event]
    {:db {::current-page 0}}))

(rf/reg-sub
  ::current-page
  (fn [db]
    (::current-page db)))

(rf/reg-event-db
  ::gizmos/change-page
  (fn [db [_ new-page]]
    (assoc db ::current-page new-page)))

(defn main
  []
  [:div
   (gizmos/pager
     @(rf/subscribe [::current-page])
     10)])

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [main] root-el)))

(defn init []
  (rf/dispatch [::initialize-db])
  (mount-root))
