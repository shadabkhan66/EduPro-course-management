# 08 — Interview keyword checklist (fast revision)

Use this the day before a call; expand with [01](01-language-engine-and-strict-basics.md)–[07](07-bridge-to-react-mental-model.md) if a topic is weak.

---

## Engine & language

- **ECMAScript** = language spec; **engine** = V8, etc.; **host** = browser/Node with extra APIs.  
- **Strict mode** / strict modules — fewer silent bugs.  
- **`let` / `const` (block) vs `var` (function)** — TDZ, no redeclaration.  
- **Falsy** list — and that **only** those are falsy.  
- **JIT** — not “purely interpreted”.

## Types and equality

- **7 primitives** + **object** (including **array**).  
- **`typeof` quirks** — `null` is `"object"`, use `x === null`.  
- **`===` by default;** `Object.is` for `NaN` and `±0`.  
- **Reference** vs value — **object assignment shares** reference.  
- **Shallow** vs **deep** copy — `structuredClone` for data trees when available.

## Functions

- **Scope is lexical;** closure = function + captured environment.  
- **`this`** — *call site* for normal functions; **lexical** for arrows.  
- **`call` / `apply` / `bind`** — change `this` for normal functions.  
- **IIFE** — legacy pattern before modules.

## Async

- **Event loop** — stack, then **microtasks** (Promise), then **macrotasks** (simplified).  
- **Promise** — pending, fulfilled, rejected; **catch** all rejections.  
- **`async/await`** — syntax on Promises; **try/catch** for errors.  
- **`Promise.all` / `allSettled` / `race`** — when to use.  
- **`fetch` + `r.ok` +** parse JSON; **CORS** is a **browser** cross-origin check.

## Modules & tooling

- **ESM** `import` / `export` — static, tree-shakeable.  
- **Bundler** — why (many files, npm, compatibility, optimization).

## DOM & web

- **querySelector** over positional indexes.  
- **XSS** — careful with `innerHTML` and user data.  
- **Event** phases — **capture, target, bubble**; `preventDefault` / `stopPropagation`.  
- **localStorage** vs **sessionStorage** — tab vs **origin**; **cookies** — server round-trip options.

## React (once you start)

- **Declarative** UI, **state** drives render.  
- **keys** in lists — identity for reconciliation.  
- **Immutability** for state updates.  
- **Stale** closures / **effect** dependencies — *why* from JS closures.

## Sound-bite answers (30 seconds)

1. *What is a closure?*  
   A function that retains access to variables from its **outer lexical** scope when executed later.  

2. *Difference between `==` and `===`?*  
   `===` is **no coercion**; `==` allows coercion; prefer `===`.  

3. *What is the event loop?*  
   Mechanism that runs your JS, then **drains** microtasks, then processes **macrotasks** (timers, I/O callbacks), in a loop.  

4. *What is a Promise?*  
   A **standard** object for a value that will be available **asynchronously**; supports `.then` chains and `async/await`.  

5. *Why is `var` avoided?*  
   Function-scoped and **hoists** in ways that are surprising; `let`/`const` are **block**-scoped and safer.

---

**Back to index:** [README](README.md)
