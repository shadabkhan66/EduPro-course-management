# Module 02 — DOM: How JavaScript Refers HTML Elements

---

## What is the DOM?

The **Document Object Model (DOM)** is a tree-shaped representation of your HTML page that JavaScript can read and modify.

```
                    window
                      │
                   document
                      │
                    <html>
                   /      \
               <head>    <body>
                /           \
           <title>     <div>, <form>, <img>...
```

- Every HTML element becomes a **node** in this tree.
- JavaScript accesses and manipulates nodes through the `document` object.
- The DOM is **not** JavaScript — it's a browser API that JavaScript interacts with.

> **Interview:** "The DOM is the browser's in-memory representation of the HTML document. JavaScript uses the DOM API to read, create, update, and delete elements dynamically."

---

## 4 Ways to Refer HTML Elements

### 1. By DOM Hierarchy (Index-Based)

Access elements using collection indexes.

```js
window.document.images[0]                    // first <img>
window.document.forms[0].elements[0]         // first input of first form
```

- **Fastest** native method for rendering.
- **Fragile** — changing element position in HTML breaks the index references.
- Not recommended for maintainable code.

### 2. By Name Attribute

Every element can have a `name` attribute. JavaScript can access it directly.

```html
<img name="pic">
<form name="frmRegister">
    <input name="btnRegister" type="button">
</form>
```

```js
pic.src = "images/fashion.jpg";
frmRegister.btnRegister.value = "Register";
```

- Cannot access child elements directly without parent reference.
- If multiple elements share the same name, behavior is inconsistent.

### 3. By ID — `getElementById()`

Access any element directly from any level using its unique `id`.

```html
<img id="pic">
```

```js
document.getElementById("pic").src = "images/fashion.jpg";
```

- Direct access regardless of DOM depth.
- IDs **should be unique** per page (though browsers don't enforce it strictly).
- Most commonly used before `querySelector` was introduced.

### 4. By CSS Selectors — `querySelector()` / `querySelectorAll()`

Use **any CSS selector** to find elements — most versatile approach.

```js
document.querySelector("img")                // first <img> element
document.querySelector(".btn-login")         // first element with class
document.querySelector("#btnLogin")          // element with id
document.querySelectorAll(".card")           // NodeList of all matches
```

- Supports **all CSS selectors**: element, class, id, attribute, pseudo-class, combinators.
- `querySelector()` — returns **first** match (or `null`).
- `querySelectorAll()` — returns **all** matches as a static `NodeList`.

> **Best Practice:** Use `querySelector` / `querySelectorAll` for new code — they are flexible and consistent.

---

## Summary: Which Method to Use

```
┌──────────────────────────────────────────────────┐
│  Method                │  Speed   │ Flexibility  │
│────────────────────────┼──────────┼──────────────│
│  DOM hierarchy [0]     │  Fastest │  Fragile     │
│  By name               │  Fast    │  Limited     │
│  getElementById()      │  Fast    │  ID only     │
│  querySelector()       │  Good    │  Any CSS sel │
│  querySelectorAll()    │  Good    │  All matches │
└──────────────────────────────────────────────────┘
```

| For... | Use... |
|--------|--------|
| Quick single element lookup by id | `getElementById()` |
| Complex selectors, class, attribute | `querySelector()` |
| All matching elements | `querySelectorAll()` |
| Legacy code / forms | Name or hierarchy |

---

## Creating Elements Dynamically

JavaScript can create new HTML elements and add them to the page at runtime.

**Steps:**
1. Create the element: `document.createElement("tagName")`
2. Set properties: `.src`, `.className`, `.innerHTML`, etc.
3. Append to parent: `parentElement.appendChild(newElement)`

```js
const li = document.createElement("li");
li.textContent = "New Item";
document.querySelector("ul").appendChild(li);
```

> **Note:** Use `.textContent` for plain text (safe). Use `.innerHTML` only when you need to insert HTML markup — and **never** with untrusted user input (XSS risk).

---

## Modifying & Removing Elements

```js
// Change content
element.textContent = "New text";
element.innerHTML = "<b>Bold text</b>";

// Change styles
element.style.display = "none";
element.style.backgroundColor = "red";

// Change classes
element.classList.add("active");
element.classList.remove("hidden");
element.classList.toggle("selected");

// Remove element
element.remove();
// or: parent.removeChild(element);
```

---

**Next:** [Module 03 — Output & Input Techniques](03-output-input-techniques.md)
