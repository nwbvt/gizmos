(ns nwbvt.gizmos.components.pager-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [nwbvt.gizmos.components.pager :as pager]))

(deftest test-pages-to-show
  (testing "getting the list of pages"
    (is (= [0]
           (pager/pages-to-show 0 1)))
    (is (= [0 1 2]
           (pager/pages-to-show 0 3)))
    (is (= [0 1 2]
           (pager/pages-to-show 0 3)))
    (is (= [0 1 2]
           (pager/pages-to-show 1 3)))
    (is (= [0 1 2 3]
           (pager/pages-to-show 0 4)))
    (is (= [0 1 2 4]
           (pager/pages-to-show 1 5)))
    (is (= [0 1 2 3 5]
           (pager/pages-to-show 2 6)))
    (is (= [0 1 2 5 6 7]
           (pager/pages-to-show 6 8)))
    (is (= [0 1 2 5 6 7 9]
           (pager/pages-to-show 6 10)))))
