(ns nwbvt.gizmos.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [nwbvt.gizmos.core :as gizmos]))

(deftest test-pages-to-show
  (testing "getting the list of pages"
    (is (= [0]
           (gizmos/pages-to-show 0 1)))
    (is (= [0 1 2]
           (gizmos/pages-to-show 0 3)))
    (is (= [0 1 2]
           (gizmos/pages-to-show 0 3)))
    (is (= [0 1 2]
           (gizmos/pages-to-show 1 3)))
    (is (= [0 1 2 3]
           (gizmos/pages-to-show 0 4)))
    (is (= [0 1 2 4]
           (gizmos/pages-to-show 1 5)))
    (is (= [0 1 2 3 5]
           (gizmos/pages-to-show 2 6)))
    (is (= [0 1 2 5 6 7]
           (gizmos/pages-to-show 6 8)))
    (is (= [0 1 2 5 6 7 9]
           (gizmos/pages-to-show 6 10)))))
