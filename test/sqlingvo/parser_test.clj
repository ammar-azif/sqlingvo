(ns sqlingvo.parser-test
  (:require [sqlingvo.parser :refer :all]
            [clojure.test :refer :all]))

(deftest test-sql-parser
  (are [sql expected]
    (is (= [:sql expected] (sql-parser sql)))
    "SELECT 1"
    [:select "SELECT"
     [:select-fields
      [:select-field [:expression "1"]]]]
    "SELECT 1, 2, 3"
    [:select "SELECT"
     [:select-fields
      [:select-field [:expression "1"]] ","
      [:select-field [:expression "2"]] ","
      [:select-field [:expression "3"]]]]
    "SELECT * FROM countries"
    [:select "SELECT"
     [:select-fields "*"]
     [:from-clause "FROM"
      [:from-item "countries"]]]))
