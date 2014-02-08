(ns nidaba.database
  (:require [clojure.java.jdbc :as sql]))

(def db {:subprotocol "mysql"
         :subname "//localhost:3306/nidaba"
         :user "lisp-god"
         :password "123456"})

(defn create-a-table []
  (sql/db-do-commands
    db
    (sql/create-table-ddl
     :enterprise
     [:id :integer "PRIMARY KEY" "AUTO_INCREMENT"]
     [:series :text]
     [:captain :text]
     [:first_officer :text] ;; no dashes with sql columns ...
     )))

(defn insert-some-data [{series :series captain :captain first-officer :first_officer}]
  (sql/insert!
   db
   :enterprise
   [:series :captain :first_officer]
   [series captain first-officer]))

#_(create-a-table)
(def test-data [{:series "tng" :captain "picard" :first_officer "riker"}
                {:series "original" :captain "kirk" :first_officer "spock"}])

#_(doall (map insert-some-data test-data))
