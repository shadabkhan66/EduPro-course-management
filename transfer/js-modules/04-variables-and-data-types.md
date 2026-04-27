# Module 04 — Variables & Data Types

---

## Variables

A variable is a **named storage location** in memory where you can store a value and use it in expressions.

```js
// Without variable — asks twice
document.write("Hello " + prompt("Enter Name"));
document.write("Hi " + prompt("Enter Name"));

// With variable — asks once, reuses
let username = prompt("Enter Name");
document.write("Hello " + username);
document.write("Hi " + username);
```

### Variable Lifecycle: 3 Phases

```
Declaration    →    Assignment    →    Initialization (both together)

let x;              x = 10;            let x = 10;
```

---

## `var` vs `let` vs `const`

### `var` — Function-Scoped

- Accessible anywhere within the **function** (not limited to block).
- Allows **re-declaration** (shadowing) in same scope.
- Supports **hoisting** — variable is moved to the top of function with value `undefined`.

```js
function example() {
    var x = 10;
    if (true) {
        var y = 20;     // accessible outside this block
        var y = 40;     // re-declaration (shadowing) — allowed
    }
    console.log(y);     // 40 — var leaks out of blocks
}
```

**Hoisting with `var`:**
```js
console.log(x);   // undefined (not error — hoisted)
var x = 10;
```

### `let` — Block-Scoped

- Accessible only within the `{ }` block where declared (and inner blocks).
- **No re-declaration** in same scope.
- **No hoisting access** — sits in **Temporal Dead Zone (TDZ)** until declaration line.

```js
if (true) {
    let a = 10;
}
console.log(a);    // ReferenceError — a is not defined
```

### `const` — Block-Scoped, Read-Only Binding

- Must be **initialized** at declaration time.
- Cannot reassign the binding.
- For **objects/arrays**: the reference is constant, but contents can change.

```js
const PI = 3.14;
PI = 3.15;          // TypeError — cannot reassign

const user = { name: "John" };
user.name = "Jane";  // OK — mutating object content
user = {};           // TypeError — cannot reassign reference
```

### Comparison Table

```
┌──────────┬───────────┬──────────────┬──────────┬──────────┐
│ Keyword  │ Scope     │ Re-declare?  │ Hoisted? │ TDZ?     │
│──────────┼───────────┼──────────────┼──────────┼──────────│
│ var      │ Function  │ Yes          │ Yes      │ No       │
│ let      │ Block     │ No           │ Yes*     │ Yes      │
│ const    │ Block     │ No           │ Yes*     │ Yes      │
└──────────┴───────────┴──────────────┴──────────┴──────────┘
  * Hoisted but NOT accessible until declaration (TDZ)
```

> **Best Practice:** Use `const` by default. Use `let` when you need to reassign. Avoid `var` in new code.

> **Interview:** "var is function-scoped and hoisted with undefined. let and const are block-scoped and sit in the Temporal Dead Zone until their declaration is reached."

---

## Global Scope

A variable declared **outside any function** is global — accessible everywhere.

```js
let x = 10;          // global

function f1() {
    console.log(x);  // 10
}
function f2() {
    console.log(x);  // 10
}
```

Inside a function, you can create a global variable using `window`:

```js
function f1() {
    window.appName = "MyApp";   // explicit global
}
```

> **Avoid globals in production** — they pollute the namespace and cause hard-to-find bugs.

---

## Variable Naming Rules

- Must start with a letter, `_`, or `$`
- Can contain letters, digits, `_`, `$`
- **Case-sensitive** (`Name` ≠ `name`)
- Cannot use reserved keywords (`for`, `let`, `class`, etc.)
- Max length: 255 characters
- Use **camelCase** by convention: `userName`, `totalPrice`, `isAvailable`

---

## Data Types

JavaScript is **dynamically typed** — a variable's type is determined by the value it holds, and can change.

```js
let x = 10;       // number
x = "hello";      // now string
x = true;         // now boolean
```

