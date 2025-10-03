# Kotlin DataFrame for SQL & Backend Developers

<web-summary>
Quickly transition from SQL to Kotlin DataFrame: load your datasets, perform essential transformations, and visualize your results — directly within a Kotlin Notebook.
</web-summary>

<card-summary>
Switching from SQL? Kotlin DataFrame makes it easy to load, process, analyze, and visualize your data — fully interactive and type-safe!
</card-summary>

<link-summary>
Explore Kotlin DataFrame as a SQL or ORM user: read your data, transform columns, group or join tables, and build insightful visualizations with Kotlin Notebook.
</link-summary>

This guide helps Kotlin backend developers with SQL experience quickly adapt to **Kotlin DataFrame**, mapping familiar
SQL and ORM operations to DataFrame concepts.

If you plan to work on a Gradle project without a Kotlin Notebook,
we recommend installing the library together with our [**experimental Kotlin compiler plugin**](Compiler-Plugin.md) (available since version 2.2.*).
This plugin generates type-safe schemas at compile time, 
tracking schema changes throughout your data pipeline.

## Add Kotlin DataFrame Gradle dependency

You could read more about the setup of the Gradle build in the [Gradle Setup Guide](SetupGradle.md).

In your Gradle build file (`build.gradle` or `build.gradle.kts`), add the Kotlin DataFrame library as a dependency:

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:dataframe:%dataFrameVersion%")
}
```

</tab>

<tab title="Groovy DSL">

```groovy
dependencies {
    implementation 'org.jetbrains.kotlinx:dataframe:%dataFrameVersion%'
}
```

</tab>
</tabs>

---

## 1. What is a dataframe?

If you’re used to SQL, a **dataframe** is conceptually like a **table**:

- **Rows**: ordered records of data
- **Columns**: named, typed fields
- **Schema**: a mapping of column names to types

Kotlin DataFrame also supports [**hierarchical, JSON-like data**](hierarchical.md) —
columns can contain *[nested dataframes](DataColumn.md#framecolumn)* or *column groups*,
allowing you to represent and transform tree-like structures without flattening.

Unlike a relational DB table:

- A DataFrame object **lives in memory** — there’s no storage engine or transaction log
- It’s **immutable** — each operation produces a *new* DataFrame
- There is **no concept of foreign keys or relations** between DataFrames
- It can be created from
  *any* [source](Data-Sources.md): [CSV](CSV-TSV.md), [JSON](JSON.md), [SQL tables](SQL.md), [Apache Arrow](ApacheArrow.md),
  in-memory objects

---

## 2. Reading Data From SQL

Kotlin DataFrame integrates with JDBC, so you can bring SQL data into memory for analysis.

| Approach                         | Example                                                             |
|----------------------------------|---------------------------------------------------------------------|
| **From a table**                 | `val df = DataFrame.readSqlTable(dbConfig, "customers")`            |
| **From a SQL query**             | `val df = DataFrame.readSqlQuery(dbConfig, "SELECT * FROM orders")` |
| **From a JDBC Connection**       | `val df = connection.readDataFrame("SELECT * FROM orders")`         |
| **From a ResultSet (extension)** | `val df = resultSet.readDataFrame(connection)`                      |

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig

val dbConfig = DbConnectionConfig(
    url = "jdbc:postgresql://localhost:5432/mydb",
    user = "postgres",
    password = "secret"
)

// Table
val customers = DataFrame.readSqlTable(dbConfig, "customers")

// Query
val salesByRegion = DataFrame.readSqlQuery(
    dbConfig, """
    SELECT region, SUM(amount) AS total
    FROM sales
    GROUP BY region
"""
)

// From JDBC connection
connection.readDataFrame("SELECT * FROM orders")

// From ResultSet
val rs = connection.createStatement().executeQuery("SELECT * FROM orders")
rs.readDataFrame(connection)
```

More information can be found [here](readSqlDatabases.md).

## 3. Why It’s Not an ORM

