(ns scalything.notes-test
  (:require [clojure.test :as t :refer [deftest is are]]
            [scalything.notes :refer [noteNumFromPitch toNote toNoteName toNoteNameShort]]))

; See http://localhost:9500/figwheel-extra-main/auto-testing

(defn f= [a b]
  (-> (- a b)
      Math.abs
      (< .01)))

(deftest noteNumFromPitch-test
  (are [frq note]
       (f= note (noteNumFromPitch frq))

    440 69
    445 69.19))

(deftest toNote-test
  (are [frq note]
       (= note (toNote frq))

    69 {:note "A", :oct 5, :note-num 69, :error 0}
    70 {:note "A#", :oct 5, :note-num 70, :error 0}
   ; 68.9 {:note "A", :oct 5, :note-num 69, :error 0}
   ; 69.2 {:note "A", :oct 5, :note-num 69, :error 0}
    ))

(deftest toNoteName-test
  (are [frq note]
       (= note (toNoteName frq))

    69 "A-5 0.00"))

(deftest toNoteNameShort-test
  (are [frq note]
       (= note (toNoteNameShort frq))

    69 "A-5"))


