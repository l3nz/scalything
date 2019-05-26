(ns scalything.cfg
  (:require [reagent.core :as r]))

(defonce S (r/atom {:bins []
                    :audio-context nil
                    :sampling-rate 44100
                    :snapshots []}))

(def HIGHEST-NOTE 50)
(def LOWEST-NOTE 500)
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


