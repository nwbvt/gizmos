(ns nwbvt.gizmos.core
  (:require [re-frame.core :as rf]
            [nwbvt.gizmos.components.pager]
            [nwbvt.gizmos.components.modal]))

(def pager nwbvt.gizmos.components.pager/pager)

(def modal nwbvt.gizmos.components.modal/modal)

(def launch-modal nwbvt.gizmos.components.modal/launch-modal)

(def close-modal nwbvt.gizmos.components.modal/close-modal)
