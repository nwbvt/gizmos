(ns nwbvt.gizmos.core
  (:require [re-frame.core :as rf]))

(defn- pages-to-show
  [page num-pages]
  (sort (set (concat
    (range (min num-pages 3))
    (range (max 0 (dec page)) (min num-pages (+ 2 page)))
    (range (max 0 (- num-pages 1)) num-pages)))))

(defn- previous-button
  [page-event current-page]
  [:a.pagination-previous
   (if (= 0 current-page)
     {:class "is-disabled"}
     {:on-click
      #(rf/dispatch
         [page-event (dec current-page)])})
   "Previous"])

(defn- next-button
  [page-event current-page max-page]
  [:a.pagination-next
   (if (= (dec max-page) current-page)
     {:class "is-disabled"}
     {:on-click #(rf/dispatch
                   [page-event (inc current-page)])})
   "Next"])

(defn page-list
  [page-event current-page max-page]
  [:ul.pagination-list
   (->>
     (let [all-pages (pages-to-show current-page max-page)]
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
                     {:on-click #(rf/dispatch [page-event page])}))
            page]]]))
     (apply concat))])

(defn pager
  ([current-page max-page]
   (pager current-page max-page ::change-page))
  ([current-page max-page page-event]
   [:span
   (if (< 1 max-page)
     [:nav.pagination {:role "nativation" :aria-label "pagination"}
      (previous-button page-event current-page)
      (next-button page-event current-page max-page)
      (page-list page-event current-page max-page)]
     [:span])]))
