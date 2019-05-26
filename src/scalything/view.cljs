(ns scalything.view
  (:require [scalything.cfg :as cfg :refer [S]]
            [scalything.notes :as notes]))

;; This place is for HTML views and components.


(defn getrms [state]
  (str
   (:audio-state state)
   " "
   (notes/f2 (* 100 (:rms state))) "%"))

(defn print-main-screen []

  [:div

   ;(note-grid)

   [:p "rms:"]
   (getrms @cfg/S)

   [:p "snapshots"]
   (count (:snapshots @cfg/S))


   ;[:p "State"]
   ;(str (:bac @cfg/S))

   ;[:p "rms"]

   ;(print-vals (:corrs @cfg/S))
   ])
