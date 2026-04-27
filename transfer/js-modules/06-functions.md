# Module 06 — Functions

---

## What is a Function?

A function is a reusable block of code. Functions enable **refactoring** — extracting repeated logic into a single named unit.

---

## Two Ways to Define Functions

### Function Declaration

```js
function hello() {
    console.log("Hello");
}
```

- **Hoisted** — can be called before its definition in code.
- Each call creates its own execution context.

### Function Expression

```js
const hello = function() {
    console.log("Hello");
};
```

- **Not hoisted** — must be defined before use.
- Can be reassigned (if using `let`), enabling dynamic behavior.

```js
let action = function() {};
if (role === "admin") {
    action = function() { console.log("Admin panel"); };
} else {
    action = function() { console.log("User dashboard"); };
}
action();
```

### Function Configuration

```
function Print(param) {    ← Declaration (name + params)
    Print(param)           ← Signature
    { ... }                ← Definition (body)
}
```

---

## Parameters

### Formal vs Actual Parameters

```js
function greet(name) {     // name = formal parameter
    console.log("Hi " + name);
}
greet("John");              // "John" = actual parameter (argument)
```

### Any Type as Parameter

JavaScript parameters have no type restriction — you can pass anything:

```js
function demo(ref) { }

demo(42);                         // number
demo("TV");                       // string
demo(true);                       // boolean
demo([1, 2, 3]);                  // array
demo({ name: "TV", price: 100 }); // object
demo(function() { });              // function
```

### Multiple Parameters

```js
function details(name, price, stock) {
    console.log(`${name} - ₹${price} - ${stock ? "Available" : "Out of stock"}`);
}
details("TV", 45000, true);
```

- Parameters are **positional** — order matters.
- Missing arguments become `undefined`.

### Default Parameters (ES6)

```js
function greet(name = "Guest") {
    console.log("Hello " + name);
}
greet();           // "Hello Guest"
greet("John");     // "Hello John"
```

### Rest Parameters (`...`)

A single parameter that collects **remaining arguments** into an array.

```js
function sum(label, ...numbers) {
    let total = numbers.reduce((a, b) => a + b, 0);
    console.log(`${label}: ${total}`);
}
sum("Total", 10, 20, 30);    // "Total: 60"
```

**Rules:**
- Only **one** rest parameter per function
- Must be the **last** parameter

### Spread Syntax (for Arguments)

Spread an array into individual arguments:

```js
function add(a, b, c) {
    return a + b + c;
}
let values = [10, 20, 30];
add(...values);    // 60
```

> **Interview:** "Rest collects multiple arguments INTO an array (formal side). Spread EXPANDS an array into individual values (actual side). They use the same `...` syntax but in opposite directions."

---

## Return

```js
function add(a, b) {
    return a + b;    // returns value and exits function
}
let result = add(10, 20);   // 30
```

A function can return **any type** — primitive, object, array, or even another function:

```js
function createGreeter(name) {
    return function() {
        console.log("Hello " + name);
    };
}
let greet = createGreeter("John");
greet();    // "Hello John"
```

Without `return`, a function returns `undefined`.

---

## Arrow Functions (ES6)

Shorter syntax for function expressions using `=>`:

```js
// Traditional
const add = function(a, b) { return a + b; };

// Arrow — implicit return (single expression)
const add = (a, b) => a + b;

// Arrow — with body (explicit return needed)
const greet = (name) => {
    let msg = "Hello " + name;
    return msg;
};

// Single parameter — parentheses optional
const double = x => x * 2;

// No parameters
const sayHi = () => console.log("Hi");
```

**Key Difference — `this` binding:**
- Regular functions: `this` depends on **how the function is called** (call site)
- Arrow functions: `this` is inherited from the **enclosing scope** (lexical)

```js
const obj = {
    name: "TV",
    // Regular — this = obj
    getName() { return this.name; },
    // Arrow — this = outer scope (NOT obj)
    getNameArrow: () => this.name   // undefined or window.name
};
```

