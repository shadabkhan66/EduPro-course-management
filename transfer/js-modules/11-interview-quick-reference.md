# Module 11 — Interview Quick Reference

Use this for **last-day revision**. Each answer should be **30 seconds or less**.

---

## Language Basics

**Q: What is JavaScript?**
A: A high-level, dynamically typed, multi-paradigm language. It's the implementation of the ECMAScript specification. Runs in browsers (V8, SpiderMonkey) and on servers (Node.js).

**Q: Is JavaScript compiled or interpreted?**
A: Modern engines use **JIT (Just-In-Time) compilation** — source is parsed to bytecode, then hot paths are compiled to optimized machine code. Saying "only interpreted" is outdated.

**Q: What is strict mode?**
A: A mode enabled by `"use strict"` that catches silent errors (undeclared variables, duplicate params), disables `with`, and makes `eval` safer. Module code is strict by default.

---

## Variables & Scope

**Q: `var` vs `let` vs `const`?**
A: `var` is function-scoped, hoisted with `undefined`, allows re-declaration. `let` is block-scoped, sits in TDZ until declaration. `const` is like `let` but the binding cannot be reassigned (object contents can still be mutated).

**Q: What is hoisting?**
A: JavaScript moves declarations to the top of their scope during compilation. `var` is initialized as `undefined`. `let`/`const` are hoisted but remain in the **Temporal Dead Zone** until execution reaches the declaration — accessing them before throws `ReferenceError`.

**Q: What is the Temporal Dead Zone (TDZ)?**
A: The period between entering a scope and the `let`/`const` declaration being executed. The variable exists but cannot be accessed.

---

## Types & Equality

**Q: What are JavaScript's data types?**
A: 7 primitives — `number`, `string`, `boolean`, `undefined`, `null`, `symbol`, `bigint`. Plus `object` (which includes arrays, functions, dates, maps, etc.).

**Q: `==` vs `===`?**
A: `==` performs **type coercion** before comparing (`1 == "1"` is true). `===` checks value AND type without coercion (`1 === "1"` is false). Always prefer `===`.

**Q: What are falsy values?**
A: `false`, `0`, `-0`, `0n`, `""`, `null`, `undefined`, `NaN` — only these 8. Everything else is truthy, including `[]`, `{}`, `"0"`, `"false"`.

**Q: `null` vs `undefined`?**
A: `undefined` — variable declared but never assigned. `null` — intentionally assigned "no value". `typeof null` is `"object"` (historical bug). Check with `x === null`.

**Q: How to deep copy an object?**
A: `structuredClone(obj)` for data-only objects (modern). `JSON.parse(JSON.stringify(obj))` works but loses functions, dates, and undefined. For complex cases, use libraries.

---

## Functions

**Q: What is a closure?**
A: A function that retains access to variables from its **outer lexical scope** even when executed outside that scope. The inner function "closes over" the outer variables.

```js
function counter() {
    let n = 0;
    return () => ++n;
}
const c = counter();
c(); // 1
c(); // 2
```

**Q: What is `this` in JavaScript?**
A: For **regular functions**, `this` is determined by how the function is called (call site). For **arrow functions**, `this` is lexically inherited from the enclosing scope.

| Call style | `this` |
|------------|--------|
| `obj.method()` | `obj` |
| `func()` (strict) | `undefined` |
| `new Func()` | new instance |
| `call/apply/bind` | first argument |
| Arrow function | enclosing scope's `this` |

**Q: Arrow function vs regular function?**
A: Arrow functions have no own `this` (lexical), no `arguments` object, cannot be used as constructors (`new`), and have shorter syntax with implicit return for single expressions.

**Q: What is an IIFE?**
A: Immediately Invoked Function Expression — `(function() { })()`. Creates a private scope. Used before modules existed; less common now.

---

## Async

**Q: What is the event loop?**
A: The mechanism that runs synchronous code on the call stack, then drains all **microtasks** (Promise callbacks), then processes one **macrotask** (setTimeout, I/O), and repeats.

**Q: What is a Promise?**
A: An object representing the eventual result of an async operation. States: pending → fulfilled (`.then`) or rejected (`.catch`). Once settled, immutable.

**Q: `Promise.all` vs `Promise.allSettled`?**
A: `all` — resolves when ALL succeed, rejects immediately if ANY fails. `allSettled` — waits for ALL to complete regardless of success/failure, never short-circuits.

