(defproject scalything "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [reagent "0.8.1"]
                 [cljsjs/chartjs "2.8.0-0"]]
  :aliases {"fig" ["trampoline" "run" "-m" "figwheel.main"]
            "figdev" ["trampoline" "run" "-m" "figwheel.main" "--" "--build" "dev" "--repl"]
            "fix" ["cljfmt" "fix"]}
  :plugins [[lein-cljfmt "0.6.4"]]
  :source-paths ["src"]

  :profiles
  {:dev
   {:resource-paths ["target"]
    :clean-targets ^{:protect false} ["target"]

    :dependencies [[org.clojure/clojurescript "1.10.339"]
                   [com.bhauman/figwheel-main "0.2.0"]
                      ;; optional but recommended
                   [com.bhauman/rebel-readline-cljs "0.1.4"]]}})




