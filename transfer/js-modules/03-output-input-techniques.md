# Module 03 — JavaScript Output & Input Techniques

---

## Output Techniques

### 1. `alert()`

Displays a message popup. User must click OK to continue.

```js
alert("Hello World");
alert("Line 1 \n Line 2");       // \n for new line in popup
alert(10 + 20);                   // shows: 30
```

- Cannot display formatted text (no HTML).
- Blocks code execution until dismissed.
- Used for quick debugging or critical warnings — avoid in production UI.

### 2. `confirm()`

Like `alert()` but with **OK** and **Cancel** buttons. Returns a boolean.

```js
let result = confirm("Are you sure you want to delete?");
// result = true  (OK clicked)
// result = false (Cancel clicked)
```

### 3. `document.write()`

Writes content directly to the page. Can render HTML markup.

```js
document.write("Hello World");
document.write("<h2>Formatted Output</h2>");
document.write("<br>");                      // use <br> not \n
```

- **Caution:** If called after the page has loaded, it **replaces the entire page content**.
- Useful only for quick demos — never use in production.

### 4. `innerText`

Sets or gets the **text-only** content of a container element (no HTML rendering).

```js
document.querySelector("p").innerText = "Deleted";
```

- Strips and ignores HTML tags.
- RC (Replaced Content) elements like `<input>` use `.value` instead.

### 5. `innerHTML`

Sets or gets **HTML content** of a container element. Can render markup.

```js
document.querySelector("div").innerHTML = "<b>Deleted</b>";
```

- **XSS Warning:** Never set `innerHTML` with untrusted user input.
  ```js
  // DANGEROUS — user can inject scripts
  element.innerHTML = userInput;
  
  // SAFE — use textContent for plain text
  element.textContent = userInput;
  ```

### 6. `outerHTML`

Replaces the **entire element** (including itself) with new markup.

```js
document.querySelector("p").outerHTML = "<h2>Welcome</h2>";
// The <p> is gone, replaced by <h2>
```

### 7. `textContent` (Preferred over `innerText`)

Similar to `innerText` but:
- Returns text of **hidden** elements too
- Slightly faster (doesn't trigger reflow)
- Safer for setting plain text

```js
element.textContent = "Safe plain text";
```

### 8. Console Methods

Developer tools (F12 → Console tab) — used for debugging, **not for user-facing output**.

```js
console.log("General message");
console.error("Error message");           // red styling
console.warn("Warning message");          // yellow styling
console.info("Info message");
console.table([{a:1}, {a:2}]);           // tabular display
console.time("timer"); /* code */ console.timeEnd("timer");  // measure time
```

> **Interview:** "Console methods are for development and debugging. In production, use proper logging services."

---

## Output Summary

```
┌────────────────┬──────────────┬────────────┬──────────────┐
│  Method        │  HTML Support│  Where     │  Use Case    │
│────────────────┼──────────────┼────────────┼──────────────│
│  alert()       │  No          │  Popup     │  Quick warn  │
│  confirm()     │  No          │  Popup     │  Yes/No ask  │
│  document.write│  Yes         │  Page      │  Demos only  │
│  innerText     │  No          │  Element   │  Plain text  │
│  textContent   │  No          │  Element   │  Plain text  │
│  innerHTML     │  Yes         │  Element   │  HTML output │
│  outerHTML     │  Yes         │  Replaces  │  Replace el  │
│  console.*     │  No          │  DevTools  │  Debugging   │
└────────────────┴──────────────┴────────────┴──────────────┘
```

---

## Input Techniques

### 1. Query String

Data appended to URL after `?` as key-value pairs.

```
http://example.com/page.html?category=electronics&page=2
```

```js
let params = new URLSearchParams(location.search);
let category = params.get("category");     // "electronics"
let page = params.get("page");              // "2"
```

- **Older approach** (still in your notes):
  ```js
  let str = location.search;   // "?category=electronics"
  let value = str.substring(str.indexOf("=") + 1);
  ```
- Use `URLSearchParams` in modern code — cleaner and handles encoding.

### 2. `prompt()`

Shows a popup input box. Returns the user's input as a **string**.

```js
let name = prompt("Enter your name");
let age = prompt("Enter age", "25");     // with default value
```

**Return values:**
- `"typed value"` — OK clicked with input
- `""` (empty string) — OK clicked without input
- `null` — Cancel clicked

```js
let folder = prompt("Enter folder name");
if (folder === null) {
    alert("Cancelled");
} else if (folder.trim() === "") {
    alert("Name cannot be empty");
} else {
    console.log("Created: " + folder);
}
```

### 3. Form Input Elements

The primary way to get user input — text, number, email, checkbox, radio, select, etc.

```html
<input type="text" id="txtName">
<input type="number" id="txtAge">
<select id="lstCity">
    <option>Delhi</option>
    <option>Hyderabad</option>
</select>
<input type="checkbox" id="chkStock">
```

**Reading values:**

```js
let name = document.getElementById("txtName").value;
let age = document.getElementById("txtAge").value;       // always returns string
let city = document.getElementById("lstCity").value;
let inStock = document.getElementById("chkStock").checked;  // boolean
```

> **Important:** Form element `.value` always returns a **string**. Convert when needed:
> ```js
> let age = parseInt(document.getElementById("txtAge").value);
> let price = parseFloat(document.getElementById("txtPrice").value);
> ```

---

## Input Summary

| Method | Returns | Use Case |
|--------|---------|----------|
| Query String | string from URL | Page-to-page data, search, filters |
| `prompt()` | string or null | Quick input (not for production) |
| Form Elements | `.value` (string), `.checked` (boolean) | Standard user input |

---

**Next:** [Module 04 — Variables & Data Types](04-variables-and-data-types.md)
