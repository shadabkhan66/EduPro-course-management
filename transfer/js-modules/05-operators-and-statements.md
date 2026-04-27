# Module 05 — Operators & Statements

---

## Operators

### 1. Arithmetic Operators

| Operator | Description | Example |
|----------|-------------|---------|
| `+` | Addition (or string concatenation) | `5 + 3` → 8 |
| `-` | Subtraction | `5 - 3` → 2 |
| `*` | Multiplication | `5 * 3` → 15 |
| `/` | Division | `10 / 3` → 3.333 |
| `%` | Modulus (remainder) | `10 % 3` → 1 |
| `**` | Exponentiation | `2 ** 3` → 8 |
| `++` | Increment | `x++` (post) / `++x` (pre) |
| `--` | Decrement | `x--` (post) / `--x` (pre) |

**Post vs Pre Increment:**
```js
let x = 10;
let y = x++;    // y = 10, x = 11 (use then increment)
let z = ++x;    // z = 12, x = 12 (increment then use)
```

**Math Object:**
```js
Math.PI;            // 3.14159...
Math.round(4.6);    // 5
Math.floor(4.9);    // 4
Math.ceil(4.1);     // 5
Math.sqrt(16);      // 4
Math.pow(2, 3);     // 8
Math.abs(-5);       // 5
Math.random();      // 0 to 0.999...
Math.max(1, 5, 3);  // 5
Math.min(1, 5, 3);  // 1
```

### 2. Comparison Operators

| Operator | Description | Example |
|----------|-------------|---------|
| `==` | Equal (with type coercion) | `10 == "10"` → true |
| `===` | Strict equal (no coercion) | `10 === "10"` → false |
| `!=` | Not equal (with coercion) | `10 != "10"` → false |
| `!==` | Strict not equal | `10 !== "10"` → true |
| `>` `>=` `<` `<=` | Greater/Less than | `5 > 3` → true |

> **Interview:** "`==` performs type coercion before comparing — `1 == '1'` is true. `===` checks both value and type without coercion — always prefer `===`."

### 3. Logical Operators

| Operator | Description | Example |
|----------|-------------|---------|
| `&&` | AND — true if both true | `true && false` → false |
| `\|\|` | OR — true if either true | `true \|\| false` → true |
| `!` | NOT — inverts boolean | `!true` → false |

**Short-circuit evaluation:**
```js
let name = userInput || "Guest";      // default value if falsy
let result = obj && obj.method();     // safe access if obj exists
```

**Nullish Coalescing `??` (ES2020):**
```js
let name = userInput ?? "Guest";
// Only falls through on null or undefined (not on 0 or "")
```

**Optional Chaining `?.` (ES2020):**
```js
let rating = product?.ratings?.rate;
// Returns undefined instead of throwing if any part is null/undefined
```

### 4. Assignment Operators

| Operator | Equivalent |
|----------|------------|
| `x += 5` | `x = x + 5` |
| `x -= 5` | `x = x - 5` |
| `x *= 5` | `x = x * 5` |
| `x /= 5` | `x = x / 5` |
| `x %= 5` | `x = x % 5` |
| `x **= 2` | `x = x ** 2` |
| `x &&= val` | Assign if truthy |
| `x \|\|= val` | Assign if falsy |
| `x ??= val` | Assign if null/undefined |

### 5. Special Operators

| Operator | Description |
|----------|-------------|
| `typeof x` | Returns type as string |
| `x instanceof Class` | Is `x` an instance of `Class`? |
| `delete obj.key` | Removes a key from object |
| `"key" in obj` | Checks if key exists in object |
| `new Constructor()` | Creates new instance |
| `? :` | Ternary — `condition ? ifTrue : ifFalse` |
| `void expr` | Evaluates expression, returns `undefined` |
| `...` | Spread / Rest operator |

**Ternary:**
```js
let status = age >= 18 ? "Adult" : "Minor";
```

---

## Statements

### 1. Selection (Conditional)

**if / else if / else:**
```js
if (score >= 90) {
    grade = "A";
} else if (score >= 80) {
    grade = "B";
} else {
    grade = "C";
}
```

**switch / case:**
```js
switch (day) {
    case "Monday":
        console.log("Start of week");
        break;
    case "Friday":
        console.log("Almost weekend");
        break;
    default:
        console.log("Regular day");
}
```

### 2. Looping

**for:**
```js
for (let i = 0; i < 5; i++) {
    console.log(i);
}
```

**while:**
```js
let i = 0;
while (i < 5) {
    console.log(i);
    i++;
}
```

**do...while:**
```js
let i = 0;
do {
    console.log(i);
    i++;
} while (i < 5);
```

### 3. Iteration

**for...in — iterates over keys/properties:**
```js
let obj = { name: "TV", price: 100 };
for (let key in obj) {
    console.log(key, obj[key]);     // "name" "TV", "price" 100
}
```

**for...of — iterates over values (arrays, strings, Maps, Sets):**
```js
let arr = [10, 20, 30];
for (let value of arr) {
    console.log(value);             // 10, 20, 30
}
```

> **Interview:** "`for...in` is for object properties (keys). `for...of` is for iterable values (arrays, strings). Don't use `for...in` on arrays — it iterates keys as strings and includes inherited properties."

### 4. Jump Statements

```js
break;         // exit loop or switch immediately
continue;      // skip current iteration, go to next
return value;  // exit function and return value
```

### 5. Exception Handling

```js
try {
    // code that might throw
    let result = riskyOperation();
} catch (error) {
    // handle the error
    console.error(error.message);
} finally {
    // always runs — cleanup code
    closeConnection();
}
```

**Throw custom errors:**
```js
function divide(a, b) {
    if (b === 0) throw new Error("Division by zero");
    return a / b;
}
```

**Error types:** `Error`, `TypeError`, `RangeError`, `ReferenceError`, `SyntaxError`

> **Interview:** "try/catch handles runtime errors gracefully. The finally block always executes — useful for cleanup like closing connections, similar to Java's try-with-resources concept."

---

**Next:** [Module 06 — Functions](06-functions.md)
