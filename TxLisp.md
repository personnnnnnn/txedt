# Txedt lisp

I find it is easier to learn with examples, so
here are some:

### Hello, World
```lisp
(print "Hello, World!")
```

### If / When / Unless
```lisp
(print (if t
    "when true"
    "when false"))

(print (when t "when true")) ; outputs nil if false
(print (unless nil "when false")) ; outputs nil if true
```

### ""While"" Loops
```lisp
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
    (mac-out (* 2 ,n)))

(print (x2-func 2)) ; 2
(print (x2-mac 2)) ; 2
```

### Functions recursion and optional parameters
```lisp
(fn countdown (i)
    (if (<= i 1)
        (cat i)
        (cat i " " (countdown (1- i)))))

(fn countup (&optional i max)
    (unless (given i) (var i 1))
    (if (>= i max)
        (cat i)
        (cat i " " (countup (1+ i) max))))

(print (countup 10))
(print (countdown 10))
```
