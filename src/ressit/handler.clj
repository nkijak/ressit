(ns ressit.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            ;[hiccup.middleware :refer [wrap-base-url]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ressit.views :refer [index-page engadget-page]]
            [prolefeed :as pf]))

(let [clojure-engadget (pf/add-feed "https://www.engadget.com/rss.xml")]
  (defn fetch-engaget-reddit []
    (clojure-engadget)))

(defroutes app-routes
  (GET "/" [] ((str index-page)))
  (GET "/rss/engadget" [] (str (engadget-page fetch-engaget-reddit)))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