### Two Categories

```
┌──────────────────────────────────────────────────┐
│               JavaScript Data Types              │
│                                                  │
│  Primitive (Immutable)    │  Non-Primitive        │
│  ─────────────────────    │  (Reference/Mutable)  │
│  • number                 │  • Object             │
│  • string                 │  • Array              │
│  • boolean                │  • Function           │
│  • undefined              │  • Map / Set          │
│  • null                   │  • Date               │
│  • symbol (ES6)           │                       │
│  • bigint (ES2020)        │                       │
│                           │                       │
│  Stored on: Stack         │  Stored on: Heap      │
│  Compared by: Value       │  Compared by: Ref     │
└──────────────────────────────────────────────────┘
```

---

## Primitive Types

### Number

Covers integers, floats, doubles, exponents, hex, octal, binary — all one type.

```js
let a = 42;           // integer
let b = 3.14;         // float
let c = 2e3;          // exponent: 2000
let d = 0xff;         // hex: 255
let e = 0o77;         // octal: 63
let f = 0b1010;       // binary: 10
```

**Special values:** `NaN`, `Infinity`, `-Infinity`

**Parsing input** (HTML inputs always return strings):
```js
let age = parseInt("25");          // 25
let price = parseFloat("99.99");   // 99.99
```

**Checking for NaN:**
```js
isNaN("hello");          // true
Number.isNaN("hello");   // false (stricter — preferred)
Number.isNaN(NaN);       // true
```

**Number formatting:**
```js
let price = 356700.45;
price.toLocaleString('en-IN', {
    style: 'currency',
    currency: 'INR'
});
// "₹3,56,700.45"
```

### BigInt

For safely handling integers larger than `Number.MAX_SAFE_INTEGER` (2^53 - 1).

```js
let big = 9988776655443322n;     // suffix 'n' makes it BigInt
typeof big;                       // "bigint"
```

### String

A sequence of characters enclosed in single quotes, double quotes, or backticks.

```js
let s1 = "Hello";
let s2 = 'World';
let s3 = `Hello ${s2}`;          // template literal with interpolation
```

**Template literals** (backticks) support:
- **String interpolation:** `${expression}`
- **Multi-line strings**
- Embedded expressions: `` `Total: ${price * qty}` ``

**Escape sequences:** `\\`, `\n`, `\t`, `\'`, `\"`

#### Common String Methods

| Method | Description | Example |
|--------|-------------|---------|
| `.length` | Character count | `"hello".length` → 5 |
| `.charAt(i)` | Character at index | `"hello".charAt(1)` → "e" |
| `.charCodeAt(i)` | ASCII/Unicode code | `"A".charCodeAt(0)` → 65 |
| `.toUpperCase()` | To uppercase | `"hello".toUpperCase()` → "HELLO" |
| `.toLowerCase()` | To lowercase | `"HELLO".toLowerCase()` → "hello" |
| `.trim()` | Remove leading/trailing spaces | `"  hi  ".trim()` → "hi" |
| `.startsWith(str)` | Starts with? | `"4455xxxx".startsWith("4455")` → true |
| `.endsWith(str)` | Ends with? | `"a@gmail.com".endsWith("gmail.com")` → true |
| `.indexOf(str)` | First occurrence index (-1 if not found) | `"hello".indexOf("l")` → 2 |
| `.lastIndexOf(str)` | Last occurrence index | `"hello".lastIndexOf("l")` → 3 |
| `.includes(str)` | Contains? (ES6) | `"hello".includes("ell")` → true |
| `.slice(start, end)` | Extract portion (end exclusive) | `"hello".slice(1, 4)` → "ell" |
| `.substring(start, end)` | Similar to slice, no negative index | `"hello".substring(1, 4)` → "ell" |
| `.split(delimiter)` | Split into array | `"a,b,c".split(",")` → ["a","b","c"] |
| `.replace(old, new)` | Replace first occurrence | `"hello".replace("l", "r")` → "herlo" |
| `.replaceAll(old, new)` | Replace all | `"hello".replaceAll("l", "r")` → "herro" |
| `.repeat(n)` | Repeat string | `"ha".repeat(3)` → "hahaha" |
| `.match(regex)` | Match against regex | `"abc123".match(/\d+/)` → ["123"] |

