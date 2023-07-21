(ns nwbvt.gizmos.sample.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [nwbvt.gizmos.core :as gizmos]))

(rf/reg-event-fx
  ::initialize-db
  (fn [cfx event]
    {:db {}}))

(rf/reg-event-db
  ::change-page
  (fn [db [_ new-page]]
    (println "On page" new-page)
    (assoc db ::current-page new-page)))

(defn main
  []
  [:div
   (gizmos/pager 10 ::change-page)])

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [main] root-el)))

(defn init []
  (rf/dispatch [::initialize-db])
  (mount-root))
