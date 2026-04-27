# Module 10 — Async: Event Loop, Promises & Fetch

---

## Why Async? (Java vs JavaScript Comparison)

| Aspect | Java (Spring Boot) | JavaScript (Browser) |
|--------|-------------------|---------------------|
| Threading | Multi-threaded (thread pool) | **Single-threaded** (one main thread) |
| Blocking I/O | OK — other threads continue | **Freezes the UI** — blocks rendering and input |
| Concurrency | Threads + Locks + Executors | **Event Loop** + Callbacks/Promises |

JavaScript runs your code on a **single main thread**. If you do something slow (network call, timer), you can't block — the browser would freeze. Instead, JS uses **async APIs** that run in the background and notify you when done.

> **Interview:** "JavaScript is single-threaded but non-blocking. Async operations are handled by browser/Node APIs and their results are queued back via the event loop."

---

## The Event Loop

The mechanism that coordinates execution of synchronous code, microtasks, and macrotasks.

```
┌────────────────────────────────────────────────────┐
│                    EVENT LOOP                       │
│                                                     │
│  1. Execute all synchronous code on CALL STACK      │
│           ↓ (stack empty)                           │
│  2. Process ALL MICROTASKS (Promise .then/.catch,   │
│     queueMicrotask, MutationObserver)               │
│           ↓ (microtask queue empty)                 │
│  3. Process ONE MACROTASK (setTimeout, setInterval, │
│     I/O callbacks, UI events)                       │
│           ↓                                         │
│  4. Go back to step 1                               │
└────────────────────────────────────────────────────┘
```

```
        ┌──────────────┐
        │  Call Stack   │  ← Your synchronous JS code runs here
        └──────┬───────┘
               │ (empty)
        ┌──────▼───────┐
        │  Microtask   │  ← Promise .then(), queueMicrotask()
        │  Queue       │     ALL processed before next macrotask
        └──────┬───────┘
               │ (empty)
        ┌──────▼───────┐
        │  Macrotask   │  ← setTimeout, setInterval, I/O, events
        │  Queue       │     ONE processed per loop cycle
        └──────────────┘
```

### Classic Interview Question

```js
console.log("1");

setTimeout(() => console.log("2"), 0);

Promise.resolve().then(() => console.log("3"));

console.log("4");
```

**Output:** `1, 4, 3, 2`

**Why:**
1. `console.log("1")` — synchronous → call stack → prints `1`
2. `setTimeout` — schedules callback in **macrotask** queue
3. `Promise.then` — schedules callback in **microtask** queue
4. `console.log("4")` — synchronous → prints `4`
5. Stack empty → drain microtasks → prints `3`
6. Microtasks empty → process one macrotask → prints `2`

---

## Callbacks (The Old Way)

A function passed to another function, executed later when the async operation completes.

```js
function loadData(url, onSuccess, onFailure) {
    // simulate async
    setTimeout(() => {
        if (url.startsWith("http")) {
            onSuccess("Data loaded");
        } else {
            onFailure("Invalid URL");
        }
    }, 1000);
}

loadData("http://api.com",
    (data) => console.log(data),
    (err) => console.error(err)
);
```

**Problem — Callback Hell:**
```js
getData(function(a) {
    getMore(a, function(b) {
        getEvenMore(b, function(c) {
            // deeply nested — hard to read and maintain
        });
    });
});
```

---

## Promises (The Modern Way)

A Promise is an object representing the **eventual completion or failure** of an async operation.

### Three States

```
   ┌─────────┐
   │ PENDING │  ← initial state
   └────┬────┘
        │
   ┌────▼────┐        ┌──────────┐
   │FULFILLED│        │ REJECTED │
   │ (value) │        │ (reason) │
   └─────────┘        └──────────┘
       ↓                    ↓
    .then()             .catch()
```

Once settled (fulfilled or rejected), a Promise is **immutable** — cannot change state again.

### Creating a Promise

```js
let promise = new Promise((resolve, reject) => {
    let success = true;
    if (success) {
        resolve("Data loaded");        // → fulfilled
    } else {
        reject("Something went wrong"); // → rejected
    }
});

promise
    .then(result => console.log(result))     // on success
    .catch(error => console.error(error))     // on failure
    .finally(() => console.log("Done"));      // always runs
```

### Chaining Promises

