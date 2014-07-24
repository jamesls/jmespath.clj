(ns jmespath.core
  "Parses JMESPath expressions"
  {:author "Michael Dowling"}
  (:use jmespath.tree)
  (:require [instaparse.core :as insta]))

(def ^:private parser
  (insta/parser
    "<expression>          = sub-expression | identifier | index-expression | multi-select-list | multi-select-hash
     identifier            = unquoted-string | quoted-string
     multi-select-list     = <'['> expression (<','> expression)* <']'>
     multi-select-hash     = <'{'> keyval-expr (<','> keyval-expr)* <'}'>
     keyval-expr           = identifier <':'> expression
     unquoted-string       = #'[A-Za-z]+[0-9A-Za-z_]*'
     quoted-string         = <'\"'> #'(?:\\|\\\"|[^\"])*' <'\"'>
     index-expression      = expression bracket-specifier | bracket-specifier
     bracket-specifier     = <'['> number <']'>
     number                = '-'* digit+
     <digit>                 = '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
     sub-expression        = expression <'.'> (identifier | multi-select-list | multi-select-hash)"
    :auto-whitespace (insta/parser "whitespace = #'\\s+'")))

(defn parse [exp]
  "Parses a JMESPath expression into an AST"
  (->> (parser exp) (insta/transform {
    :number (comp read-string str)
    :digit str})))

(defn search [exp data]
  "Searches the provides data structures using a JMESPath expression"
  (interpret (parse exp) data))

(defn check [exp]
  (insta/parses parser exp))