**Destructuring with split:**
```js
let email = "john@gmail.com";
let [id, domain] = email.split("@");
// id = "john", domain = "gmail.com"
```

### Boolean

Two values: `true` and `false`. Used in conditions.

```js
let inStock = true;
if (inStock) {
    console.log("Available");
}
```

> **Truthy vs Falsy (Interview):**
> 
> **Falsy values (only these 8):** `false`, `0`, `-0`, `0n`, `""`, `null`, `undefined`, `NaN`
> 
> **Everything else is truthy** — including `[]`, `{}`, `"0"`, `"false"`

### Undefined

A variable that is **declared but not assigned** a value.

```js
let x;
console.log(x);        // undefined
console.log(typeof x); // "undefined"
```

**`undefined` vs `not defined`:**
```js
let x;
console.log(x);  // undefined — memory exists, no value
console.log(y);  // ReferenceError: y is not defined — no memory allocated
```

### Null

Intentional absence of any value. Assigned explicitly.

```js
let result = null;
typeof result;          // "object" (historical bug in JS — interview trivia)
result === null;        // true (correct way to check)
```

> **Interview:** "undefined means a variable exists but hasn't been assigned. null means deliberately empty."

### Symbol (ES6)

Creates a **unique, hidden** identifier. Used as object keys that won't collide with other keys.

```js
let id = Symbol("userId");
let user = {
    [id]: "john_123",
    name: "John",
    age: 22
};

// Symbol keys are skipped in for...in loops
for (let key in user) {
    console.log(key);    // only "name" and "age"
}

console.log(user[id]);   // "john_123" — accessible directly
```

---

## Non-Primitive Types

### Array

Ordered collection of values. Can store **mixed types**. Size is **dynamic**.

```js
let arr = [10, "TV", true, [1, 2], function() { return "hi"; }];
arr[0];          // 10
arr[3][1];       // 2
arr[4]();        // "hi"
arr.length;      // 5
```

#### Array Methods — Reading

| Method | What it does |
|--------|-------------|
| `toString()` | Returns comma-separated string |
| `join(sep)` | Joins with custom separator |
| `slice(start, end)` | Extract portion (non-destructive) |
| `map(fn)` | Transform each element, returns new array |
| `forEach(fn)` | Iterate each element (no return) |
| `filter(fn)` | Return elements matching condition |
| `find(fn)` | Return first match |
| `reduce(fn, init)` | Accumulate into single value |
| `some(fn)` | Any element matches? (boolean) |
| `every(fn)` | All elements match? (boolean) |
| `includes(val)` | Contains value? (boolean) |
| `indexOf(val)` | Index of value (-1 if not found) |
| `flat(depth)` | Flatten nested arrays |

#### Array Methods — Modifying

| Method | What it does |
|--------|-------------|
| `push(...items)` | Add to end |
| `pop()` | Remove from end, returns it |
| `unshift(...items)` | Add to beginning |
| `shift()` | Remove from beginning, returns it |
| `splice(i, delCount, ...items)` | Insert/remove at position |
| `sort()` | Sort in-place (lexicographic by default) |
| `reverse()` | Reverse in-place |

**Destructuring:**
```js
let [a, b, ...rest] = [10, 20, 30, 40];
// a = 10, b = 20, rest = [30, 40]
```

#### Iterating Arrays

```js
// for...of — values
for (let value of arr) { }

// for...in — keys/indexes (string type)
for (let index in arr) { }

// forEach
arr.forEach((value, index) => { });

// map (returns new array)
let doubled = arr.map(x => x * 2);
```

### Object

Key-value collection. Keys are strings (or Symbols). Values can be anything.

