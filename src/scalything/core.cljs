(ns scalything.core
  (:require [reagent.core :as r]
            [scalything.audio :as a]
            [cljsjs.chartjs]
            [clojure.string :as str]
            [cljs.pprint :as pprint]
            [scalything.cfg :as cfg]
            [scalything.notes :as notes]))


(js/console.log "Starting up!")

(defn f2 [fnum]
  (pprint/cl-format nil  "~,2f" fnum))

(defn currentSampleRate []
  (let [ac (:audio-context @cfg/S)]
    (cond
      (nil? ac) 44100
      :else (.-sampleRate ac))))

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

(defn data->frq [data threshold]
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


(defn main-loop []

  (js/setTimeout (partial a/readAudioToAtom cfg/S) 100)

  [:div

   [:a {:href  "./figwheel-extra-main/auto-testing"
        :target "tests"}
    "Auto-tests"]

   (printrms)


   ]

  )



(r/render [main-loop]
          (js/document.getElementById "app"))

(a/getUserMedia cfg/S)
