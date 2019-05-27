(ns scalything.view
  (:require [scalything.cfg :as cfg :refer [S]]
            [scalything.notes :as notes]))

;; This place is for HTML views and components.


(defn getrms [state]
  (str
   (:audio-state state)
   " "
   (notes/f2 (* 100 (:rms state))) "%"))

(defn prepare-note-buckets
  "We receive as input snapshots, each of which is a sequence  of sequences
  of {:noteId :error}.

  We output a sequence of rows, each of which contains a sequence of
  {:noteId :num :avg}

  "

  [snapshots group-size]

  0

  )



(defn note-grid
  "Draws a grid for our notes"
  [snapshots min-note max-note]
  (let [n-cols (- max-note min-note)]


  [:table
   [:tbody
   [:tr {:key "ttl"}
    (for [c (range n-cols)
          :let [n (notes/toNoteNameShort (+ min-note c))]]
      [:th {:key c} [:small n]])]

   (for [r (range 10)]
     [:tr {:key r}
      (for [c (range n-cols)
            :let [k (str  "k" r "-" c)]]
        [:td {:key k} "*"])])

    ]]))



(defn print-main-screen []

  (let [{:keys [snapshots min-note-id max-note-id]} @cfg/S]



  [:div

   (note-grid snapshots min-note-id max-note-id)

   [:p "rms:"]
   (getrms @cfg/S)

   [:p "snapshots"]
   (count snapshots)

   [:p "last good data"]
   (str (last (:snapshots @cfg/S)))


   [:p "State"]
   (str @cfg/S)

   ;[:p "rms"]

   ;(print-vals (:corrs @cfg/S))
   ]))
