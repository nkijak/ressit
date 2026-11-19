(defproject ressit "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [dev.data-star.clojure/sdk "1.0.0-RC13"]
                 [dev.data-star.clojure/ring "1.0.0-RC13"]
                 [dev.data-star.clojure/malli-schemas "1.0.0-RC13"]
                 [dev.data-star.clojure/ring-malli-schemas "1.0.0-RC13"]
                 [hiccup "2.0.0"]
                 [prolefeed "0.1-SNAPSHOT"]
                 [org.postgresql/postgresql "42.7.14"]
                 [com.github.igrishaev/pg2-core "0.1.52"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler ressit.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}
   :migrations {:dependencies [[net.clojars.abogoyavlensky/automigrate "0.3.3"]] :main automigrate.core}}
  :aliases {"migrations" ["with-profile" "+migrations" "run"]})
