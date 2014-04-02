(ns sqlingvo.parser
  (:require [instaparse.core :as insta]))

(def whitespace
  (insta/parser
   "whitespace = #'\\s+'"))

(def sql-parser
  (insta/parser
   "sql = select
    select = 'SELECT' distinct-clause? select-fields from-clause?
    distinct-clause = (('ALL' | 'DISTINCT'))?
    select-fields = ('*' | select-field (',' select-field)*)
    select-field = (expression ('AS' output-name)?)
    from-clause = 'FROM' from-item
    from-item = #'[a-zA-Z0-9]+'
    expression = #'[a-zA-Z0-9]+'
    output-name = #'[a-zA-Z0-9]+'"
   :auto-whitespace whitespace))

(def postgresql-parser
  (insta/parser
   "select-stmt = select-no-parens | select-with-parens
    select-with-parens = '(' select-no-parens ')' | '(' select-with-parens ')'
    select-no-parens = simple-select | select-clause sort-clause
    select-clause = simple-select | select-with-parens
    simple-select = 'SELECT' opt-distinct target-list into-clause from-clause where-clause group-clause having-clause window-clause
    opt-distinct = 'DISTINCT' | 'DISTINCT ON' '(' expr-list ')' | 'ALL'
    expr-list = a-expr | expr-list ',' a-expr
    a-expr = c-expr
      | a-expr 'TYPECAST' typename
      | a-expr 'COLLATE' any-name
      | a-expr 'AT TIME ZONE' a-expr
      | '+' a-expr
      | '-' a-expr
      | a-expr '+' a-expr
      | a-expr '-' a-expr
      | a-expr '*' a-expr
      | a-expr '/' a-expr
      | a-expr '%' a-expr
      | a-expr '^' a-expr
      | a-expr '<' a-expr
      | a-expr '>' a-expr
      | a-expr '=' a-expr
      | a-expr qual-op a-expr
      | qual-op a-expr
      | a-expr qual-op
      | a-expr 'AND' a-expr
      | a-expr 'OR' a-expr
      | 'NOT' a-expr
      | a-expr 'LIKE' a-expr
      | a-expr 'LIKE' a-expr 'ESCAPE' a-expr
      | a-expr 'NOT LIKE' a-expr
      | a-expr 'NOT LIKE' a-expr 'ESCAPE' a-expr
      | a-expr 'ILIKE' a-expr
      | a-expr 'ILIKE' a-expr 'ESCAPE' a-expr
      | a-expr 'NOT ILIKE' a-expr
      | a-expr 'NOT ILIKE' a-expr 'ESCAPE' a-expr
      | a-expr 'SIMILAR TO' a-expr
      | a-expr 'SIMILAR TO' a-expr 'ESCAPE' a-expr
      | a-expr 'NOT SIMILAR TO' a-expr
      | a-expr 'NOT SIMILAR TO' a-expr 'ESCAPE' a-expr
    qual-op =  #'.*'
    any-name = #'.*'
    c-expr = #'.*'
    typename = #'.*'
    target-list = #'.*'
    into-clause = #'.*'
    from-clause = #'.*'
    where-clause = #'.*'
    group-clause = #'.*'
    having-clause = #'.*'
    sort-clause = #'.*'
    window-clause = #'.*'"
   :auto-whitespace whitespace))
