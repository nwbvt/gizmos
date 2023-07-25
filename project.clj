(defproject nwbvt.gizmos "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[reagent "1.1.1"]
                 [re-frame "1.3.0"]
                 [day8.re-frame/tracing "0.6.2"]
                 [org.clojure/clojure "1.11.1"]

                 [binaryage/devtools "1.0.6"]
                 [day8.re-frame/re-frame-10x "1.5.0"]]

  :min-lein-version "2.0.0"
  
  :source-paths ["src/main"]
  :test-paths ["src/test"]
  :resource-paths ["resources"]
  :target-path "target/%s/"

  :plugins [] 

  :profiles
  {:uberjar {:omit-source false
             :aot :all
             :uberjar-name "gizmos.jar"}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:jvm-opts ["-Dconf=dev-config.edn" ]
                  :dependencies [[org.clojure/tools.namespace "1.3.0"]
                                 [pjstadig/humane-test-output "0.11.0"]
                                 [prone "2021-04-23"]
                                 [thheller/shadow-cljs "2.24.1"]
                                 [ring/ring-devel "1.9.6"]
                                 [ring/ring-mock "0.4.0"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.24.1"]
                                 [jonase/eastwood "1.2.4"]
                                 [cider/cider-nrepl "0.26.0"]] 
                  
                  :source-paths ["env/dev/clj" ]
                  :resource-paths ["env/dev/resources"]
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:jvm-opts ["-Dconf=test-config.edn" ]
                  :resource-paths ["env/test/resources"] }
   :profiles/dev {}
   :profiles/test {}})