```js
fetch("/api/user")
    .then(response => response.json())       // parse JSON
    .then(user => fetch(`/api/orders/${user.id}`))  // another request
    .then(response => response.json())
    .then(orders => console.log(orders))
    .catch(error => console.error(error));   // catches any error in chain
```

### Promise Utility Methods

| Method | Description |
|--------|-------------|
| `Promise.all([p1, p2])` | Resolves when **ALL** resolve; rejects if **ANY** rejects |
| `Promise.allSettled([p1, p2])` | Waits for all to settle (no short-circuit) |
| `Promise.race([p1, p2])` | Resolves/rejects with the **first** to settle |
| `Promise.any([p1, p2])` | Resolves with the **first fulfilled** (ignores rejections) |

```js
// Parallel requests
let [users, products] = await Promise.all([
    fetch("/api/users").then(r => r.json()),
    fetch("/api/products").then(r => r.json())
]);
```

---

## `async` / `await` (ES2017)

Syntactic sugar over Promises — write async code that **looks** synchronous.

### `async` function always returns a Promise

```js
async function greet() {
    return "Hello";
}
greet().then(msg => console.log(msg));    // "Hello"
```

### `await` pauses execution until Promise settles

```js
async function loadUser() {
    try {
        let response = await fetch("/api/user");
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }
        let user = await response.json();
        console.log(user);
    } catch (error) {
        console.error("Failed:", error.message);
    }
}
```

**Key points:**
- `await` can only be used inside `async` functions (or at top level in modules)
- `await` pauses **the function**, not the entire thread — other tasks continue
- Use `try/catch` for error handling (replaces `.catch()`)

### Parallel with async/await

```js
// Sequential (slow — waits for each)
let users = await fetchUsers();
let products = await fetchProducts();

// Parallel (fast — both start immediately)
let [users, products] = await Promise.all([
    fetchUsers(),
    fetchProducts()
]);
```

---

## `fetch` API

The modern way to make HTTP requests in JavaScript.

```js
// Basic GET request
let response = await fetch("http://fakestoreapi.com/products");
let products = await response.json();

// POST request
let response = await fetch("/api/products", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name: "TV", price: 45000 })
});
```

### Response Object

```js
response.ok;          // true if status 200-299
response.status;      // HTTP status code (200, 404, 500...)
response.statusText;  // "OK", "Not Found"
response.json();      // parse body as JSON (returns Promise)
response.text();      // parse body as text
response.blob();      // parse body as binary
```

### Error Handling with fetch

`fetch` only rejects on **network errors** — HTTP errors (404, 500) do **not** throw.

```js
async function loadData(url) {
    try {
        let response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Server error: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error("Fetch failed:", error.message);
    }
}
```

### Using fetch with .then() (your original style — still valid)

```js
fetch("http://fakestoreapi.com/products")
    .then(function(response) {
        return response.json();
    })
    .then(function(products) {
        products.map(function(product) {
            console.log(product.title);
        });
    })
    .catch(function(error) {
        console.error(error);
    });
```

---

## CORS (Cross-Origin Resource Sharing)

When your page at `https://mysite.com` tries to fetch from `https://api.other.com`, the browser checks **CORS headers** on the response.

```
┌──────────────┐     request      ┌──────────────┐
│  Browser     │ ──────────────►  │  API Server  │
│  (your page) │                  │              │
│              │  ◄──────────────  │              │
│              │  Access-Control-  │              │
│              │  Allow-Origin: * │              │
└──────────────┘                  └──────────────┘
```

- **Same-origin:** `mysite.com` → `mysite.com/api` = OK
- **Cross-origin:** `mysite.com` → `api.other.com` = needs CORS headers

**Fix is on the server side** — add `Access-Control-Allow-Origin` header. In development, use a **proxy** (Vite `server.proxy`, or `http-proxy-middleware`).

> **For you as Java backend dev:** In Spring Boot, use `@CrossOrigin` annotation or configure a global CORS filter.

---

## Summary: Async Patterns Evolution

```
Callbacks (ES5)  →  Promises (ES2015)  →  async/await (ES2017)
     ↓                    ↓                      ↓
 callback hell       .then() chains        clean try/catch
 hard to read        better chaining       looks synchronous
 error-prone         .catch() for errors   easiest to read
```

---

**Next:** [Module 11 — Interview Quick Reference](11-interview-quick-reference.md)
