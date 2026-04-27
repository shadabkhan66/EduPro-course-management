# Module 01 — Evolution of JavaScript & Integration into HTML

---

## Evolution of JavaScript

- In early 1990s, the web needed a scripting language to make pages interactive.
- In 1995, **Netscape** developed a browser called **Netscape Communicator**.
- Netscape hired **Brendan Eich** to create a client-side scripting language.
- He built it in ~10 days — initially called **Mocha**, then renamed to **LiveScript**.
- Netscape partnered with **Sun Microsystems**, who renamed it to **JavaScript** (marketing decision to ride Java's popularity — the two languages are **unrelated**).
- In 1997, Netscape submitted JavaScript to **ECMA International** for standardization.
- The standard is called **ECMAScript (ES)**. JavaScript is the most popular implementation.
- In 2002, Netscape shut down; JavaScript continued under ECMA governance.

```
Mocha → LiveScript → JavaScript → ECMAScript (ES)
```

### ECMAScript Version Timeline

| Version | Year | Key Additions |
|---------|------|---------------|
| ES3 | 1999 | try/catch, regex |
| **ES5** | **2009** | strict mode, JSON, Array methods (forEach, map, filter) |
| **ES6 / ES2015** | **2015** | let/const, arrow functions, classes, modules, Promises, template literals |
| ES2017 | 2017 | async/await |
| ES2020 | 2020 | optional chaining `?.`, nullish coalescing `??` |
| ES2022 | 2022 | top-level await, `.at()`, class fields |
| ES2024+ | ongoing | yearly releases continue |

> **Interview:** "JavaScript is the language; ECMAScript is the specification. Browsers implement the spec through engines like V8 (Chrome/Node), SpiderMonkey (Firefox), JavaScriptCore (Safari)."

### Key References
- https://www.ecma-international.org/ → JavaScript specification
- https://whatwg.org/ → HTML / CSS standards
- https://developer.mozilla.org/en-US/ → MDN (best reference docs)

---

## JavaScript Engines

A **JavaScript engine** compiles and executes JS code. Modern engines use **JIT (Just-In-Time) compilation** — not purely interpreted.

```
┌─────────────────────────────────────────────┐
│              JavaScript Engine               │
│                                              │
│  Source Code → Parser → AST → Bytecode       │
│                                  ↓           │
│                          JIT Compiler         │
│                                  ↓           │
│                        Optimized Machine Code │
└─────────────────────────────────────────────┘
```

| Engine | Used In |
|--------|---------|
| **V8** | Chrome, Node.js, Edge |
| **SpiderMonkey** | Firefox |
| **JavaScriptCore** | Safari |

---

## Integrating JavaScript into HTML

You can integrate JavaScript into an HTML page using **3 techniques**:

1. **Inline**
2. **Embedded**
3. **External File**

### 1. Inline Technique

Write JavaScript directly inside an HTML element's event attribute.
- Fast to write but **cannot reuse** — tightly coupled to element.

```html
<button onclick="window.print()">Print</button>
```

### 2. Embedded Technique

Place JavaScript inside a `<script>` tag within the `<head>` or `<body>`.
- Functions are reusable across the page.
- Keeps logic separate from individual elements.

```html
<script>
    function PrintPage() {
        window.print();
    }
</script>

<button onclick="PrintPage()">Print</button>
```

### 3. External File Technique

Define JavaScript in a separate `.js` file and link it using `<script src="...">`.
- Best for reusability across multiple pages.
- Enables caching by the browser (faster on revisits).
- Keeps HTML clean and JS maintainable.

```js
// scripts/print.js
"use strict";
function PrintPage() {
    window.print();
}
```

```html
<head>
    <script src="scripts/print.js"></script>
</head>
<body>
    <button onclick="PrintPage()">Print</button>
</body>
```

> **Best Practice:** Use external files for production. Use embedded for quick prototypes or small scripts.

---

## Script MIME Types and Attributes

| Type | Usage |
|------|-------|
| `<script type="text/javascript">` | Default (can be omitted — browsers assume JS) |
| `<script type="module">` | ES Module system — strict mode by default, supports `import`/`export` |
| `<script type="text/babel">` | For Babel transpiler (used in React dev setups) |

### `defer` vs `async` (Interview Favorite)

```
┌───────────────────────────────────────────────────────┐
│  <script>           → blocks HTML parsing until done  │
│  <script defer>     → downloads parallel, runs after  │
│                       HTML is fully parsed             │
│  <script async>     → downloads parallel, runs ASAP   │
│                       (order not guaranteed)           │
└───────────────────────────────────────────────────────┘
```

- `defer` — preserves script order, runs after DOM ready. Best for most cases.
- `async` — runs as soon as downloaded. Good for independent scripts (analytics).
- `type="module"` — **deferred by default**.

---

## Strict Mode

JavaScript is **not strictly typed** by default. You can enable strict mode to catch common errors.

```js
"use strict";
x = 10;  // ReferenceError: x is not defined (would silently create global without strict)
```

**What strict mode does:**
- Prevents accidental global variable creation
- Throws errors on duplicate parameter names
- Disallows `with` statement
- Makes `eval` safer
- **Module code** is always in strict mode automatically

> **Interview:** "Strict mode catches silent errors and turns them into explicit exceptions, making code safer and easier to debug."

---

## Minification

- You can **minify** JavaScript for production — removes whitespace, shortens variable names.
- Tools: **Terser**, **UglifyJS**, or build tools like **Vite/Webpack** do it automatically.
- Minified files are typically named `file.min.js`.

---

## Limitations of JavaScript (and Solutions)

| Limitation | Modern Solution |
|-----------|----------------|
| Not strongly typed — type changes implicitly | **TypeScript** (superset that adds static types) |
| No strict mode by default | `"use strict"` or use modules |
| No traditional OOP (initially) | ES6 **classes** (syntactic sugar over prototypes) |
| No built-in module system (initially) | ES Modules (`import`/`export`) since ES2015 |
| Code-level security issues | Content Security Policy (CSP), proper auth patterns |

> **Note:** TypeScript is not a replacement for JavaScript — it **compiles down to JavaScript**. It adds type safety at development time.

---

**Next:** [Module 02 — DOM: How JavaScript Refers HTML Elements](02-dom-referring-elements.md)
