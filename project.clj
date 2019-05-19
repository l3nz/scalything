(defproject scalything "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [reagent "0.8.1"]]
:aliases {"fig" ["trampoline" "run" "-m" "figwheel.main"]}
:source-paths ["src"]


  :profiles 
    {:dev 
      {:resource-paths ["target"]
                   :clean-targets ^{:protect false} ["target"]


      	:dependencies [[org.clojure/clojurescript "1.10.339"]
                      [com.bhauman/figwheel-main "0.2.0"]
                      ;; optional but recommended
                      [com.bhauman/rebel-readline-cljs "0.1.4"]]}})




