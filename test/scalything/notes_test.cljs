(ns scalything.notes-test
  (:require [clojure.test :as t]
            [scalything.notes :refer [noteNumFromPitch]]))

; See http://localhost:9500/figwheel-extra-main/auto-testing

(defn f= [a b]
  (-> (- a b)
      Math.abs
      (< .01)
      ))


(t/deftest noteNumFromPitch-test
  (t/are [frq note]
    (f= note (noteNumFromPitch frq))

    440 69
    )
  )

