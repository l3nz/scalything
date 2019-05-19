(ns scalything.core
  (:require [reagent.core :as r]
            [scalything.audio :as a]
            [cljsjs.chartjs]
            [clojure.string :as str]
            [cljs.pprint :as pprint]))

(defonce S (r/atom {}))

(defonce SX (r/atom "foo"))

(js/console.log "Starting up!")

(def HIGHEST-NOTE 50)
(def LOWEST-NOTE 500)

(defn f2 [fnum]
  (pprint/cl-format nil  "~,2f" fnum))

(def NOTES ["C " "C#" "D " "D#" "E " "F " "F#" "G " "G#" "A " "A#" "B "])

(defn noteNumFromPitch
  "Central A is 69"

  [pitch]
  (-> pitch
      (/ 440)
      Math.log
      (/ (Math.log 2))
      (* 12)
      (+ 69)))

(defn toNoteName
  "A"
  [noteNum]

  (let [pos (-> noteNum
                (+ 0.5)
                int)
        octave (int (/ noteNum 12))
        rdelta  (- noteNum pos)

        noteName   (get NOTES (mod pos 12))]

    (str noteName octave " " (f2 rdelta))))

(defn currentSampleRate []
  (.-sampleRate (:audio-context @S)))

(defn noteForBin [nBin]
  (toNoteName (noteNumFromPitch (a/freqForBin nBin (currentSampleRate)))))

(defn getrms [state]
  (str (f2 (* 100 (:rms state))) "%"))

(defn bin->freq [data samplerate nBin]
  (let [value (get data nBin)
        freq (a/freqForBin nBin samplerate)]

    {:bin nBin
     :frq freq
     :note (toNoteName (noteNumFromPitch freq))
     :corr value}))

(defn data->frq [data threshold]
  (let [samplerate  (currentSampleRate)
        allVals (map (partial bin->freq (vec data)  samplerate)
                     (range (count data)))
        no-low-bins (filter #(> LOWEST-NOTE (:bin %) HIGHEST-NOTE) allVals)
        goodEnough (filter #(> (:corr %) threshold) no-low-bins)]

    (reverse (sort-by :corr goodEnough))))

(defn print-vals
  [data]
  (let [v (data->frq data .9)]
    [:pre
     (str/join "\n" (map str v))]))

(defn printrms []
  (js/setTimeout (partial a/readAudioToAtom S) 100)

  [:div
   [:h3 "Readinbg from " (noteForBin HIGHEST-NOTE)
    " to " (noteForBin LOWEST-NOTE)]

   [:p "rms"]
   (getrms @S)

   [:p "State"]
   (str (:bac @S))

   [:p "rms"]

   (print-vals (:corrs @S))])

(r/render [printrms]
          (js/document.getElementById "app"))

(a/getUserMedia S)
