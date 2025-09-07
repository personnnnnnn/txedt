# Txedt lisp

I find it is easier to learn with examples, so
here are some:

### Hello, World
```lisp
(print "Hello, World!")
```

### If / When / Unless / Cond
```lisp
(print (if t
    "when true"
    "when false"))

(print (when t "when true")) ; outputs nil if false
(print (unless nil "when false")) ; outputs nil if true

(var x (input))

(cond
    ((= x "a") (print "A"))
    ((= x "b") (print "B"))
    (t (print "else")))
```

### Loops
```lisp
; prints the numbers 1-10 in ascending order

(var i 0)
(loop
    (when (= i 10)
        (return)) ; exits out of the 'loop' block
    (set i (1+ i))
    (print i))
```

### Functions and macros
```lisp
(fn x2-func (n)
    (* 2 n))

(mac x2-mac (n)
    (ast (* 2 ,n)))

(print (x2-func 2)) ; 2
(print (x2-mac 2)) ; 2 (but fancier)
```

### Function recursion and optional parameters
```lisp
(fn countdown (i)
    (if (<= i 1)
        (cat i) ; concat
        (cat i " " (countdown (1- i)))))

(fn countup (&optional i max)
    (unless (given i) (var i 1))
    (if (>= i max)
        (cat i)
        (cat i " " (countup (1+ i) max))))

(print (countup 10)) ; 1 2 3 ... 10
(print (countdown 10)) ; 10 9 8 ... 1
```

### Variable contexts as core data-types

```lisp
(var global-context (current-context))
(var global-context:x 0)
(print x) ; 0

(var ctx-a (progn
    (var y 5)
    (current-context)))

(var ctx-b (progn
    (var z 3)
    (current-context)))

(set (context-parent ctx-b) ctx-a)
(print ctx-b:x) ; 0
(print ctx-b:y) ; 5
(print ctx-b:z) ; 3
```

### Libraries and packages

A package is a context that doesn't allow 'x:y' syntax to read values from its parent
(unless that parent is also a package).

Think of it not as a package of code, but as a package of data.

```lisp
; this use-case only really makes sense for libraries
; but for the sake of the example...
(package pkg
    (var x 5)
    (var y 7))

(print pkg:x) ; 5
(print pkg:y) ; 7
(print pkg:+) ; errors, since '+' is defined in the parent scope, not the scope itself
```

It's used more when defining data structures, a bit like this:

```lisp
(fn parse (str)
    (var failed nil)
    (var result nil)
    (var error-message nil)
    ; ... imagine parser code here ...
    (package
        (var ok (not failed))
        (var value result)
        (var error error-message)))

(var result (parse (input)))
(if result:ok
    (print (cat "Success: " result:value))
    (print (cat "Failure: " result:error-message)))

(print result:failed) ; would error beacuse it's defined in the parent scope of the package
```

A library is a context that only gives variables on demand and that doesn't allow 'x:y' syntax to read values from its parent.

```lisp
(library l1
    (var message (cat "Hello, " l2:person))
    ; export tells any users that the given variable is ready to be used
    ; (or omit it to make it private)
    (export message))

(library l2
    ; you can also do this
    (export var person "World!")
    
    ; (it works for any function in theory, it only assumes that the 1st
    ; symbol after the function name corresponds to the name of the variable
    ; you're trying to export)
    (export fn greet ()
        (print l1:message))
    
    (print "Job's done!"))

(l2:greet) ; Hello World!
; "Job's done!" is never printed because only the part up to 'greet' is executed.
```
