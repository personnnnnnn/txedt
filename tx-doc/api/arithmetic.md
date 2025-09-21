# Arithmetic operators

## Standard math functions

They are your standard operations:
- Addition `+`
- Subtraction `-`
- Multiplication `*`
- Division `/`
- Floor division `floor/`
- Ceiling division `ceil/`
- Rounded division `round/`
- Modulo `%` (does not preserve the sign, always results in numbers >= 0)
- Signed module `%-` (preserves the sign -- pass in a negative, you get a negative)

> NOTE: when dividing or modulo-ing by 0, the result will also be 0.

They all take at least one parameter, and then apply the operation
repeatedly with the other given arguments.

> NOTE: if there is only one argument, it will just be returned
> and won't error, even if it is not a number.

## Functions that work similarly

## Cat
`(defn std:cat (x &rest xs))`

Returns a string made by concatenating
all the given values in order.

> NOTE: unlike `+`, `-` or `*`, `cat` actually does something when only
> given one input -- it converts it into a string, so it also acts as
> your to-string function.
