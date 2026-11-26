(ns ressit.views
  (:require [hiccup2.core :as h]
            [hiccup.form :as f]
            [clojure.string :as str]))

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
     (when (some? attribution) [:a {:href (str "/rss/" attribution)} [:kbd attribution]])
     [:i.fa.fa-hourglass-1 {:aria-hidden "true"}] [:small (get entry :published_at (get entry :publishedDate))]]
    [:h2 (:title entry)]
    [:p (first (filter #(not (str/blank? %)) (map entry [:contents :description])))
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

(defn feed-page [name feed]
  (ressit-page (:title feed)
               [:h1 (:title feed)]
               [:p (:description feed)]
               [:div
                (map #(entry-block % name) (:entries feed))]))

(defn index-page [entries]
  (ressit-page "Latest"
               [:h1 "Today"]
               [:div
                (map #(entry-block %1 (get %1 :name)) entries)]))

(defn admin-add-rss [anti-forgery-token]
  (ressit-page "New Feed"
               [:section#control
                [:form#feed_info
                 [:h2 "Add new Feed"]

                 [:label {:for "url"} "URL"]
                 [:input#url {:type "text"
                              :name "url"
                              :placeholder "https://whatever.com/rss.xml"
                              :data-bind "url"}]

                 [:label {:for "name"} "Tag Name"]
                 [:input#name {:type "text"
                               :name "name"
                               :placeholder "whatever"
                               :data-bind "name"}]
                 (f/hidden-field "__anti-forgery-token" anti-forgery-token)
                 [:button {:data-on:click "$preview=true; @post('/admin/rss/add', {contentType: 'form'})"
                           :data-attr:disabled "$url == '' || $name == ''"} "Fetch"]]]
               [:article#preview {:data-attr:aria-busy "$preview ? String($preview) : false"}]))

(defn preview-rss-feed [name url anti-forgery-token {:keys [title description entries]}]
  (h/html [:article#preview
           [:h1 title]
           [:p description]
           [:div
            (map #(entry-block %1 name) (take 3 entries))]]
          [:section#control
           [:form {:action "/admin/rss" :method "post"}
            (f/hidden-field "__anti-forgery-token" anti-forgery-token)
            (f/hidden-field "name" name)
            (f/hidden-field "url" url)
            (f/hidden-field "title" title)
            (f/hidden-field "description" description)
            [:input {:type "submit" :value (str "Save " name)}]]]))