```js
let product = {
    name: "Samsung TV",
    price: 46000.44,
    inStock: true,
    ratings: { rate: 4.3, count: 17890 }
};
```

**Access:** `product.name` or `product["name"]`

**`this` keyword:** Inside an object's method, `this` refers to the current object.

```js
let product = {
    name: "TV",
    price: 100,
    qty: 2,
    total() {
        return this.price * this.qty;
    }
};
product.total();   // 200
```

**Useful operations:**

```js
Object.keys(product);           // ["name", "price", "inStock", "ratings"]
Object.values(product);         // [values...]
Object.entries(product);        // [[key, value], ...]
"price" in product;             // true
delete product.inStock;         // removes key
```

**Destructuring:**
```js
let { name, price } = product;
let { name: productName } = product;   // rename
```

**Spread operator:**
```js
let copy = { ...product, qty: 5 };     // shallow copy + override
```

### JSON — JavaScript Object Notation

When an object contains **only data** (no functions), it can be represented as **JSON**.

```json
{
    "name": "Samsung TV",
    "price": 46000
}
```

```js
// Parse JSON string → object
let obj = JSON.parse(jsonString);

// Object → JSON string
let str = JSON.stringify(obj);
```

- JSON keys must be **double-quoted strings**
- Values: string, number, boolean, null, array, object (no functions)
- JSON files use `.json` extension
- APIs communicate using JSON over HTTP

### Map

Key-value collection like Object, but keys can be **any type** (not just strings).

```js
let topics = new Map();
topics.set(1, "HTML");
topics.set("categories", ["Electronics", "Fashion"]);

topics.get(1);            // "HTML"
topics.has("categories"); // true
topics.size;              // 2
topics.delete(1);
topics.clear();
```

| Map vs Object | Map | Object |
|--------------|-----|--------|
| Key types | Any | String or Symbol |
| Order | Insertion order guaranteed | Mostly insertion order |
| Size | `.size` property | `Object.keys(obj).length` |
| Iteration | Built-in `.forEach`, `for...of` | Need `Object.keys/entries` |
| Performance | Better for frequent add/delete | Better for static lookup |

---

## Date & Time

```js
let now = new Date();                            // current date/time
let custom = new Date("2023-04-20T18:30:21");    // specific date

// Getters
now.getFullYear();   // 2026
now.getMonth();      // 0-11 (0 = January)
now.getDate();       // 1-31
now.getDay();        // 0-6 (0 = Sunday)
now.getHours();      // 0-23
now.getMinutes();    // 0-59
now.getSeconds();    // 0-59

// Formatting
now.toLocaleDateString();   // "4/25/2026"
now.toLocaleTimeString();   // "11:30:00 PM"
now.toLocaleString();       // both
now.toISOString();          // "2026-04-25T18:00:00.000Z"
```

### Timer Events

```js
// Execute once after delay
let timer = setTimeout(() => {
    console.log("Runs after 2 seconds");
}, 2000);
clearTimeout(timer);     // cancel before it runs

// Execute repeatedly at interval
let interval = setInterval(() => {
    console.log("Every 1 second");
}, 1000);
clearInterval(interval); // stop repeating
```

- `setTimeout` — **debounce** pattern: delay execution, cancel if called again
- `setInterval` — **polling** pattern: repeat until explicitly stopped

---

## `typeof` Operator

Returns the type of a value as a string.

```js
typeof 42;            // "number"
typeof "hello";       // "string"
typeof true;          // "boolean"
typeof undefined;     // "undefined"
typeof null;          // "object"       ← historical bug
typeof {};            // "object"
typeof [];            // "object"       ← arrays are objects
typeof function(){}; // "function"
typeof Symbol();      // "symbol"
typeof 42n;           // "bigint"
```

**Reliable checks:**
```js
Array.isArray([]);           // true
x === null;                  // check for null
x === undefined;             // check for undefined
```

---

**Next:** [Module 05 — Operators & Statements](05-operators-and-statements.md)
