(ns ressit.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            ;[hiccup.middleware :refer [wrap-base-url]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ressit.views :as view]
            [prolefeed :as pf]
            [ressit.db :as db :only [get-entries]]))

(let [clojure-engadget (pf/add-feed "https://www.engadget.com/rss.xml")]
  (defn fetch-engaget-reddit []
    (clojure-engadget)))

(defn- main-page []
  (view/index-page (db/get-entries)))

(defroutes app-routes
  (GET "/" [] (str (main-page)))
  (GET "/rss/engadget" [] (str (view/engadget-page fetch-engaget-reddit)))
  (GET "/admin/rss/new" [] (str (view/admin-add-rss)))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
