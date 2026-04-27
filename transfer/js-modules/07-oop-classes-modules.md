# Module 07 — OOP: Classes, Modules & Inheritance

---

## Programming Paradigms

| Paradigm | Description | Examples |
|----------|-------------|---------|
| **POPS** (Procedural) | Step-by-step instructions, low-level, fast | C, Pascal |
| **OBPS** (Object-Based) | Objects without full OOP features | Early JavaScript, VBScript |
| **OOP** (Object-Oriented) | Encapsulation, inheritance, polymorphism | Java, C#, C++, Python |

JavaScript started as **Object-Based** but now supports full OOP patterns through **prototypes** and **ES6 classes**.

> **Interview:** "JavaScript is multi-paradigm — it supports procedural, object-oriented (prototype-based), and functional programming styles."

---

## JavaScript Module System

A module is a file containing functions, variables, and classes that can be **exported** and **imported** across different files.

### Types of Module Systems

| System | Used In |
|--------|---------|
| **CommonJS** (`require` / `module.exports`) | Node.js (legacy) |
| **ESModule** (`import` / `export`) | Browsers & modern Node.js |
| AMD / UMD | Legacy bundlers |

### ES Modules (Standard)

**Creating a module — export members:**

```js
// math.module.js
export function add(a, b) {
    return a + b;
}

export const PI = 3.14159;

export default function multiply(a, b) {
    return a * b;
}
```

**Importing:**

```js
// app.js
import multiply, { add, PI } from "./math.module.js";

console.log(add(2, 3));      // 5
console.log(multiply(2, 3)); // 6
console.log(PI);              // 3.14159
```

**In HTML:**
```html
<script type="module" src="app.js"></script>
```

**Key rules:**
- `export` makes a member available to other modules
- `export default` — one per module, imported without `{ }`
- `import { name }` — named imports
- Modules are **strict mode** by default
- Modules have their own **scope** (no global pollution)

> **Interview:** "ES modules are statically analyzable — the imports and exports are determined at parse time, enabling tree-shaking (dead code elimination) by bundlers."

---

## Classes (ES6)

A class is a **template** (blueprint) for creating objects with shared structure and behavior.

### Declaration vs Expression

```js
// Declaration
class Product { }

// Expression
const Product = class { };
```

### Class Members

A JavaScript class can have:

1. **Properties** — store data
2. **Methods** — define behavior
3. **Constructor** — runs when creating an instance
4. **Accessors** (get/set) — control property read/write

### Properties

```js
class Product {
    name = "Default";
    price = 0;
    inStock = true;
}

let tv = new Product();
tv.name = "Samsung TV";
tv.price = 45000;
console.log(tv.name);    // "Samsung TV"
```

### Methods

```js
class Product {
    name = "";
    price = 0;
    qty = 0;

    total() {
        return this.price * this.qty;
    }

    display() {
        console.log(`${this.name} - ₹${this.price} x ${this.qty} = ₹${this.total()}`);
    }
}

let tv = new Product();
tv.name = "TV";
tv.price = 45000;
tv.qty = 2;
tv.display();    // "TV - ₹45000 x 2 = ₹90000"
```

### Constructor

A special method that runs **once** when a new instance is created.

```js
class Database {
    constructor(dbName) {
        this.dbName = dbName;
        console.log(`Connected to ${dbName}`);
    }

    insert() {
        console.log(`Record inserted into ${this.dbName}`);
    }
}

let db = new Database("MySQL");    // "Connected to MySQL"
db.insert();                        // "Record inserted into MySQL"
```

- Only **one** constructor per class (no overloading in JS).
- Use default parameters for flexibility: `constructor(name = "default")`.

### Accessors (Getters & Setters)

Control **how** properties are read and written.

```js
class Product {
    _name = "";

    get name() {
        return this._name.toUpperCase();
    }

    set name(value) {
        if (value.length < 2) {
            console.error("Name too short");
            return;
        }
        this._name = value;
    }
}

let p = new Product();
p.name = "TV";              // uses setter
console.log(p.name);        // "TV" — uses getter (returns uppercase)
p.name = "A";               // "Name too short"
```

- Convention: prefix internal property with `_` (e.g., `_name`)
- Access like a property (no parentheses): `p.name` not `p.name()`
- Useful for validation, computed values, access control

---

## Inheritance

Accessing members of one class from another by establishing a **parent-child relationship**.

### `extends` Keyword

```js
class Animal {
    name;
    speak() {
        console.log(`${this.name} makes a sound`);
    }
}

class Dog extends Animal {
    breed;
    speak() {
        console.log(`${this.name} barks`);
    }
}

let dog = new Dog();
dog.name = "Rex";
dog.breed = "Labrador";
dog.speak();    // "Rex barks"
```

### `super` Keyword

Access the **parent class** constructor or methods.

```js
class Shape {
    constructor(color) {
        this.color = color;
    }
    describe() {
        return `A ${this.color} shape`;
    }
}

class Circle extends Shape {
    constructor(color, radius) {
        super(color);              // call parent constructor
        this.radius = radius;
    }
    describe() {
        return `${super.describe()} — circle with radius ${this.radius}`;
    }
}

let c = new Circle("red", 5);
c.describe();   // "A red shape — circle with radius 5"
```

### Aggregation (Has-A Relationship)

Using an instance of one class **inside** another without inheritance.

```js
class Engine {
    start() { console.log("Engine started"); }
}

class Car {
    constructor() {
        this.engine = new Engine();   // has-an engine
    }
    drive() {
        this.engine.start();
        console.log("Car is moving");
    }
}
```

> **Interview:** "Inheritance creates an IS-A relationship (Dog IS-A Animal). Aggregation creates a HAS-A relationship (Car HAS-A Engine). Prefer composition over inheritance when relationships aren't truly hierarchical."

---

## Polymorphism

One interface, multiple implementations. A parent reference can point to different child objects.

```js
class Employee {
    name;
    role;
    describe() {
        console.log(`${this.name} - ${this.role}`);
    }
}

class Developer extends Employee {
    constructor() {
        super();
        this.name = "Raj";
        this.role = "Developer";
    }
    describe() {
        super.describe();
        console.log("Responsibilities: Build, Debug, Test, Deploy");
    }
}

class Manager extends Employee {
    constructor() {
        super();
        this.name = "Tom";
        this.role = "Manager";
    }
    describe() {
        super.describe();
        console.log("Responsibilities: Approvals, Reviews");
    }
}

let team = [new Developer(), new Manager()];
team.forEach(emp => emp.describe());    // each calls its own version
```

---

## Static Members

Belong to the **class** itself, not instances.

```js
class MathUtils {
    static PI = 3.14159;

    static circleArea(radius) {
        return MathUtils.PI * radius ** 2;
    }
}

MathUtils.circleArea(5);     // 78.539...
// Cannot call on instance: new MathUtils().circleArea() — error
```

---

## Private Fields (ES2022)

Prefix with `#` — truly private, not accessible outside the class.

```js
class BankAccount {
    #balance = 0;

    deposit(amount) {
        this.#balance += amount;
    }

    get balance() {
        return this.#balance;
    }
}

let acc = new BankAccount();
acc.deposit(1000);
console.log(acc.balance);     // 1000
// acc.#balance;              // SyntaxError — private
```

> **Java comparison:** JavaScript `#private` is similar to Java's `private` keyword. Before `#`, JS used `_` prefix as a convention (no enforcement).

---

**Next:** [Module 08 — Events](08-events.md)
