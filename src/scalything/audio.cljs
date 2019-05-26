(ns scalything.audio
  (:require [clojure.string :as s]))

(defn ^:export create-audio-context
  "Construct an audio context in a way that works even if it's prefixed.

  Lifted from https://github.com/ctford/cljs-bach/
  "
  []
  (if js/window.AudioContext. ; Some browsers e.g. Safari don't use the
    (js/window.AudioContext.) ; unprefixed version yet.
    (js/window.webkitAudioContext.)))

(defonce audioContext
  (delay (create-audio-context)))

(defn log [& rest]
  (. js/console (log (apply str rest))))

; navigator.mozGetUserMedia has been replaced by navigator.mediaDevices.getUserMedia


(defn audioEnvironment
  [stream]
  (let [samplerate (.-sampleRate @audioContext)
        streamSrc	(.createMediaStreamSource @audioContext stream)
        analyser (.createAnalyser @audioContext)
        _ (.connect streamSrc analyser)]

    {:audio-context @audioContext
     :sampling-rate samplerate
     :analyser analyser}))

(defn getUserMedia
  "https://github.com/johnjelinek/cljs-getusermedia/blob/master/src/cljs/gum/core.cljs

  Stores the results in a given atom.
  "

  [myAtom]

  (let [on-success (fn [stream] (swap! myAtom merge  (audioEnvironment stream)))
        on-error (fn [err] (log "ko " err))
        constraints #js {:audio true :video false}]

    (. js/navigator (mozGetUserMedia constraints on-success on-error))))


(defn getUserMediaIfNeeded
  [myAtom]
  (if (nil? (:audio-context @myAtom))
    (getUserMedia myAtom)

    )

  )


(defn float32->array [buf]
  (reduce (fn [a v] (conj a (aget buf v)))
          []
          (range (.-length buf))))

(defn readAudio
  "Reads 1024 bytes from the analyser.
  If the analyser does not yet exist, it returns []"

  [analyser]

  (cond

    (nil? analyser)
    []

    :else
    (let [size 1024
          buf (new js/Float32Array size)
          _ (.getFloatTimeDomainData analyser buf)]

      (float32->array buf))))

(defn compute-rms
  "Computes RMS on a CLJS vector"

  [buffer]
  (let [addsquared (fn [a v]  (+ a (* v v)))
        v  (reduce addsquared 0 buffer)]
    (Math/sqrt (/ v (count buffer)))))

(defn autocorrelation
  "Computes autocorrelation on a CLJS vector.
	1 - (sum( v[n] - v[n+offset]) / size)

	"
  [data offset]
  (let  [size (count data)
         p0 (range size)
         p1 (map #(mod (+ offset %) size) p0)
         diff (map #(Math/abs (- (get data %1) (get data %2)))
                   p0 p1)]

    (- 1.0 (/ (reduce + 0.0 diff) size))))

(defn find-best-autocorrelation
  [data]

  (let [max-offset (/ (count data) 3)
        correlations (map (partial autocorrelation data) (range max-offset))]

    correlations))

(defn compute-autocorrelation
  [data]
  (if (empty? data)
    {:rms 0.0 :audio-state :no-input :corrs []}

    (let [rms (compute-rms data)]
      (if (< rms 0.1)
        {:rms rms :audio-state :low-volume :corrs []}

        (let [bacs (find-best-autocorrelation data)]
          {:rms rms :audio-state :ok :corrs bacs})))))

(defn readAudioStructure
  "returns {:rms 0.0 :bac -1 :corrs [....]}"

  [analyser]
  (let [vals (readAudio analyser)]
    (compute-autocorrelation vals)))

(defn readAudioToAtom
  [myAtom]
  (let [analyser (:analyser @myAtom)
        vals (if (nil? analyser)
               []
               (readAudio analyser))
        results (compute-autocorrelation vals)]

    (swap! myAtom merge results)))

(defn freqForBin
  [nBin samplingRate]

  (if (pos? nBin)
    (/ samplingRate nBin)
    0))
