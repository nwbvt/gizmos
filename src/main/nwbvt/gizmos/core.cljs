(ns nwbvt.gizmos.core
  (:require [re-frame.core :as rf]))

(defn- pages-to-show
  [page num-pages]
  (sort (set (concat
    (range (min num-pages 3))
    (range (max 0 (dec page)) (min num-pages (+ 2 page)))
    (range (max 0 (- num-pages 1)) num-pages)))))

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

(rf/reg-event-db
  ::init-pager
  (fn [db [_ pager-id last-page on-change]]
    (-> db
        (assoc-in [::last-page pager-id] last-page)
        (assoc-in [::on-change pager-id] on-change))))

(rf/reg-event-fx
  ::change-page
  (fn [{db :db} [_ pager-id new-page]]
    {:db (assoc-in db [::current-page pager-id] new-page)
     :fx [[:dispatch [(get-in db [::on-change pager-id]) new-page]]]}))

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
         last-page @(rf/subscribe [::last-page pager-id])]
     (if (= (dec last-page) current-page) {:class "is-disabled"}
       {:on-click
        #(rf/dispatch [::change-page pager-id (inc current-page)])}))
   "Next"])

(defn page-list
  [pager-id]
  [:ul.pagination-list
   (let [current-page @(rf/subscribe [::current-page pager-id])
         last-page @(rf/subscribe [::last-page pager-id])]
     (->>
       (let [all-pages (pages-to-show current-page last-page)]
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
  ([last-page on-change]
   (pager ::default-pager last-page on-change))
  ([pager-id last-page on-change]
   (rf/dispatch [::init-pager pager-id last-page on-change])
   (let [current-page @(rf/subscribe [::current-page])]
    [:span
     (if (< 1 last-page)
       [:nav.pagination {:role "nativation" :aria-label "pagination"}
        (previous-button pager-id)
        (next-button pager-id)
        (page-list pager-id)]
       [:span])])))
