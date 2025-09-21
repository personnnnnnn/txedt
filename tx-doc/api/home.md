# Txedt Lisp API

Here you will see just a bunch of function/macro definitions
that may or not link an example making use of the mentioned
symbol.

> As you can see, this page is a work in progress.

## `std` package

### `(var std:nil)`
### `(var std:t)`
###
### `(defn std:1+ (x))`
### `(defn std:1- (x))`
### `(defn std:-1* (x))`
### `(defn std:fraction (x))`
### `(defn std:abs (x))`
###
### [`(defn std:+ (x &rest xs))`](arithmetic.md#standard-math-functions)
### [`(defn std:- (x &rest xs))`](arithmetic.md#standard-math-functions)
### [`(defn std:* (x &rest xs))`](arithmetic.md#standard-math-functions)
### [`(defn std:/ (x &rest xs))`](arithmetic.md#standard-math-functions)
### [`(defn std:floor/ (x &rest xs))`](arithmetic.md#standard-math-functions)
### [`(defn std:ceil/ (x &rest xs))`](arithmetic.md#standard-math-functions)
### [`(defn std:round/ (x &rest xs))`](arithmetic.md#standard-math-functions)
### [`(defn std:% (x &rest xs))`](arithmetic.md#standard-math-functions)
### [`(defn std:%- (x &rest xs))`](arithmetic.md#standard-math-functions)
###
### `(defn std:<< (x &rest xs))`
### `(defn std:>> (x &rest xs))`
### `(defn std:>>- (x &rest xs))`
### `(defn std:& (x &rest xs))`
### `(defn std:&~ (x &rest xs))`
### `(defn std:| (x &rest xs))`
### `(defn std:^ (x &rest xs))`
### `(defn std:~ (x))`
###
### [`(defn std:cat (x &rest xs))`](arithmetic.md#cat)
###
### `(defn std:is (x y &rest xs))`
### `(defn std:isnt (x y &rest xs))`
### `(defn std:= (x y &rest xs))`
### `(defn std:!= (x y &rest xs))`
###
### `(defn std:< (x y &rest xs))`
### `(defn std:> (x y &rest xs))`
### `(defn std:<= (x y &rest xs))`
### `(defn std:>= (x y &rest xs))`
###
### `(defn std:not (x))`
### `(defn std:and (x y &rest xs))`
### `(defn std:or (x y &rest xs))`
### `(defn std:xor (x y &rest xs))`
###
### `(defn std:zero? (x))`
### `(defn std:non-zero? (x))`
### `(defn std:<0? (x))`
### `(defn std:>0? (x))`
### `(defn std:<=0? (x))`
### `(defn std:>=0? (x))`
###
### `(defmac std:var ( (symbol var-name) (optional (expr value)) ))`
### `(defmac std:set ( (choose (variable (symbol var-name)) (property (sublist (expr prop) (body args)))) (expr value) ))`
###
### `(defmac std:prog ( (body stmts) ))`
### `(defmac std:progn ( (body stmts) ))`
### `(defmac std:progp ( (body stmts) ))`
###
### `(defmac std:block ( (symbol block-name) (body stmts) ))`
### `(defmac std:blockn ( (symbol block-name) (body stmts) ))`
### `(defmac std:blockp ( (symbol block-name) (body stmts) ))`
###
### `(defmac std:this-ctx ( ))`
###
### `(defmac std:loop ( (body stmts) ))`
### `(defmac std:loopb ( (symbol block-name) (body stmts) ))`
###
### `(defmac std:if ( (expr cond) (expr if-true) (expr if-false) ))`
### `(defmac std:when ( (expr cond) (body when-true) ))`
### `(defmac std:unless ( (expr cond) (body when-false) ))`
### `(defmac std:cond ( (many options (sublist (expr condition) (body return-value) )) ))`
###
### `(defmac std:defn ( (symbol function-name) (sublist (function-args args)) (body stmts) ))`
### `(defmac std:fn ( (sublist (function-args args)) (body stmts) ))`
###
### `(defmac std:return ( (optional (expr value)) )`
### `(defmac std:return-from ( (symbol target) (optional (expr value)) )`
###
### `(defmac std:given ( (symbol var-name) ))`
### `(defmac std:exists ( (symbol var-name) ))`
###
### `(defn std:list (&rest items))`
### `(defprop std:list-idx &get ((list idx)) &set ((list idx value)))`
### `(defprop std:list-len &get ((list)) &set ((list &optional fill value)))`
###
### `(defn std:str-len (s))`
### `(defn std:str-idx (s i))`
### `(defn std:str-code (s &optional i))`
### `(defn std:code-str (i))`
###
### `(defn std:zero? (x))`
### `(defn std:non-zero? (x))`
### `(defn std:<0? (x))`
### `(defn std:>0? (x))`
### `(defn std:<=0? (x))`
### `(defn std:>=0? (x))`
###
### `(defn std:round (x))`
### `(defn std:floor (x))`
### `(defn std:ceil (x))`
###
### `(defn std:int (x))`
### `(defn std:float (x))`
###
### `(defn std:nil? (x))`
### `(defn std:non-nil? (x))`
### `(defn std:int? (x))`
### `(defn std:float? (x))`
### `(defn std:number? (x))`
### `(defn std:string? (x))`
### `(defn std:list? (x))`
### `(defn std:context? (x))`
###
### `(defmac std:let ( (sublist (many (choice (var-mention (sybol name)) (var-def (symbol name) (expr value))))) (body stmts) ))`
### `(defmac std:let* ( (sublist (many (choice (var-mention (sybol name)) (var-def (symbol name) (expr value))))) (body stmts) ))`

## `io` package
> This package will probably be removed once the `windowing` package is perfected and I make a proper logging utility
> directly with it.

### `(defn io:print (msg))`
### `(defn io:input ())`

## `windowing` package

### `(defn windowing:create ())`
### `(defmac windowing:draw-to ( (expr window) (body stmts) ))`
### `(defn windowing:rect (window x y width height color))`
### `(defn windowing:text (window font alignment text x y color))`
### `(defn windowing:color (r g b &optional a))`
### `(defn windowing:width (window))`
### `(defn windowing:height (window))`
### `(defn windowing:center-x (window))`
### `(defn windowing:center-y (window))`
### `(defn windowing:closed (window))`
### `(defprop windowing:title &get ((window)) &set ((window title)))`
### `(defn windowing:resize (window width height))`
### `(defn windowing:close (window))`
### `(defn windowing:ensure-closed (window))`
### `(defn windowing:clock ())`
### `(defn windowing:ensure-framerate (clock target-fps))`
### `(defn windowing:font (name style size))`
### `(defn windowing:derive-font-style (src style))`
### `(defn windowing:derive-font-size (src size))`
### `(defn windowing:derive-font (src style size))`
### `(var windowing:normal)`
### `(var windowing:italic)`
### `(var windowing:bold)`
### `(var windowing:bold-italic)`
### `(var windowing:left)`
### `(var windowing:right)`
### `(var windowing:middle)`
### `(var windowing:top)`
### `(var windowing:bottom)`
### `(var windowing:center)`
