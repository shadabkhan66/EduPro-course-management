# Module 09 — State Management (Client-Side)

---

## Why State Management?

Web applications use **HTTP/HTTPS** — a **stateless** protocol. Each request is independent; the server doesn't remember previous requests.

To persist data between pages, requests, or sessions, we use **state management techniques**.

```
┌──────────────────────────────────────────────────────────┐
│              State Management                             │
│                                                           │
│  Client-Side                    │   Server-Side           │
│  ─────────────                  │   ────────────          │
│  • Query String                 │   • Sessions            │
│  • Local Storage                │   • Databases           │
│  • Session Storage              │   • Server Cache        │
│  • Cookies                      │   • Redis / Memcached   │
└──────────────────────────────────────────────────────────┘
```

---

## 1. Query String

Data appended to the URL as key-value pairs after `?`.

```
http://example.com/page.html?username=john&age=25
```

**Reading query strings:**

```js
// Modern approach (recommended)
let params = new URLSearchParams(location.search);
let username = params.get("username");    // "john"
let age = params.get("age");              // "25"

// Traditional approach
let str = location.search;                // "?username=john&age=25"
let value = str.substring(str.indexOf("=") + 1);
```

**HTML form with GET method sends data as query string:**
```html
<form action="welcome.html" method="GET">
    <input type="text" name="username">
    <button>Submit</button>
</form>
<!-- Navigates to: welcome.html?username=john -->
```

**Characteristics:**
- Visible in URL (not secure for sensitive data)
- Shareable and bookmarkable
- Limited length (~2000 characters)
- Lost when URL changes

---

## 2. Local Storage

**Permanent** storage in the browser. Data persists even after browser/device restart.

```js
// Store
localStorage.setItem("username", "John");

// Retrieve
let name = localStorage.getItem("username");    // "John"

// Remove specific item
localStorage.removeItem("username");

// Clear all
localStorage.clear();
```

**Characteristics:**
- ~5–10 MB per origin (browser-dependent)
- Accessible across **all tabs** of the same origin
- Data persists until **manually cleared**
- Only stores **strings** — use `JSON.stringify` / `JSON.parse` for objects

```js
// Storing objects
let user = { name: "John", age: 25 };
localStorage.setItem("user", JSON.stringify(user));

// Retrieving objects
let stored = JSON.parse(localStorage.getItem("user"));
console.log(stored.name);   // "John"
```

**Example — Remember user across pages:**

```js
// home.html — save
function login() {
    localStorage.setItem("username", document.getElementById("txtName").value);
    location.href = "dashboard.html";
}

// dashboard.html — read
function load() {
    let user = localStorage.getItem("username");
    if (!user) {
        location.href = "home.html";    // redirect if not logged in
    } else {
        document.querySelector("h1").textContent = `Welcome ${user}`;
    }
}
```

---

## 3. Session Storage

**Temporary** storage — cleared when the browser **tab** is closed.

```js
// Store
sessionStorage.setItem("username", "John");

// Retrieve
let name = sessionStorage.getItem("username");

// Remove
sessionStorage.removeItem("username");

// Clear all
sessionStorage.clear();
```

**Characteristics:**
- ~5–10 MB per origin
- **Not accessible** from other tabs (isolated per tab)
- Data is removed when the **tab/window is closed**
- API is identical to localStorage

---

## 4. Cookies

Small text files stored on the client. Can be sent **automatically** with HTTP requests to the server.

```js
// Set a cookie (with expiration)
document.cookie = "username=John; expires=Thu, 01 Jan 2027 00:00:00 UTC; path=/";

// Read all cookies (returns one string)
let allCookies = document.cookie;    // "username=John; theme=dark"

// Delete a cookie (set expired date)
document.cookie = "username=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/";
```

**Check if cookies are enabled:**
```js
if (navigator.cookieEnabled) {
    console.log("Cookies enabled");
}
```

**Cookie attributes:**

| Attribute | Purpose |
|-----------|---------|
| `expires` | When the cookie expires (date) |
| `max-age` | Seconds until expiration |
| `path` | URL path the cookie is valid for |
| `domain` | Domain the cookie belongs to |
| `secure` | Only sent over HTTPS |
| `HttpOnly` | Cannot be accessed by JavaScript (server-set) |
| `SameSite` | CSRF protection (`Strict`, `Lax`, `None`) |

---

## Comparison Table

```
┌───────────────┬─────────────┬──────────────┬───────────────┬──────────────────┐
│ Feature       │ Query String│ localStorage │ sessionStorage│ Cookies          │
│───────────────┼─────────────┼──────────────┼───────────────┼──────────────────│
│ Capacity      │ ~2KB (URL)  │ ~5-10 MB     │ ~5-10 MB      │ ~4 KB per cookie │
│ Persists?     │ In URL only │ Until cleared│ Until tab close│ Until expires    │
│ Accessible    │ Any page    │ All tabs     │ Same tab only │ All tabs (path)  │
│ Sent to server│ GET request │ No           │ No            │ Yes (auto)       │
│ Visible?      │ URL bar     │ DevTools     │ DevTools      │ DevTools + HTTP  │
│ Security      │ Exposed     │ XSS risk     │ XSS risk      │ HttpOnly flag    │
└───────────────┴─────────────┴──────────────┴───────────────┴──────────────────┘
```

> **Interview:** "For auth tokens in SPAs, prefer `HttpOnly` cookies (set by server, inaccessible to JavaScript) to prevent XSS attacks. localStorage is convenient but vulnerable to cross-site scripting. sessionStorage is similar but limited to the current tab."

---

## When to Use What

| Scenario | Best Choice |
|----------|-------------|
| Share data via URL (filters, search) | Query String |
| Remember preferences (theme, language) | localStorage |
| Store temp data during a session | sessionStorage |
| Auth tokens sent with API requests | HttpOnly cookies (server-set) |
| Shopping cart (guest user) | localStorage or sessionStorage |

---

**Next:** [Module 10 — Async: Event Loop, Promises & Fetch](10-async-event-loop-promises.md)
