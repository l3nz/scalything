(ns scalything.core
  (:require [reagent.core :as r]
            [scalything.audio :as a]
            [cljsjs.chartjs]
            [clojure.string :as str]
            [cljs.pprint :as pprint]))

(defonce S (r/atom {:bins []}))

(js/console.log "Starting up!")

(def HIGHEST-NOTE 50)
(def LOWEST-NOTE 500)
(def STATE-SIZE 100)

(defn f2 [fnum]
  (pprint/cl-format nil  "~,2f" fnum))

(def NOTES ["C " "C#"
            "D " "D#"
            "E "
            "F " "F#"
            "G " "G#"
            "A " "A#"
            "B "])

(defn noteNumFromPitch
  "Central A is 69"

  [pitch]
  (-> pitch
      (/ 440)
      Math.log
      (/ (Math.log 2))
      (* 12)
      (+ 69)))

(defn toNote
  "A"
  [noteNum]

  (let [pos (-> noteNum
                (+ 0.5)
                int)
        octave (int (/ noteNum 12))
        rdelta  (- noteNum pos)

        noteName   (get NOTES (mod pos 12))]

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

(defn currentSampleRate []
  (let [ac (:audio-context @S)]
    (cond
      (nil? ac) 44100
      :else (.-sampleRate ac))))

(defn noteForBin
  [nBin]

  (->
   (a/freqForBin nBin (currentSampleRate))
   noteNumFromPitch
   toNoteNameShort))

(defn getrms [state]
  (str (f2 (* 100 (:rms state))) "%"))

(defn bin->freq [data samplerate nBin]
  (let [value (get data nBin)
        freq (a/freqForBin nBin samplerate)
        {:keys [note oct error]} (toNote (noteNumFromPitch freq))]

    {:bin nBin
     :frq freq
     :note (str note " " oct)
     :error error
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
  (cond
    (empty? data)
    [:pre "?"]

    :else

    (let [v (data->frq data .9)]
      [:pre
       (str/join "\n" (map str v))])))

(defn note-grid
  "Draws a grid for our notes"
  []
  [:table
   [:tr {:key "ttl"}
    (for [c (range 24)
          :let [n (toNoteNameShort (+ 40 c))]]
      [:th {:key c} n])]

   (for [r (range 10)]
     [:tr {:key r}
      (for [c (range 24)
            :let [k (str  "k" r "-" c)]]
        [:td {:key k} "*"])])])

(defn printrms []
  (js/setTimeout (partial a/readAudioToAtom S) 100)

  (let [corrs (:corrs @S)
        prev  (:bins @S)
        v (take 5 (data->frq corrs .9))
        allbins (conj (take 20 prev) v)]

        ;(swap! S merge :bins allbins )
    )

  [:div
   [:h3 "Reading from " (noteForBin HIGHEST-NOTE)
    " to " (noteForBin LOWEST-NOTE)]

   (note-grid)

   [:p "rms"]
   (getrms @S)

   [:p "State"]
   (str (:bac @S))

   [:p "rms"]

   (print-vals (:corrs @S))])

(r/render [printrms]
          (js/document.getElementById "app"))

(a/getUserMedia S)
