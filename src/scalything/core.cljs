(ns scalything.core
  (:require [reagent.core :as r]
            [scalything.audio :as a]))


(defonce S (r/atom {}))

(defonce SX (r/atom "foo"))

(js/console.log "Starting up!")

(defn simple-component []
  [:div
   [:p "I am a component."]
   [:p.someclass
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} " and reda keeping state "] "text.:" @SX]])


(defn getrms [state]
	(a/rms (:samples state)))


(defn printrms []
	(js/setTimeout (partial a/readAudioToAtom S) 100)


	[:div 
		[:p "Audiocontext"]
		;(str @S)
		[:p "rms"]
		(getrms @S)

	]

)


(r/render [printrms]
          (js/document.getElementById "app"))

(a/getUserMedia S)
