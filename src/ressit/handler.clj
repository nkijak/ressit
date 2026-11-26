(ns ressit.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            ;[hiccup.middleware :refer [wrap-base-url]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.util.response :as rresp]
            [ressit.views :as view]
            [prolefeed :as pf]
            [ressit.db :as db]))

(defn- main-page []
  (view/index-page (db/get-entries)))

(defn- db-to-feed [db-entries]
  (let [e (first db-entries)
        {:keys [feed_title feed_description]} e]
    {:title feed_title :description feed_description :entries db-entries}))

(defroutes app-routes
  (GET "/" [] (str (main-page)))
  (GET "/rss/:name" [name]
    (println "showing feed page for" name)
    (->> (db/feed-with-entries name)
         db-to-feed
         (view/feed-page name)
         str))
  (GET  "/admin/rss/new" {token :anti-forgery-token} (str (view/admin-add-rss token)))

  ; these are strings when they come from form-params and keywords when coming as params
  ; also i need the :anti-forgery-token from the request probably to create a new request here...
  (POST "/admin/rss/add" {:keys [anti-forgery-token] {:strs [url name]} :form-params}
    (->> (pf/fetch url)
         (view/preview-rss-feed name url anti-forgery-token)
         (str)))

  (POST "/admin/rss" {{:strs [url name]} :form-params}
    (println "saving " url " as " name)
    (db/save-feed! url name)
    (rresp/redirect (str "/rss/" name)))

  (route/not-found "Not Found"))

; when i finally need API (json only) endpoints this will help https://github.com/ska2342/ring-routes-demo/
(def app
  (wrap-defaults #'app-routes site-defaults))
