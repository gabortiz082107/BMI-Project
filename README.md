# BMI Tracking System (Java, JDBC, Swing)

A complete OOP capstone application: manage client BMI records (Create,
Read, Update, Delete) through a Swing desktop GUI backed by a SQLite
database accessed via JDBC. BMI and weight category are computed live
from height/weight — never stored redundantly.

## 1. Project Structure

```
BMITrackingSystem/
├── pom.xml
├── .gitignore
└── src/main/java/com/bmitracker/
    ├── Main.java                      # entry point
    ├── model/
    │   ├── Person.java                # abstract base class
    │   └── BMIRecord.java             # concrete subclass, BMI math
    ├── dao/
    │   ├── Crud.java                  # generic CRUD interface
    │   └── BMIRecordDAO.java          # JDBC implementation (DAO pattern)
    ├── service/
    │   └── BMIService.java            # business logic layer
    ├── util/
    │   └── DatabaseConnection.java    # Singleton JDBC connection
    └── gui/
        └── BMITrackerGUI.java         # Swing GUI, event-driven
```

## 2. Requirements → Where They're Implemented

| Requirement | Where |
|---|---|
| Database connectivity (JDBC) | `util/DatabaseConnection.java`, `dao/BMIRecordDAO.java` |
| CRUD + object persistence | `dao/BMIRecordDAO.java` (implements `Crud<BMIRecord>`) |
| Encapsulation | Private fields + validated getters/setters in `Person`, `BMIRecord` |
| Inheritance | `BMIRecord extends Person` |
| Polymorphism | Overridden `getRole()`, `toString()` in `BMIRecord` |
| Abstraction | `Person` abstract class, `Crud<T>` interface, BMI formula hidden behind `getBmi()`/`getCategory()` |
| Collections | `List<Double> weightHistoryKg` in `BMIRecord`; `List<BMIRecord>` throughout |
| GUI / event-driven programming | `gui/BMITrackerGUI.java` (Swing `ActionListener`, `ListSelectionListener`) |
| SOLID principles | See section 4 below |
| Design patterns | Singleton (`DatabaseConnection`), DAO (`BMIRecordDAO`) |
| Real-world problem | BMI tracking for clients (add, edit, delete, search, categorize) |

## 3. How It Works

Each record stores a client's name, email, height (cm), and weight (kg).
`BMIRecord.getBmi()` computes `weight / (height in meters)^2` on demand,
and `getCategory()` classifies it using the standard WHO adult bands:

| BMI | Category |
|---|---|
| < 18.5 | Underweight |
| 18.5 – 24.9 | Normal |
| 25.0 – 29.9 | Overweight |
| ≥ 30.0 | Obese |

## 4. How to Run

**Requirements:** JDK 17+, Maven 3.8+

```bash
cd BMITrackingSystem
mvn clean package
java -jar target/bmi-tracking-system.jar //COPY PASTE MO LANG TO SA TERMINAL
```

This builds a single runnable JAR with the SQLite driver bundled in, and
creates `bmidb.sqlite` (and the `bmi_records` table) automatically on
first run — no manual database setup needed.

*Using an IDE (IntelliJ/Eclipse) instead:* import as a Maven project,
let it download `sqlite-jdbc`, then run `Main.java` directly.

> I wasn't able to compile this in my sandbox (no JDK/network there) —
> please run `mvn clean package` yourself to confirm a clean build
> before your defense.

## 5. SOLID Principles Applied

- **Single Responsibility** — `BMIRecordDAO` only talks to the database;
  `BMIService` only holds business rules; `BMITrackerGUI` only handles
  presentation/events. Each class has one reason to change.
- **Open/Closed** — new business rules (e.g. `getByCategory()`) are
  added to `BMIService` without modifying `BMIRecordDAO` or the GUI.
- **Liskov Substitution** — any `Person` subclass can be used wherever a
  `Person` is expected without breaking behavior.
- **Interface Segregation** — `Crud<T>` exposes only the five methods
  every consumer actually needs; no bloated "god interface."
- **Dependency Inversion** — `BMIService` depends on the `Crud<BMIRecord>`
  *interface*, not the concrete `BMIRecordDAO` class, so the DAO is
  injected via the constructor and can be swapped/mocked freely.

## 6. Design Patterns

- **Singleton** (`DatabaseConnection`) — ensures exactly one JDBC
  connection is shared app-wide.
- **DAO (Data Access Object)** (`BMIRecordDAO`) — isolates all SQL
  behind a clean interface so the GUI and service layer never see raw SQL.

## 7. Suggested Git Workflow (for your submission history)

```bash
git init
git add .
git commit -m "Initial project structure"

git add src/main/java/com/bmitracker/model
git commit -m "Add Person and BMIRecord model classes (inheritance, BMI logic)"

git add src/main/java/com/bmitracker/dao
git commit -m "Add Crud interface and BMIRecordDAO (JDBC CRUD, DAO pattern)"

git add src/main/java/com/bmitracker/util
git commit -m "Add Singleton DatabaseConnection"

git add src/main/java/com/bmitracker/service
git commit -m "Add BMIService business logic layer (SOLID/DIP)"

git add src/main/java/com/bmitracker/gui src/main/java/com/bmitracker/Main.java
git commit -m "Add Swing GUI with event-driven CRUD operations"

git remote add origin <your-repo-url>
git push -u origin main
```

Refactoring example worth mentioning in your defense: BMI was initially
considered as a stored column, but was refactored into a computed
method (`getBmi()`) on `BMIRecord` so the value can never drift out of
sync with height/weight — a small abstraction win you can point to.

## 8. Possible Extensions (if you want to go further)

- Store a full history table (one row per weigh-in) instead of a single
  current weight, and chart BMI trend over time.
- Add age/gender-adjusted categories, or a `Child` subclass of `Person`
  using pediatric BMI percentiles (more inheritance/polymorphism).
- Add a Factory pattern for creating different `Person` subtypes.
- Add unit tests for `BMIService` using a mock `Crud<BMIRecord>`.
