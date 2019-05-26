(ns scalything.core-test
  (:require [clojure.test :refer [deftest is are]]
            [scalything.core :refer [update-bounded-vector]]))

(deftest update-bounded-vector-test
  (are [v i o]
       (= o (update-bounded-vector v i 5))

    ; vuoto
    [] :a  [:a]

    ; a 4
    [:a :b :c :d] :e [:a :b :c :d :e]

    ; a 5
    [:a :b :c :d :e] :f [:b :c :d :e :f]))
