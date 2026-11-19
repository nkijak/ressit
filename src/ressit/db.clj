(ns ressit.db
  (:require [pg.core :as pg]
            [pg.pool :as pool]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as str :only [join]]))

; TODO get this from .. edn file? 
(def config
  {:host "127.0.0.1",
   :port 5432
   :user "postgres"
   :password (System/getenv "RESSIT_DB_PASSWORD")
   :database "ressit"})

(defn get-feeds
  ([] (get-feeds 0 0))
  ([page limit]
   (let [offset (* page limit)
         limit-clause (when (pos? limit) (str " limit " limit))
         offset-clause (when (pos? offset) (str " offset " offset))
         stmt (str "
                   with last_published as (
                                           select feed_id, max(published_at) as last_published 
                                           from entry 
                                           group by feed_id)
                   select f.id, f.url, COALESCE(lp.last_published, TIMESTAMP '1970-01-01 00:00:00+00') last_published
                    from feed f 
                    left join last_published as lp 
                    on lp.feed_id = f.id
                   "
                   (when (some? limit-clause) limit-clause)
                   (when (some? offset-clause) offset-clause))]
     (println limit-clause)
     (println offset-clause)
     (println stmt)
     (pg/with-pool [pool config]
       (pg/query pool stmt)))))

; TODO spelled description wrong in the database
(defn save-entry [feed-id entry]
  (pg/with-pool [pool config]
    (pg/execute pool "
            insert into entry (feed_id, link, title, decription, contents, published_at, updated_at)
            values ($1, $2, $3, $4, $5, $6, $7)
                "
                {:params [feed-id
                          (:link entry)
                          (:title entry)
                          (:description entry)
                          (str/join "\n" (:contents entry))
                          (:publishedDate entry)
                          (:updatedDate entry)]})))

; :entry {:fields [[:id :serial {:primary-key true}]
;                  [:feed-id :integer {:foreign-key :feed/id}]
;                  [:author-id :integer {:foreign-key :author/id}]
;                  [:link :text {:null false}]
;                  [:title :text {:null false}]
;                  [:decription :text]
;                  [:contents :text]
;                  [:published-at :timestamptz {:null false}]
;                  [:updated-at :timestamptz]]
;(:author
; :link
; :publishedDate
; :title
; :updatedDate
; :uri
; :description
; :contents)

(defn get-entries []
  (pg/with-pool [pool config]
    (pg/execute pool "
      select e.*, f.url from entry e 
      join feed f on e.feed_id = f.id 
      where published_at >= date_trunc('day', now()) 
      order by published_at desc
    ")))
