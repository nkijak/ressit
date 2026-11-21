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
                            ")]
            [:script {:type "module" :src "https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.5/bundles/datastar.js"}]]

           [:body
            [:header "ressit blah blah"]
            [:main content]
            [:footer "Copyright (c) 2026 Author. All Rights Reserved."]]]))

(defn entry-block
  ([entry] (entry-block entry nil))
  ([entry attribution]
   [:article
    [:div
     (when (some? attribution) [:kbd attribution])
     [:i.fa.fa-hourglass-1 {:aria-hidden "true"}] [:small (get entry :published_at (get entry :publishedDate))]]
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
      [:small 0]]]]))

(defn engadget-page [refreshf]
  (let [feed (refreshf)]
    (ressit-page (:title feed)
                 [:h1 (:title feed)]
                 [:p (:description feed)]
                 [:div
                  (map entry-block (:entries feed))])))

(defn- tag-from-url [url]
  (when (some? url)
    (.getHost (java.net.URL. url))))

(defn index-page [entries]
  (ressit-page "Latest"
               [:h1 "Today"]
               [:div
                (map #(entry-block %1 (tag-from-url (get %1 :url))) entries)]))

(defn admin-add-rss []
  (ressit-page "New Feed"
               [:section
                [:form#feed_info
                 [:h2 "Add new Feed"]

                 [:label {:for "url"} "URL"]
                 [:input#url {:type "text" :name "url" :placeholder "https://whatever.com/rss.xml"}]

                 [:label {:for "name"} "Tag Name"]
                 [:input#name {:type "text" :name "name" :placeholder "whatever"}]
                 [:button {:data-on:click "@post('/admin/rss/add', {contentType: 'form'})"} "Fetch"]]]
               [:section#preview
                [:div "Display here"]]))

