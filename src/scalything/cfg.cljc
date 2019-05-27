(ns scalything.cfg
  (:require [reagent.core :as r]))

(defonce S (r/atom {:bins []
                    :audio-context nil
                    :sampling-rate 44100
                    :min-note-id 1
                    :max-note-id 2
                    :snapshots []}))

(def HIGHEST-NOTE-BIN 50)
(def LOWEST-NOTE-BIN 500)
(def STATE-SIZE 100)
(def STATE-PER-SECOND 10)

(def CENTRAL-A-NOTE-NUMBER 69)
(def CENTRAL-A-NOTE-FREQ 440)

(def NOTES ["C" "C#"
            "D" "D#"
            "E"
            "F" "F#"
            "G" "G#"
            "A" "A#"
            "B"])



(defn log [& s]
  (js/console.log (apply str s)))