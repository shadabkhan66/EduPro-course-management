# Modern JavaScript — modular notes (for React and full-stack interviews)

These modules refresh and **upgrade** the ideas in your older `appended.txt` notes. They are **condensed** on purpose: fewer long HTML pages, more **keywords**, **models**, and **interview language**.

**Suggested order**

| Order | File | You learn |
|------:|------|------------|
| 1 | [01-language-engine-and-strict-basics](01-language-engine-and-strict-basics.md) | How JS runs, spec vs engines, `let`/`const`/`var`, strict mode, truthiness |
| 2 | [02-types-references-equality](02-types-references-equality.md) | Primitives vs objects, `==` vs `===`, copying vs mutation |
| 3 | [03-functions-scope-closures-this](03-functions-scope-closures-this.md) | Closures, `this`, arrow functions, IIFE (historical) |
| 4 | [04-async-event-loop-promises](04-async-event-loop-promises.md) | Event loop, microtasks, Promises, `async/await`, `fetch` |
| 5 | [05-modules-and-tooling-snapshot](05-modules-and-tooling-snapshot.md) | ES modules, npm, why bundlers exist |
| 6 | [06-dom-events-and-web-apis](06-dom-events-and-web-apis.md) | DOM mental model, events (bubble/capture), storage, CORS in one place |
| 7 | [07-bridge-to-react-mental-model](07-bridge-to-react-mental-model.md) | What from JS actually matters first in React + interview Qs |
| 8 | [08-interview-keyword-checklist](08-interview-keyword-checklist.md) | Fast revision sheet |

**If you are time-boxed** (e.g. 2 weeks before React): do **1 → 2 → 3 → 4 → 7**, skim **5–6**, use **8** the day before interviews.

**Compared to a Java background**

- JS in the **browser** is mostly **single-threaded**; concurrency is **cooperative** (event loop, async APIs), not like a thread pool.
- **Types** are at **runtime**; you can add **TypeScript** later (common on full-stack teams).

---

## Questions for you (to optimize the next revision)

Reply with short answers; I can then tailor depth (e.g. more HTTP/auth, or more data structures).

1. **Target stack** — React only, or also Node.js / Express (or another JS backend)?
2. **Interviews** — more **frontend** (JS/React/CSS), or **system design** + APIs too?
3. **TypeScript** — plan to learn **after** React, **with** React, or **before**?
4. **Build tools** — okay to add a tiny Vite or CRA-style module later, or stay **no-build** in the browser for now?
5. **Depth** — prefer **shorter** modules with links to MDN, or **longer** worked examples in this repo?
6. **Your Java level** — comfortable with **streams**, **NIO**, **generics**? (helps calibrate analogies.)

---

## What we intentionally de-emphasized from old notes

- **HTML comment wrappers** in `<script>` — only relevant for ancient browsers; ignore.
- **jQuery** — still in legacy codebases; for **new** React work, use **vanilla DOM** or **React** patterns first.
- **“JS is not OOP”** — outdated; JS has **prototypes** and **classes** (syntactic sugar over prototypes).

Keep `appended.txt` as a **worked-example archive**; use these `js-notes` files as your **current source of truth**.
