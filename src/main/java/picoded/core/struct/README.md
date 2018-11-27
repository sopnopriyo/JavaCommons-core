# core.struct

Common reusable data structures, and interfaces for multiple use case.

Data Structures here are not created for performance optimization (they only need to be "good enough"),
And are meant more for easy code reuse and developer convinence.

Almost all data structures here are either based of standard java List, or Map

## Subpackages

+ `core.struct.template.*` convinence default implmentation for supporting java lang Map / List interfaces, for quick prototyping of such compliant custom classes.
+ `core.struct.query.*` interface and class implmentation, of in java query over a collection of maps, using SQL-like query language

---

## Core GenericConvert Interfaces
Interfaces which applies the entire suite of `core.conv.GenericConvert` commands onto a Map / List class.
Most of the subsequent usage of the Map / List classes should use this as the declared variable type, for better cross implementation support.

+ `GenericConvertMap` for `java.util.Map` interface
+ `GenericConvertList` for `java.util.List` interface

---

## Utility interfaces (not generic convert based)
Interfaces which function independently from GenericConvert implementation.


---

## GenericConvert Map Classes
Class implmentation which applies `GenericConvertMap` interface, into a working map implementation

+ `GenericConvertHashMap` for `java.util.HashMap`
+ `GenericConvertConcurrentHashMap` for `java.util.concurrent.ConcurrentHashMap`
+ `CaseInsensitiveHashMap` for a (lowercase) case insensitive varient of `java.util.HashMap`
+ `ProxyGenericConvertMap`

## GenericConvert List Classes
Class implementation which applies `GenericConvertList` interface, into a working list implementation

+ `GenericConvertArrayList` for `java.util.ArrayList`
+ `MutablePair` for `org.apache.commons.lang3.tuple.MutablePair` and provides an easy to use "data pair", which can be accessed as a list of size 2
+ `ProxyGenericConvertList`

---

## Experimental

> The following classes / interfaces is clasified as experimental, usage should be avoided, unless there is a **strong** use case for it.
> 
> Experimental classes will be promoted to be part of the library standard, if strong use cases sustains it. 
> Else it maybe deprecated in the future

+ `GenericConvertValue` interface which applies the entire suite of `core.conv.GenericConvert` commands onto a value holder object
