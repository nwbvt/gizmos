(ns nwbvt.gizmos.components.pager
  (:require [re-frame.core :as rf]))

(defn- pages-to-show
  [page num-pages max-pages]
  (let [mandatory-pages
        (set (concat
               [0]
               (range (max 0 (dec page)) (min num-pages (+ 2 page)))
               [(dec num-pages)]))
        additional-pages
        (take (- max-pages (count mandatory-pages))
              (remove mandatory-pages (range 0 num-pages)))]
    (sort (concat mandatory-pages additional-pages))))

(rf/reg-sub
  ::current-page
  (fn [db [_ pager-id]]
    (or
      (get-in db [::current-page pager-id])
      0)))

(rf/reg-sub
  ::last-page
  (fn [db [_ pager-id]]
    (or
      (get-in db [::last-page pager-id])
      0)))

(def last-pages (atom {}))

(def on-changes (atom {}))

(def max-pages-mapping (atom {}))

(rf/reg-event-fx
  ::change-page
  (fn [{db :db} [_ pager-id new-page]]
    (let [event (@on-changes pager-id)]
      {:db (assoc-in db [::current-page pager-id] new-page)
     :fx [[:dispatch [event new-page]]]})))

(defn- previous-button
  [pager-id]
  [:a.pagination-previous
   (let [current-page @(rf/subscribe [::current-page pager-id])]
     (if (= 0 current-page) {:class "is-disabled"}
       {:on-click
        #(rf/dispatch [::change-page pager-id (dec current-page)])}))
   "Previous"])

(defn- next-button
  [pager-id]
  [:a.pagination-next
   (let [current-page @(rf/subscribe [::current-page pager-id])
         last-page (@last-pages pager-id)]
     (if (= (dec last-page) current-page) {:class "is-disabled"}
       {:on-click
        #(rf/dispatch [::change-page pager-id (inc current-page)])}))
   "Next"])

(defn page-list
  [pager-id]
  [:ul.pagination-list
   (let [current-page @(rf/subscribe [::current-page pager-id])
         last-page (@last-pages pager-id)
         max-pages (@max-pages-mapping pager-id)]
     (->>
       (let [all-pages (pages-to-show current-page last-page max-pages)]
         (for [page all-pages]
           [(if (and (< 0 page) (not (some #{(dec page)} all-pages)))
              ;; Break from previous page, show elipses
              [:li {:key (str "page" (dec page))}
               [:span.pagination-ellipses "..."]])
            [:li {:key (str "page" page)}
             [:a.pagination-link
              (merge {:key (str "pager-page" page)}
                     (if (= page current-page)
                       {:class "is-current"}
                       {:on-click #(rf/dispatch [::change-page pager-id page])}))
              page]]]))
       (apply concat)))])

(defn pager
  ([last-page max-pages on-change]
   (pager ::default-pager last-page max-pages on-change))
  ([pager-id last-page max-pages on-change]
   (swap! last-pages assoc pager-id last-page)
   (swap! max-pages-mapping assoc pager-id max-pages)
   (swap! on-changes assoc pager-id on-change)
   (let [current-page @(rf/subscribe [::current-page])]
    [:span
     (if (< 1 last-page)
       [:nav.pagination {:role "nativation" :aria-label "pagination"}
        (previous-button pager-id)
        (next-button pager-id)
        (page-list pager-id)]
       [:span])])))
