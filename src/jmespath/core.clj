(ns jmespath.core
  "Parses JMESPath expressions"
  {:author "Michael Dowling"}
  (:use jmespath.tree)
  (:require [instaparse.core :as insta]))

(def ^:private parser
  (insta/parser
    "<expression>          = simple-expression | boolean-expression
     <simple-expression>   = identifier | sub-expression
     sub-expression        = simple-expression <'.'> identifier
     <boolean-expression>  = or-expression | and-expression | not-expression
     or-expression         = or-clause (<'||'> or-clause)+
     or-clause             = simple-expression | and-expression | not-expression
     and-expression        = simple-expression (<'&&'> simple-expression)+
     identifier            = unquoted-string
     not-expression        = <'!'> simple-expression
     <unquoted-string>     = #'[A-Za-z]+[0-9A-Za-z_]*'"
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
