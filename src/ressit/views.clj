(ns ressit.views
  (:require [hiccup2.core :as h]))

(defn ressit-page [title & content]
  (h/html {:escape-strings? false} (h/raw "<!DOCTYPE html>")
          [:html {:lang "en" :data-theme "light"}
           [:head
            [:meta {:charset "utf-8"}]
            [:meta {:name "viewport" :content "width=device-width, iniital-scale=1"}]
            [:meta {:name "color-scheme" :content "light dark"}]
            [:title "Ressit | " title]
            [:link {:rel "stylesheet" :href "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"}]
            [:link {:rel "stylesheet" :href "https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.classless.pumpkin.min.css"}]
            [:style (h/raw "
                           .stats {
                              display: grid;
                              grid-template-columns: repeat(4, 1fr);
                              grid-template-rows: 1fr;
                              grid-column-gap: 10px;
                              grid-row-gap: 0px;
                            }
                            ")]]

           [:body
            [:header "ressit blah blah"]
            [:main content]
            [:footer "Copyright (c) 2026 Author. All Rights Reserved."]]]))

(defn index-page []
  (h/html
   [:head
    [:title "Ressit"]]
   [:body
    [:h1 "Hello World"]]))

(defn entry-block [entry]
  [:article
   [:h2 (:title entry)]
   [:p (:contents entry)
    [:cite (:author entry)]
    [:br]
    [:a {:href (:link entry)} "more"]]
   [:footer.stats
    [:div
     [:a {:href "comments"}
      [:i.fa.fa-comments {:aria-hidden "true"}]]
     [:small 0]]
    [:div
     [:a {:href "views"}
      [:i.fa.fa-eye {:aria-hidden "true"}]]
     [:small 0]]
    [:div
     [:a {:href "upvotes"}
      [:i.fa.fa-arrow-up {:aria-hidden "true"}]]
     [:small 0]]
    [:div
     [:a {:href "downvotes"}
      [:i.fa.fa-arrow-down {:aria-hidden "true"}]]
     [:small 0]]]])

(defn engadget-page [refreshf]
  (let [feed (refreshf)]
    (ressit-page (:title feed)
                 [:h1 (:title feed)]
                 [:p (:description feed)]
                 [:div
                  (map entry-block (:entries feed))])))


