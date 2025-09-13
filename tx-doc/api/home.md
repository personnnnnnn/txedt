# Txedt Lisp API

Here you will see just a bunch of function/macro definitions
that may or not link an example making use of the mentioned
symbol.

> As you can see, this page is a work in progress.

## `std` package

### `(defn std:+ (x &rest xs))`
### `(defn std:- (x &rest xs))`
### `(defn std:* (x &rest xs))`
### `(defn std:/ (x &rest xs))`
### `(defn std:% (x &rest xs))`
### `(defn std:cat (x &rest xs))`
###
### `(defmac std:var ( (symbol var-name) (optional (expr value)) ))`
### `(defmac std:set ( (symbol var-name) (expr value) ))`
###
### `(defmac std:prog ( (body stmts) ))`
### `(defmac std:progn ( (body stmts) ))`
### `(defmac std:progp ( (body stmts) ))`

## `io` package

### `(defn io:print (msg))`
### `(defn io:input ())`
