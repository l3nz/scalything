(ns scalything.notes
  (:require [scalything.cfg :as cfg])
  )


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
  "A"
  [noteNum]

  (let [{:keys [note oct error]} (toNote noteNum)]
    (str note oct " " (f2 error))))

(defn toNoteNameShort
  "A"
  [noteNum]

  (let [{:keys [note oct error]} (toNote noteNum)]
    (str note oct)))



