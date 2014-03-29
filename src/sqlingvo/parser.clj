(ns sqlingvo.parser
  (:require [instaparse.core :as insta]))

(def whitespace
  (insta/parser
   "whitespace = #'\\s+'"))

(def select
  (insta/parser
   "select = 'SELECT' distinct-clause? select-fields from-clause?
    distinct-clause = (('ALL' | 'DISTINCT'))?
    select-fields = ('*' | select-field (',' select-field)*)
    select-field = (expression ('AS' output-name)?)
    from-clause = 'FROM' from-item
    from-item = #'[a-zA-Z0-9]+'
    expression = #'[a-zA-Z0-9]+'
    output-name = #'[a-zA-Z0-9]+'"
   :auto-whitespace whitespace))