> **Interview:** "Arrow functions don't have their own `this`. They inherit `this` from the surrounding lexical scope. This makes them ideal for callbacks but unsuitable as object methods."

---

## Closure

A closure is when a function **retains access** to variables from its **outer (enclosing) scope**, even after the outer function has returned.

```js
function makeCounter() {
    let count = 0;                    // outer variable
    return function() {               // inner function
        count++;                      // still has access
        return count;
    };
}

const counter = makeCounter();
counter();    // 1
counter();    // 2
counter();    // 3
```

```
┌───────────────────────────────────────────┐
│  makeCounter() scope                      │
│  ┌─────────────────────────────────┐      │
│  │  count = 0                      │      │
│  │  ┌───────────────────────┐      │      │
│  │  │  returned function    │      │      │
│  │  │  has access to count  │──────┤      │
│  │  └───────────────────────┘      │      │
│  └─────────────────────────────────┘      │
└───────────────────────────────────────────┘
```

> **Interview:** "A closure is a function bundled together with references to its surrounding lexical environment. Even after the outer function finishes, the inner function remembers the variables."

**Practical uses:** data privacy, factory functions, event handlers, React hooks (useState uses closures internally).

---

## Callback Functions

A function passed as an **argument** to another function, to be called later.

```js
function fetchData(url, onSuccess, onFailure) {
    if (url === "http://api.example.com") {
        onSuccess("Data loaded");
    } else {
        onFailure("Error: Invalid URL");
    }
}

fetchData("http://api.example.com",
    function(data) { console.log(data); },
    function(err) { console.error(err); }
);
```

- Callbacks are **synchronous** by default (blocking).
- For **async** operations, Promises and async/await are preferred (see Module 09).

---

## Promise (Introduction)

A Promise represents an **asynchronous** operation that will complete in the future.

```
Promise States:
   pending  →  fulfilled (resolved)  →  .then()
                    or
             →  rejected              →  .catch()
```

```js
let promise = new Promise(function(resolve, reject) {
    let success = true;
    if (success) {
        resolve("Data loaded");
    } else {
        reject("Failed to load");
    }
});

promise
    .then(function(result) { console.log(result); })
    .catch(function(error) { console.error(error); })
    .finally(function() { console.log("Done"); });
```

> Full deep-dive on Promises, async/await, and the event loop in **Module 09**.

---

## Function Generator

Generators produce a sequence of values **lazily** — one at a time, on demand.

Defined with `function*` and uses `yield` to return values.

```js
function* numbers() {
    yield 10;
    yield 20;
    yield 30;
}

const gen = numbers();
gen.next();    // { value: 10, done: false }
gen.next();    // { value: 20, done: false }
gen.next();    // { value: 30, done: false }
gen.next();    // { value: undefined, done: true }
```

- Each `yield` pauses execution and returns a value.
- `.next()` resumes from where it paused.
- `.done` is `true` when no more values.

---

## Recursion

A function calling **itself**. Must have a **base case** to avoid infinite loops.

```js
function factorial(n) {
    if (n === 0) return 1;           // base case
    return n * factorial(n - 1);     // recursive case
}
factorial(5);    // 120  (5 × 4 × 3 × 2 × 1)
```

**Use cases:** tree traversal, mathematical computations, nested data processing.

> **Caution:** Deep recursion can cause **stack overflow**. For large iterations, prefer loops or **tail call optimization** (limited browser support).

---

## IIFE — Immediately Invoked Function Expression

A function that runs **immediately** after being defined. Used historically for **encapsulation** before modules existed.

```js
(function() {
    let secret = "hidden";
    console.log("Runs immediately");
})();

// secret is not accessible here
```

- Still seen in legacy code and some library patterns.
- Modern alternative: **ES Modules** with their own scope.

---

**Next:** [Module 07 — OOP: Classes, Modules & Inheritance](07-oop-classes-modules.md)
