(ns sqlingvo.vendor
  (:require [inflections.core :refer [hyphenize underscore]]))

(defprotocol Keywordable
  (sql-keyword [vendor x]))

(defprotocol Nameable
  (sql-name [vendor x]))

(defprotocol Quoteable
  (sql-quote [vendor x]))

(def sql-name-underscore
  (comp underscore name))

(def sql-keyword-hyphenize
  (comp keyword hyphenize))

(defn sql-quote-backtick [x]
  (str "`" x "`"))

(defn sql-quote-double-quote [x]
  (str "\"" x "\""))

(defmacro defvendor [name & {:as opts}]
  `(defrecord ~name [~'spec]
     Keywordable
     (sql-keyword [~'vendor ~'x]
       ((or ~(:keyword opts) sql-name-underscore) ~'x))
     Nameable
     (sql-name [~'vendor ~'x]
       ((or ~(:name opts) sql-keyword-hyphenize) ~'x))
     Quoteable
     (sql-quote [~'vendor ~'x]
       (~(or (:quote opts) sql-quote-backtick)
        (sql-name ~'vendor ~'x)))))

(defvendor mysql
  :name sql-name-underscore
  :keyword sql-keyword-hyphenize
  :quote sql-quote-backtick)

(defvendor postgresql
  :name sql-name-underscore
  :keyword sql-keyword-hyphenize
  :quote sql-quote-double-quote)

(defvendor sqlite
  :name sql-name-underscore
  :keyword sql-keyword-hyphenize
  :quote sql-quote-double-quote)

(defvendor vertica
  :name sql-name-underscore
  :keyword sql-keyword-hyphenize
  :quote sql-quote-double-quote)