Frameworks like **[Hibernate](https://hibernate.org/orm/)** or **[Exposed](https://github.com/JetBrains/Exposed)**:

- Map DB tables to Kotlin objects (entities)
- Track object changes and sync them back to the database
- Focus on **persistence** and **transactions**

Kotlin DataFrame:

- Has no persistence layer
- Doesn’t try to map rows to mutable entities
- Focuses on **in-memory analytics**, **transformations**, and **type-safe pipelines**
- The **main idea** is that the schema *changes together with your transformations* — and the [**Compiler Plugin
  **](Compiler-Plugin.md) updates the type-safe API automatically under the hood.
    - You don’t have to manually define or recreate schemas every time — the plugin infers them dynamically from the data or
      transformations.
- In ORMs, the mapping layer is **frozen** — schema changes require manual model edits and migrations.

Think of Kotlin DataFrame as a **data analysis/ETL tool**, not an ORM.

---

## 4. Key Differences from SQL & ORMs

| Feature / Concept          | SQL Databases (PostgreSQL, MySQL…) | ORM (Hibernate, Exposed…)          | Kotlin DataFrame                                                    |
|----------------------------|------------------------------------|------------------------------------|---------------------------------------------------------------------|
| **Storage**                | Persistent                         | Persistent                         | In-memory only                                                      |
| **Schema definition**      | `CREATE TABLE` DDL                 | Defined in entity classes          | Derived from data or transformations or defined manually            |
| **Schema change**          | `ALTER TABLE`                      | Manual migration of entity classes | Automatic via transformations + Compiler Plugin or defined manually |
| **Relations**              | Foreign keys                       | Mapped via annotations             | Not applicable                                                      |
| **Transactions**           | Yes                                | Yes                                | Not applicable                                                      |
| **DB Indexes**             | Yes                                | Yes (via DB)                       | Not applicable                                                      |
| **Data manipulation**      | SQL DML (`INSERT`, `UPDATE`)       | CRUD mapped to DB                  | Transformations only (immutable)                                    |
| **Joins**                  | `JOIN` keyword                     | Eager/lazy loading                 | [`.join()` / `.leftJoin()` DSL](join.md)                            |
| **Grouping & aggregation** | `GROUP BY`                         | DB query with groupBy              | [`.groupBy().aggregate()`](groupBy.md)                              |
| **Filtering**              | `WHERE`                            | Criteria API / query DSL           | [`.filter { ... }`](filter.md)                                      |
| **Permissions**            | `GRANT` / `REVOKE`                 | DB-level permissions               | Not applicable                                                      |
| **Execution**              | On DB engine                       | On DB engine                       | In JVM process                                                      |

---

## 5. SQL → Kotlin DataFrame Cheatsheet

### DDL Analogues

| SQL DDL Command / Example                                                                                     | Kotlin DataFrame Equivalent                                                                  |
|---------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------|
| **Create table:**<br>`CREATE TABLE person (name text, age int);`                                              | `@DataSchema`<br>`interface Person {`<br>`    val name: String`<br>`    val age: Int`<br>`}` |
| **Add column:**<br>`ALTER TABLE sales ADD COLUMN profit numeric GENERATED ALWAYS AS (revenue - cost) STORED;` | `.add("profit") { revenue - cost }`                                                          |
| **Rename column:**<br>`ALTER TABLE sales RENAME COLUMN old_name TO new_name;`                                 | `.rename { old_name }.into("new_name")`                                                      |
| **Drop column:**<br>`ALTER TABLE sales DROP COLUMN old_col;`                                                  | `.remove { old_col }`                                                                        |
| **Modify column type:**<br>`ALTER TABLE sales ALTER COLUMN amount TYPE numeric;`                              | `.convert { amount }.to<Double>()`                                                           |

---

### DML Analogues

| SQL DML Command / Example                                                                                                                              | Kotlin DataFrame Equivalent            |
|--------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------|
| `SELECT col1, col2`                                                                                                                                    | `df.select { col1 and col2 }`          |
| `WHERE amount > 100`                                                                                                                                   | `df.filter { amount > 100 }`           |
| `ORDER BY amount DESC`                                                                                                                                 | `df.sortByDesc { amount }`             |
| `GROUP BY region`                                                                                                                                      | `df.groupBy { region }`                |
| `SUM(amount)`                                                                                                                                          | `.aggregate {  sum { amount } }`           |
| `JOIN`                                                                                                                                                 | `.join(otherDf) { id match right.id }` |
| `LIMIT 5`                                                                                                                                              | `.take(5)`                             |
| **Pivot:** <br>`SELECT * FROM crosstab('SELECT region, year, SUM(amount) FROM sales GROUP BY region, year') AS ct(region text, y2023 int, y2024 int);` | `.pivot(region, year) {  sum { amount } }` |
| **Explode array column:** <br>`SELECT id, unnest(tags) AS tag FROM products;`                                                                          | `.explode { tags }`                    |
| **Update column:** <br>`UPDATE sales SET amount = amount * 1.2;`                                                                                       | `.update { amount }.with { it * 1.2 }` |

## 6. Example: SQL vs. DataFrame Side-by-Side

**SQL (PostgreSQL):**

```sql
SELECT region, SUM(amount) AS total
FROM sales
WHERE amount > 0
GROUP BY region
ORDER BY total DESC LIMIT 5;
```

```kotlin
sales.filter { amount > 0 }
    .groupBy { region }
    .aggregate { sum { amount } into "total" }
    .sortByDesc { total }
    .take(5)
```

## In Conclusion

- Kotlin DataFrame keeps the familiar SQL-style workflow (select → filter → group → aggregate) but makes it **type-safe
  ** and fully integrated into Kotlin.
- The main focus is **readability** and schema change safety via
  the [Compiler Plugin](Compiler-Plugin.md).
- It is neither a database nor an ORM — a Kotlin DataFrame library does not store data or manage transactions but works as an in-memory
  layer for analytics and transformations.
- It does not provide some SQL features (permissions, transactions, indexes) — but offers convenient tools for working
  with JSON-like structures and combining multiple data sources.
- Use Kotlin DataFrame as a **type-safe DSL** for post-processing, merging data sources, and analytics directly on the
  JVM, while keeping your code easily refactorable and IDE-assisted.
- Use Kotlin DataFrame for small- and average-sized datasets, but for large datasets, consider using a more
  **performant** database engine.

## What's Next?

If you're ready to go through a complete example, we recommend our **[Quickstart Guide](quickstart.md)**
— you'll learn the basics of reading data, transforming it, and creating visualization step-by-step.

Ready to go deeper? Check out what’s next:

- 📘 **[Explore in-depth guides and various examples](Guides-And-Examples.md)** with different datasets,
  API usage examples, and practical scenarios that help you understand the main features of Kotlin DataFrame.

- 🛠️ **[Browse the operations overview](operations.md)** to learn what Kotlin DataFrame can do.

- 🧠 **Understand the design** and core concepts in the [library overview](concepts.md).

- 🔤 **[Learn more about Extension Properties](extensionPropertiesApi.md)**  
  and make working with your data both convenient and type-safe.

- 💡 **[Use Kotlin DataFrame Compiler Plugin](Compiler-Plugin.md)**  
  for auto-generated column access in your IntelliJ IDEA projects.

- 📊 **Master Kandy** for stunning and expressive DataFrame visualizations
  [Kandy Documentation](https://kotlin.github.io/kandy).
