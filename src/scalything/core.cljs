(ns scalything.core
(:require [reagent.core :as r]))


(defonce S (r/atom "foo"))


(js/console.log "Hello World I'm gooda!")


(defn simple-component []
  [:div
   [:p "I am a component!"]
   [:p.someclass
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} " and red keeping state "] "text.:" @S  ]])

  (r/render [simple-component]
            (js/document.getElementById "app"))

