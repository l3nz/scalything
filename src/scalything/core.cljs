(ns scalything.core
  (:require [reagent.core :as r]
            [scalything.audio :as a]
            [cljsjs.chartjs]
            [clojure.string :as str]
            [cljs.pprint :as pprint]
            [scalything.cfg :as cfg]
            [scalything.notes :as notes]
            [scalything.view :as v]))

(js/console.log "Starting up!")

(defn f2 [fnum]
  (pprint/cl-format nil  "~,2f" fnum))

(defn currentSampleRate []
  (:sampling-rate @cfg/S))

(defn noteForBin
  [nBin]

  (->
   (a/freqForBin nBin (currentSampleRate))
   notes/noteNumFromPitch
   notes/toNoteNameShort))

(defn getrms [state]
  (str (f2 (* 100 (:rms state))) "%"))

(defn bin->freq [data samplerate nBin]
  (let [value (get data nBin)
        freq (a/freqForBin nBin samplerate)
        {:keys [note oct error]} (notes/toNote (notes/noteNumFromPitch freq))]

    {:bin nBin
     :frq freq
     :note (str note " " oct)
     :error error
     :corr value}))

(defn data->frq [data  threshold]
  (let [samplerate  (currentSampleRate)
        allVals (map (partial bin->freq (vec data)  samplerate)
                     (range (count data)))
        no-low-bins (filter #(> cfg/LOWEST-NOTE (:bin %) cfg/HIGHEST-NOTE) allVals)
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
          :let [n (notes/toNoteNameShort (+ 40 c))]]
      [:th {:key c} n])]

   (for [r (range 10)]
     [:tr {:key r}
      (for [c (range 24)
            :let [k (str  "k" r "-" c)]]
        [:td {:key k} "*"])])])

(defn printrms []

  (let [corrs (:corrs @cfg/S)
        prev  (:bins @cfg/S)
        v (take 5 (data->frq corrs .9))
        allbins (conj (take 20 prev) v)]

        ;(swap! S merge :bins allbins )
    )

  [:div
   [:h3 "Reading from " (noteForBin cfg/HIGHEST-NOTE)
    " to " (noteForBin cfg/LOWEST-NOTE)]

   (note-grid)

   [:p "rms"]
   (getrms @cfg/S)

   [:p "State"]
   (str (:bac @cfg/S))

   [:p "rms"]

   (print-vals (:corrs @cfg/S))])

(defn _bin->freq [data samplerate nBin]
  (let [value (get data nBin)
        freq (a/freqForBin nBin samplerate)
        {:keys [note oct error]} (notes/toNote (notes/noteNumFromPitch freq))]

    {:bin nBin
     :frq freq
     :note (str note " " oct)
     :error error
     :corr value}))

(defn _data->frq [data samplerate threshold]
  (let [allVals (map (partial _bin->freq (vec data)  samplerate)
                     (range (count data)))
        no-low-bins (filter #(> cfg/LOWEST-NOTE (:bin %) cfg/HIGHEST-NOTE) allVals)
        goodEnough (filter #(> (:corr %) threshold) no-low-bins)]

    (reverse (sort-by :corr goodEnough))))

(defn update-bounded-vector
  "Adds an item to the end of our bounded-size vector."
  [myVec newData max-len]

  (let [newVec (conj myVec newData)
        toSkip (- (count newVec) max-len)]
    (if (pos? toSkip)
      (vec (drop toSkip newVec))
      newVec)))

(defn updateSnapshots
  [snapshots state corrs samplerate threshold]

  (cond
    (= :ok state)
    (let [new-best (take 3 (_data->frq corrs samplerate threshold))]
      (update-bounded-vector snapshots new-best cfg/STATE-SIZE))

    :else
    snapshots))

(defn process-new-data-in [myAtom]

  (let [{:keys [analyser snapshots sample-rate]} @myAtom
        analysis (a/readAudioStructure analyser)
        state (:audio-state analysis)
        corrs (:corrs analysis)

        new-snapshots (updateSnapshots snapshots state corrs sample-rate 0.9)
        newAnalysis (assoc analysis :snapshots new-snapshots)]

    (swap! myAtom merge newAnalysis)))

(defn main-loop []

  (js/setTimeout (partial process-new-data-in cfg/S) 100)

  [:div

   [:a {:href  "./figwheel-extra-main/auto-testing"
        :target "tests"}
    "Auto-tests"]

   (v/print-main-screen)])

(r/render [main-loop]
          (js/document.getElementById "app"))

(a/getUserMediaIfNeeded cfg/S)
