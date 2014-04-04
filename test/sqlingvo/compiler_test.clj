(ns sqlingvo.compiler-test
  (:require [clojure.test :refer :all]
            [sqlingvo.compiler :refer :all]
            [sqlingvo.util :refer [parse-tables]]
            [sqlingvo.vendor :refer [->postgresql]]))

(def db (->postgresql))

(deftest test-compile-column
  (are [ast expected]
    (is (= expected (compile-stmt db ast)))
    {:op :column :name :*}
    ["*"]
    {:op :column :table :continents :name :*}
    ["\"continents\".*"]
    {:op :column :name :created-at}
    ["\"created_at\""]
    {:op :column :table :continents :name :created-at}
    ["\"continents\".\"created_at\""]
    {:op :column :schema :public :table :continents :name :created-at}
    ["\"public\".\"continents\".\"created_at\""]
    {:op :column :schema :public :table :continents :name :created-at :as :c}
    ["\"public\".\"continents\".\"created_at\" AS \"c\""]))

(deftest test-compile-constant
  (are [ast expected]
    (is (= expected (compile-stmt db ast)))
    {:op :constant :form 1}
    ["1"]
    {:op :constant :form 3.14}
    ["3.14"]
    {:op :constant :form "x"}
    ["?" "x"]))

(deftest test-compile-sql
  (are [ast expected]
    (is (= expected (compile-sql db ast)))
    {:op :nil}
    ["NULL"]
    {:op :constant :form 1}
    ["1"]
    {:op :keyword :form :continents.created-at}
    ["\"continents\".\"created_at\""]
    {:op :fn :name 'max :args [{:op :keyword :form :created-at}]}
    ["max(\"created_at\")"]
    {:op :fn :name 'greatest :args [{:op :constant :form 1} {:op :constant :form 2}]}
    ["greatest(1, 2)"]
    {:op :fn :name 'ST_AsText :args [{:op :fn :name 'ST_Centroid :args [{:op :constant :form "MULTIPOINT(-1 0, -1 2, -1 3, -1 4, -1 7, 0 1, 0 3, 1 1, 2 0, 6 0, 7 8, 9 8, 10 6)"}]}]}
    ["ST_AsText(ST_Centroid(?))" "MULTIPOINT(-1 0, -1 2, -1 3, -1 4, -1 7, 0 1, 0 3, 1 1, 2 0, 6 0, 7 8, 9 8, 10 6)"]))

(deftest test-compile-drop-table
  (are [ast expected]
    (is (= expected (compile-sql db ast)))
    {:op :drop-table
     :children [:tables]
     :tables (parse-tables [:continents])}
    ["DROP TABLE \"continents\""]
    {:op :drop-table
     :children [:tables :cascade]
     :tables (parse-tables [:continents])
     :cascade {:op :cascade :condition true}}
    ["DROP TABLE \"continents\" CASCADE"]
    {:op :drop-table
     :children [:tables :restrict]
     :tables (parse-tables [:continents])
     :restrict {:op :restrict :condition true}}
    ["DROP TABLE \"continents\" RESTRICT"]
    {:op :drop-table
     :children [:if-exists :tables]
     :if-exists {:op :if-exists :condition true}
     :tables (parse-tables [:continents])}
    ["DROP TABLE IF EXISTS \"continents\""]
    {:op :drop-table
     :children [:if-exists :tables :cascade :restrict]
     :if-exists {:op :if-exists :condition true}
     :tables (parse-tables [:continents])
     :cascade {:op :cascade :condition true}
     :restrict {:op :restrict :condition true}}
    ["DROP TABLE IF EXISTS \"continents\" CASCADE RESTRICT"]))

(deftest test-compile-limit
  (are [ast expected]
    (is (= expected (compile-sql db ast)))
    {:op :limit :count 1}
    ["LIMIT 1"]
    {:op :limit :count nil}
    ["LIMIT ALL"]))

(deftest test-compile-offset
  (are [ast expected]
    (is (= expected (compile-sql db ast)))
    {:op :offset :start 1}
    ["OFFSET 1"]
    {:op :offset :start nil}
    ["OFFSET 0"]))

(deftest test-compile-table
  (are [ast expected]
    (is (= expected (compile-sql db ast)))
    {:op :table :name :continents}
    ["\"continents\""]
    {:op :table :schema :public :name :continents}
    ["\"public\".\"continents\""]
    {:op :table :schema :public :name :continents :as :c}
    ["\"public\".\"continents\" AS \"c\""]))

(deftest test-wrap-stmt
  (are [stmt expected]
    (is (= expected (wrap-stmt stmt)))
    ["SELECT 1"]
    ["(SELECT 1)"]
    ["SELECT ?" "x"]
    ["(SELECT ?)" "x"]))

(deftest test-unwrap-stmt
  (are [stmt expected]
    (is (= expected (unwrap-stmt stmt)))
    ["(SELECT 1)"]
    ["SELECT 1"]
    ["(SELECT ?)" "x"]
    ["SELECT ?" "x"]))

(deftest test-compile-tables
  (are [node expected]
    (= expected (compile-sql db node))
    {:op :tables
     :children [:tables]
     :tables [{:children [:name] :name :continents :op :table}]}
    ["\"continents\""]
    {:op :tables
     :children [:tables]
     :tables [{:children [:name] :name :continents :op :table}
              {:children [:name] :name :countries :op :table}]}
    ["\"continents\", \"countries\""]))
