(defproject ts-learning "0.1.0-SNAPSHOT"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.2"]
                 [org.clojure/tools.reader "1.4.0"]
                 [uncomplicate/neanderthal "0.61.0"]
                 [org.bytedeco/mkl-platform-redist "2025.2-1.5.12"]
                 [org.apache.commons/commons-math3 "3.6.1"]
                 [aerial.hanami "0.19.0"]
                 [metasoarous/oz "2.0.0-alpha5"]]
  :main ^:skip-aot ts-learning.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
