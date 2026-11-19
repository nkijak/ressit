(ns ressit.crawler
  (:require
   [ressit.db :as db]
   [prolefeed :as pf]
   [clojure.pprint :refer [pprint]]))

(defn store-newer-entries [{:keys [last_published id], :as feed-meta} rss]
  (pprint feed-meta)
  (println "looking for entries newer than " last_published)
  (doseq [entry (:entries rss)]
    (let [entryts (.getTime (:publishedDate entry))
          feedts (.toEpochSecond last_published)]
      (when (> entryts feedts)
        (db/save-entry id entry)))))

; TODO i need the top level information stored too -- probably on feed creation
(defn start-consuming-feeds []
  (doseq [feed (db/get-feeds)]
    (println "getting " (:url feed))
    (->> (pf/fetch (:url feed))
         (store-newer-entries feed))))

