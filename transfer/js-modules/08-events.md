# Module 08 — JavaScript Events

---

## What is an Event?

An event is a **notification** sent by an element (sender) to a handler (subscriber) when something happens — click, keypress, mouse move, form submit, page load, etc.

This follows the **Observer pattern** — subscribers define actions; senders trigger notifications.

```
┌──────────────────┐         ┌──────────────────┐
│  Sender (Button) │ ──────► │  Subscriber (Fn)  │
│  onclick="..."   │ event   │  InsertClick()    │
└──────────────────┘         └──────────────────┘
```

---

## 3 Ways to Handle Events

### 1. Inline Event Handler (HTML Attribute)

```html
<button onclick="InsertClick()">Insert</button>
```

### 2. DOM Property

```js
document.getElementById("btn").onclick = function() {
    console.log("Clicked");
};
```

### 3. `addEventListener()` (Recommended)

```js
document.getElementById("btn").addEventListener("click", function(e) {
    console.log("Clicked");
});
```

**Why `addEventListener` is preferred:**
- Can attach **multiple** handlers to same event
- Supports **capture** and **bubble** phases
- Can be **removed** with `removeEventListener()`
- Clean separation of HTML and JavaScript

```js
// Adding with options
element.addEventListener("click", handler, { once: true });     // runs once
element.addEventListener("click", handler, { capture: true });  // capture phase
```

---

## Event Object — `this` and `event`

Every event handler receives two key pieces of information:

### `this` — Current Element

Contains info about the element: `id`, `name`, `className`, `value`, etc.

### `event` (or `e`) — Event Details

Contains event-specific data: `clientX`, `clientY`, `key`, `ctrlKey`, `shiftKey`, `target`, etc.

```html
<button onclick="handleClick(this, event)" id="btn" value="Save">Save</button>
```

```js
function handleClick(el, e) {
    console.log(el.id);         // "btn"
    console.log(el.value);      // "Save"
    console.log(e.clientX);     // mouse X position
    console.log(e.ctrlKey);     // true if Ctrl was held
}
```

With `addEventListener`, the event object is the **first parameter**:

```js
btn.addEventListener("click", function(e) {
    console.log(e.target.id);      // element that triggered event
    console.log(e.currentTarget);  // element handler is attached to
});
```

---

## Event Categories

### Mouse Events

| Event | Triggers When |
|-------|---------------|
| `click` | Single click |
| `dblclick` | Double click |
| `contextmenu` | Right click |
| `mousedown` | Mouse button pressed |
| `mouseup` | Mouse button released |
| `mouseover` | Mouse enters element |
| `mouseout` | Mouse leaves element |
| `mousemove` | Mouse moves over element |

```js
element.addEventListener("mousemove", function(e) {
    console.log(`X: ${e.clientX}, Y: ${e.clientY}`);
});
```

### Keyboard Events

| Event | Triggers When |
|-------|---------------|
| `keydown` | Key pressed down |
| `keyup` | Key released |
| `keypress` | Key pressed (deprecated — use `keydown`) |

```js
input.addEventListener("keyup", function(e) {
    console.log(e.key);          // "a", "Enter", "Shift"
    console.log(e.code);         // "KeyA", "Enter", "ShiftLeft"
});
```

### Form Events

| Event | Triggers When |
|-------|---------------|
| `submit` | Form is submitted |
| `reset` | Form is reset |
| `change` | Value changes (after losing focus) |
| `input` | Value changes (immediately, every keystroke) |
| `focus` | Element gains focus |
| `blur` | Element loses focus |

```html
<form onsubmit="handleSubmit(event)">
    <input type="text" name="username">
    <button type="submit">Submit</button>
</form>
```

```js
function handleSubmit(e) {
    e.preventDefault();       // prevent page reload
    console.log("Form submitted");
}
```

> **`e.preventDefault()`** — stops the browser's default action (form submit, link navigation, etc.)

### Element State Events

| Event | Triggers When |
|-------|---------------|
| `focus` | Element gets focus |
| `blur` | Element loses focus |
| `change` | Value changes (select, checkbox, radio) |
| `load` | Page or resource finished loading |
| `resize` | Window is resized |
| `scroll` | Element is scrolled |

### Clipboard Events

| Event | Triggers When |
|-------|---------------|
| `cut` | Content is cut |
| `copy` | Content is copied |
| `paste` | Content is pasted |

### Touch Events (Mobile)

| Event | Triggers When |
|-------|---------------|
| `touchstart` | Finger touches screen |
| `touchend` | Finger lifts from screen |
| `touchmove` | Finger moves on screen |

---

## Event Propagation: Bubbling & Capturing

When an event fires on a nested element, it travels through the DOM in phases:

```
           CAPTURING PHASE (down)
           window → document → html → body → div → button
                                                    ↓
                                              TARGET PHASE
                                                    ↓
           BUBBLING PHASE (up)
           button → div → body → html → document → window
```

```
┌─────────────────────────────────────────┐
│  ① Capture (top → target)               │
│    ┌─────────────────────────────┐      │
│    │  ② Target (event fires)     │      │
│    │    ┌─────────────────┐      │      │
│    │    │  ③ Bubble        │      │      │
│    │    │  (target → top)  │      │      │
│    │    └─────────────────┘      │      │
│    └─────────────────────────────┘      │
└─────────────────────────────────────────┘
```

**By default**, handlers run in the **bubble** phase.

```js
// Capture phase
parent.addEventListener("click", handler, { capture: true });

// Bubble phase (default)
parent.addEventListener("click", handler);
```

**Stop propagation:**
```js
e.stopPropagation();           // stop bubbling/capturing
e.stopImmediatePropagation();  // also stops other handlers on same element
```

> **Interview:** "Events bubble up from target to root by default. Use `stopPropagation()` to prevent parent handlers from firing. `preventDefault()` stops the browser's default behavior (like navigation or form submission) — these are two different things."

---

## Event Delegation

Instead of adding listeners to every child, add **one listener to the parent** and check `e.target`.

```js
document.querySelector("ul").addEventListener("click", function(e) {
    if (e.target.tagName === "LI") {
        console.log("Clicked: " + e.target.textContent);
    }
});
```

**Benefits:**
- Works for **dynamically added** elements
- Better **memory** usage (fewer listeners)
- Commonly used in React's event system under the hood

---

## Disabling Events

```js
// Disable right-click
document.oncontextmenu = function() {
    return false;
};

// Disable text selection
document.onselectstart = function() {
    return false;
};
```

---

**Next:** [Module 09 — State Management](09-state-management.md)
