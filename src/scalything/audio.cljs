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

(def audioContext
  (delay (create-audio-context)))

(defn log [& rest]
  (. js/console (log (apply str rest))))

; navigator.mozGetUserMedia has been replaced by navigator.mediaDevices.getUserMedia


(defn audioEnvironment
  [stream]
  (let [streamSrc	(.createMediaStreamSource @audioContext stream)
        analyser (.createAnalyser @audioContext)
        _ (.connect streamSrc analyser)]

    {:audio-context @audioContext
     :analyser analyser}))

(defonce AUDIO (atom nil))

(defn getUserMedia
  "https://github.com/johnjelinek/cljs-getusermedia/blob/master/src/cljs/gum/core.cljs

  Stores the results in a given atom.
  "

  [myAtom]

  (let [on-success (fn [stream] (swap! myAtom merge  (audioEnvironment stream)))
        on-error (fn [err] (log "ko " err))
        constraints #js {:audio true :video false}]

    (. js/navigator (mozGetUserMedia constraints on-success on-error))))

(defn float32->array [buf]
  (reduce (fn [a v] (conj a (aget buf v)))
          []
          (range (.-length buf))))

(defn readAudio [analyser]
  (let [size 1000
        buf (new js/Float32Array size)
        _ (.getFloatTimeDomainData analyser buf)]

    (float32->array buf)))


(defn readAudioToAtom [myAtom]
	(let [analyser (:analyser @myAtom)
		  vals (if (nil? analyser)
		            []
		            (readAudio analyser))]
		(swap! myAtom merge {:samples vals})))


(defn rms
  "Computes RMS"

  [buffer]
  (let [addsquared (fn [a v]  (+ a (* v v)))
        v  (reduce addsquared 0 buffer)]
    (Math/sqrt (/ v (count buffer)))))


