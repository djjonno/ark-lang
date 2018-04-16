 _ _ /
(// /(   🦒

# Ark Lang
Functional language with Lisp style syntax and some syntactic sugar for great readability.

#### Ark
The idea of creating a whole unified platform to build software is something I find to be super exciting.  I am to 
create a fully-fledged platform, end-to-end, including a comprehensive standard library.

I want to emphasize performance of the programmer and the interpreter by adopting a simple Lisp like, context-free 
grammar with a simple learning curve; We are aiming to keep the syntax light.

Ark will eventually be strongly typed and will be a good hybrid between classical imperative OOP and Functional language
styles - leaning more towards the functional paradigm - aim to get the best of both worlds.

#### My Motivations
###### Yet another hobbyist lang.

Why am I creating this language? Mostly to learn. I love de-mistifying things we programmers take for granted. 
Language implementation is no exception and there is so much to gain by having a better understanding of what goes into 
building a programming language and how they work.

Will this be production ready? Technically yes. The interpreter will eventually be written in C++ and should offer 
performance on par with something like Python. Even though I am building this language for learning purposes, I will not
be cutting corners just for the sake of getting something to work - it will be built correctly, for sure.

### Language Spec

##### Comments
```
;; this is a comment
```

##### Numeric Operations
```
;; addition
(+ 1 2) ;; 3
;; substraction
(- 1 2) ;; -1
```

```
;; multiplication
(* 2 3) ;; 6

;; division
(/ 6 3) ;; 2
```

```
;; modulo
(% 5 2) ;; 1
```

```
(< 1 2)  ;; true
(< 1 0)  ;; false
(> 1 0)  ;; true
(> 1 2)  ;; false
(>= 1 0) ;; true
(>= 1 1) ;; true
(>= 1 5) ;; false
(!= 1 2) ;; true
(!= 1 1) ;; false
```

##### Logic Operations
```
(and true false) ;; false
(and true true)  ;; true
(or true false)  ;; true
(or (> 1 2) (!= 1 3)) ;; true 
```

##### Collections

Iterate over collections using `item:collection` enumerator syntax.

```
let nums = [1,2,3,4]
(n:nums) {
  // code here ...
  (print n)
}
```

##### Lambdas

```
(lambda sum | a:int, b:int -> (+ a b))
(sum 4 6) ;; 10
```

```
(lambda filter | c:list, f:lambda -> {
  let a = []
  (i:a) {
    if (f i) {
      (add a i)
    }
  }
  send a
})
;; filter nums greater than 3
let nums = (filter [1,2,3,4,5], lambda | x -> (<= x 3))
(print nums) ;; [1,2,3]
```

##### Sending

This is probably my favourite feature of Ark. Using `sends` adds so much clarity. You will see below how data flows 
from the left to the right. Rather than nesting function calls, expression are evaluated from the left to the right...

```
[1,2,3,4] -> (pow 2)
    -> (filter lambda | x -> (< x 5)
    -> (lambda | x -> Point(x:x, 0)) ;; [Point(1,0), Point(4,0)]
```

##### More features to come! 💃

#### Language Roadmap

- Strong typing
- Structs
- Standard Library
    - File I/O
    - Network I/O (TCP/IP, HTTP)
    - I/O Streams
    - Regex
    - Math
    - Data Structures (Stacks, Queues, Heaps, etc)
- Dependency management (inspired by Golang github urls)
- Unit Testing Framework
- Static Analyser for IDE Integration
- Syntax Themes for Sublime, Atom, etc


#### Contributing
If you happen to find this interesting, email me! I'd love to get people involved 🙂