**Q: What is `async/await`?**
A: Syntactic sugar over Promises. `async` function always returns a Promise. `await` pauses the function (not the thread) until the Promise settles. Use `try/catch` for errors.

**Q: `setTimeout(..., 0)` — does it run immediately?**
A: No. It schedules the callback as a **macrotask**. It runs after the current synchronous code AND all microtasks finish. Minimum delay is ~4ms in browsers.

---

## DOM & Events

**Q: What is the DOM?**
A: The Document Object Model — a tree of nodes representing the HTML document. JavaScript interacts with the DOM API to read and manipulate page content.

**Q: `querySelector` vs `getElementById`?**
A: `getElementById` only finds by ID (faster). `querySelector` accepts any CSS selector (more flexible). `querySelectorAll` returns all matches as a static NodeList.

**Q: What is event bubbling?**
A: When an event fires on a nested element, it propagates **upward** through parent elements (target → document → window). Default behavior. Use `stopPropagation()` to prevent.

**Q: `preventDefault` vs `stopPropagation`?**
A: `preventDefault` — stops the browser's **default action** (form submit, link navigation). `stopPropagation` — stops the event from **bubbling** to parent handlers. They are independent.

**Q: What is event delegation?**
A: Attaching a single listener to a parent instead of individual children. Check `e.target` to identify which child triggered the event. Works for dynamically added elements.

**Q: `innerHTML` vs `textContent`?**
A: `innerHTML` parses and renders HTML (XSS risk with user input). `textContent` sets plain text only (safe). Prefer `textContent` for user-provided data.

---

## Storage

**Q: `localStorage` vs `sessionStorage` vs cookies?**
A:

| | localStorage | sessionStorage | Cookies |
|-|---|---|---|
| Persists | Until cleared | Until tab closes | Until expires |
| Scope | All tabs (same origin) | Single tab | All tabs (path) |
| Size | ~5-10 MB | ~5-10 MB | ~4 KB |
| Sent to server | No | No | Yes (auto) |

---

## OOP

**Q: How does OOP work in JavaScript?**
A: JavaScript uses **prototype-based** inheritance. ES6 classes are syntactic sugar over prototypes. Classes support constructor, methods, getters/setters, static members, inheritance (`extends`/`super`), and private fields (`#`).

**Q: Prototypal vs Classical inheritance?**
A: Classical (Java) — classes define blueprints, instances are created from classes. Prototypal (JS) — objects inherit directly from other objects via a prototype chain. ES6 `class` is syntactic sugar over prototypes.

**Q: What is the prototype chain?**
A: When accessing a property, JS looks at the object first, then its prototype, then the prototype's prototype, until `null`. This chain enables inheritance without classes.

---

## ES6+ Features Summary

| Feature | What it does |
|---------|-------------|
| `let` / `const` | Block-scoped declarations |
| Arrow functions `=>` | Short syntax, lexical `this` |
| Template literals `` ` ` `` | String interpolation `${expr}` |
| Destructuring `{ a, b }` / `[a, b]` | Extract values from objects/arrays |
| Spread `...arr` | Expand array/object |
| Rest `...params` | Collect remaining arguments |
| Default parameters | `function(x = 10)` |
| Modules `import/export` | Code organization |
| Classes | OOP syntax |
| Promises | Async handling |
| `async/await` | Clean async syntax |
| `?.` Optional chaining | Safe property access |
| `??` Nullish coalescing | Default for null/undefined only |
| `Map` / `Set` | New collection types |
| `Symbol` | Unique identifiers |
| `for...of` | Iterate values |
| `Array.from()` | Convert iterable to array |

---

## Common Coding Patterns

**Debounce** — delay execution until user stops typing:
```js
function debounce(fn, delay) {
    let timer;
    return function(...args) {
        clearTimeout(timer);
        timer = setTimeout(() => fn.apply(this, args), delay);
    };
}
```

**Throttle** — execute at most once per interval:
```js
function throttle(fn, interval) {
    let last = 0;
    return function(...args) {
        let now = Date.now();
        if (now - last >= interval) {
            last = now;
            fn.apply(this, args);
        }
    };
}
```

---

**Back to index:** [README](README.md)
