(ns sqlingvo.vendor
  (:require [sqlingvo.util :refer :all]))

(defprotocol Keywordable
  (sql-keyword [vendor x]))

(defprotocol Nameable
  (sql-name [vendor x]))

(defprotocol Quoteable
  (sql-quote [vendor x]))

(defn vendor?
  "Returns true if `x` satisfies as vendor, otherwise false."
  [x]
  (and (satisfies? Keywordable x)
       (satisfies? Nameable x)
       (satisfies? Quoteable x)))

(defmacro defvendor [name doc & {:as opts}]
  `(defrecord ~name []
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
  "The world's most popular open source database."
  :name sql-name-underscore
  :keyword sql-keyword-hyphenize
  :quote sql-quote-backtick)

(defvendor postgresql
  "The world's most advanced open source database."
  :name sql-name-underscore
  :keyword sql-keyword-hyphenize
  :quote sql-quote-double-quote)

(defvendor oracle
  "Oracle Database."
  :name sql-name-underscore
  :keyword sql-keyword-hyphenize
  :quote sql-quote-double-quote)

(defvendor sqlite
  "The in-process SQL database engine."
  :name sql-name-underscore
  :keyword sql-keyword-hyphenize
  :quote sql-quote-double-quote)

(defvendor sqlserver
  "Microsoft SQL server."
  :name sql-name-underscore
  :keyword sql-keyword-hyphenize
  :quote sql-quote-double-quote)

(defvendor vertica
  "The Real-Time Analytics Platform."
  :name sql-name-underscore
  :keyword sql-keyword-hyphenize
  :quote sql-quote-double-quote)
