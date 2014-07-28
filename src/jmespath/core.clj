(ns jmespath.core
  "Parses JMESPath expressions"
  {:author "Michael Dowling"}
  (:use jmespath.tree)
  (:require [instaparse.core :as insta]))

(def ^:private parser
  (insta/parser
    "<expression>          = complex-expression | top-level-expression | current-node
     <top-level-expression>= pipe-expression
     <complex-expression>  = simple-expression | boolean-expression | multi-select-list | multi-select-hash
     <simple-expression>   = identifier | sub-expression | index-expression | function-expression
     current-node          = <'@'>
     function-expression   = identifier <'('> function-args <')'>
     function-args         = expression | expression (<','> expression)*
     pipe-expression       = expression <'|'> complex-expression
     sub-expression        = simple-expression <'.'> (identifier | multi-select-list | multi-select-hash | function-expression)
     index-expression      = [simple-expression] <'['> number <']'>
     multi-select-list     = <'['> expression (<','> expression)* <']'>
     multi-select-hash     = <'{'> keyval-expr (<','> keyval-expr)* <'}'>
     keyval-expr           = identifier <':'> expression
     <boolean-expression>  = or-expression | and-expression | not-expression
     or-expression         = or-clause (<'||'> or-clause)+
     or-clause             = simple-expression | and-expression | not-expression
     and-expression        = simple-expression (<'&&'> simple-expression)+
     identifier            = unquoted-string
     not-expression        = <'!'> simple-expression
     number                = '-'* digit+
     <digit>               = '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
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
