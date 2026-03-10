# Duplicate Issues

This document lists open GitHub issues that are deemed duplicates of each other,
based on analysis of all 257 open issues in this repository as of March 2026.

Issues are grouped by the feature or topic they describe. For each group the
strongest candidate for being the **canonical** issue to keep is listed first.

---

## 1. Windowed / Rolling-Window Operations

Both issues request adding windowed / rolling-window support (moving averages,
rolling calculations, etc.) to DataFrame – the same feature described from two
different angles. A comment in #335 already links back to #95.

| Issue | Title |
|-------|-------|
| [#95](https://github.com/Kotlin/dataframe/issues/95) | Windowed operations (rolling average, etc.) |
| [#335](https://github.com/Kotlin/dataframe/issues/335) | Dataframe Time Series Resampling and rolling calculations like python pandas dataframe |

---

## 2. Public API Documentation (KDocs)

Both issues report the same problem – the public API lacks KDoc documentation –
and are both assigned to the same person under the milestone "KDocs for the
public API".

| Issue | Title |
|-------|-------|
| [#29](https://github.com/Kotlin/dataframe/issues/29) | Document API |
| [#158](https://github.com/Kotlin/dataframe/issues/158) | Lack of API-docs makes it very hard to onboard users |

---

## 3. `split` with Data Classes / Complex Types

Both issues request that the `split` operation work transparently with
non-primitive, non-string types (data classes, `Pair`, arbitrary classes such
as `LocalDateTime`). They describe the same desired improvement using different
examples.

| Issue | Title |
|-------|-------|
| [#310](https://github.com/Kotlin/dataframe/issues/310) | Split function should work with Pairs and data classes |
| [#488](https://github.com/Kotlin/dataframe/issues/488) | Split by operation on "normal classes" could be clearer |

---

## 4. SQLite Type-Mapping / ClassCast Bugs

Both issues are ClassCast exceptions that occur when reading from a SQLite
database due to incorrect JDBC type-to-Kotlin-type mapping (SQLite's `INTEGER`
storing `Long` instead of `Int`, or `0`/`1` instead of `Boolean`). They share
the same root cause and the same area of code.

| Issue | Title |
|-------|-------|
| [#964](https://github.com/Kotlin/dataframe/issues/964) | ClassCast exception when reading from specific sqlite db |
| [#1013](https://github.com/Kotlin/dataframe/issues/1013) | Kotlin Dataframe SQLite Integer cannot be cast to Boolean in Notebook |

---

## 5. Inline Type Aliases in Public API

Both issues ask to inline type aliases so that the public API exposes concrete
types to users instead of opaque aliases. #906 is the broader request (inline
`AnyFrame`, `AnyRow`, `AnyBaseCol`, etc. in the entire `api` package); #1699
is a specific instance of the same goal (`ColumnFilter` alias).

| Issue | Title |
|-------|-------|
| [#906](https://github.com/Kotlin/dataframe/issues/906) | Inline some type aliases in public API |
| [#1699](https://github.com/Kotlin/dataframe/issues/1699) | Inline ColumnFilter typealias in public API |

---

## 6. Unit Tests for the `DbType` Class

Both issues call for proper unit tests for `DbType`. #1586 is the broader
request (mock-based testing of fallbacks, `SQLExceptions`, and general
behaviour); #1736 is a focused follow-up covering the `getExpectedJdbcType`
method specifically (triggered by the bug found in #762 / #1735). Given that
both target the same class and both aim to increase unit-test coverage that is
currently missing, they substantially overlap.

| Issue | Title |
|-------|-------|
| [#1586](https://github.com/Kotlin/dataframe/issues/1586) | Create tests for DbType class |
| [#1736](https://github.com/Kotlin/dataframe/issues/1736) | Add `getExpectedJdbcType` tests for different `DbType`s |

---

## 7. Phase Out KProperties / Column Accessors Usage in Examples

Both issues were created on the same day by the same author, have identical
bodies, and describe the same task – removing KProperties/Column Accessor
usages from the project so that the `core-compat` split (#1156) becomes
possible. The only difference is the scope: IDEA Gradle examples vs. Jupyter
notebook examples. They are effectively two sub-tasks of the same goal and
carry a strong risk of being treated as independent work items that duplicate
each other.

| Issue | Title |
|-------|-------|
| [#1157](https://github.com/Kotlin/dataframe/issues/1157) | Phase out usages of KProperties/Column Accessors in IDEA examples |
| [#1159](https://github.com/Kotlin/dataframe/issues/1159) | Phase out usages of KProperties/Column Accessors in example notebooks |

---

## Notes

* Issues #1526, #1527, #1528, and #1536 are all sub-tasks of the umbrella
  issue [#821](https://github.com/Kotlin/dataframe/issues/821) ("Add KDocs for
  `groupBy`/`pivot`"). They are not duplicates of each other, but since #821 is
  still open it largely supersedes them.

* Issues #762 (MySQL `BigInteger` → `Long`) and #964 / #1013 (SQLite type
  mismatches) share a common theme (incorrect JDBC type mapping causing
  `ClassCastException`) but affect different databases and different type
  mappings, so they are listed separately.
