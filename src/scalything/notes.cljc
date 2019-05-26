(ns scalything.notes
  (:require [scalything.cfg :as cfg]
            [cljs.pprint :as pprint]))

(defn f2 [fnum]
  (pprint/cl-format nil  "~,2f" fnum))

(defn note2
  "Prints a note with a 2-letter string"
  [noteName]
  (if (= 1 (count noteName))
    (str noteName "-")
    noteName))

(defn noteNumFromPitch
  "Central A is 69"

  [pitch]
  (-> pitch
      (/ cfg/CENTRAL-A-NOTE-FREQ)
      Math.log
      (/ (Math.log 2))
      (* 12)
      (+ cfg/CENTRAL-A-NOTE-NUMBER)))

(defn toNote
  "A"
  [noteNum]

  (let [pos (-> noteNum
                (+ 0.5)
                int)
        octave (int (/ noteNum 12))
        rdelta  (- noteNum pos)

        noteName   (get cfg/NOTES (mod pos 12))]

    {:note noteName
     :oct octave
     :note-num pos
     :error rdelta}))

(defn toNoteName
  "A#4 0.23"
  [noteNum]

  (let [{:keys [note oct error]} (toNote noteNum)]
    (str (note2 note) oct " " (f2 error))))

(defn toNoteNameShort
  "A-5"
  [noteNum]

  (let [{:keys [note oct]} (toNote noteNum)]
    (str (note2 note) oct)))



