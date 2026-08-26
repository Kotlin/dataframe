# AGENTS.md — core

Guidance for this module. See the root `AGENTS.md` for repo-wide build/style/KoDEx rules; only module-specific
details are here. `core` is the base module (artifact `dataframe-core`) — it has no dependency on any other
`dataframe-*` module; everything else depends on it.

Paths below abbreviate `core/src/main/kotlin/org/jetbrains/kotlinx/dataframe/` as `…/`.

## Central types & the column model

Top-level public interfaces, each in its own file at `…/`:

- **`DataFrame<out T>`** (`DataFrame.kt`) — immutable, ordered list of columns (distinct non-empty names, equal
  sizes). `T` is a **schema marker** (covariant), not stored data — it resolves the generated typesafe extension
  properties (see Codegen). Impl in `impl/DataFrameImpl.kt`.
- **`DataColumn<out T>`** (`DataColumn.kt`) — named, typed column; its companion is the main factory
  (`createValueColumn`, `createColumnGroup`, `createFrameColumn`, `createByInference`).
- **`DataRow<out T>`** (`DataRow.kt`) — a single row (`index()`, `df()`, cell getters). Impl `impl/DataRowImpl.kt`.
- Access hierarchy: **`ColumnsScope`** (`ColumnsScope.kt`, minimal `get(name)`) ⊃ **`ColumnsContainer`**
  (`ColumnsContainer.kt`) ⊃ `DataFrame`. Generated extension properties hang off `ColumnsScope<Schema>`.

**Column kinds** (`columns/ColumnKind.kt`: `Value`/`Group`/`Frame`) are what make schemas *hierarchical*:

- `ValueColumn<T>` (`columns/ValueColumn.kt`) — plain values.
- `ColumnGroup<T>` (`columns/ColumnGroup.kt`) — a nested set of columns; behaves as both a column **and** a
  `DataFrame`. This is how nested/JSON-like structures are represented.
- `FrameColumn<T>` (`columns/FrameColumn.kt`) — each cell is itself a `DataFrame` (produced by `groupBy`).

Type aliases in `aliases.kt`: `AnyFrame`/`AnyRow`/`AnyCol` and the DSL lambda aliases `ColumnsSelector<T,C>`,
`ColumnSelector<T,C>`, `RowFilter<T>`, `RowExpression`, etc. The `columns/` package holds the lower-level
abstractions the DSL resolves against: `BaseColumn`, `ColumnReference`, `ColumnAccessor`, `ColumnSet`,
`ColumnsResolver`, `SingleColumn`, `ColumnPath`, `ColumnWithPath`.

## Operations API (`api/`) and the `api → impl` convention

~125 files in `api/`, **one file per operation**, lowercase-named after the operation (`filter.kt`, `select.kt`,
`groupBy.kt`, `convert.kt`, `move.kt`, `pivot.kt`, `join.kt`, `add.kt`, …). PascalCase files hold supporting API
types (`ColumnsSelectionDsl.kt`, `DataFrameGet.kt`, `DataRowApi.kt`, `ParserOptions.kt`, `Defaults.kt`).

A typical operation file exposes public extension functions on `DataFrame<T>` (plus overloads on `DataColumn`,
`GroupBy`, `ColumnsSelectionDsl`), grouped with `// region …` comments, and usually offers overloads for a
`ColumnsSelector` lambda, `vararg String`, and (deprecated) `KProperty`/`ColumnReference`.

**Delegation convention — follow it when adding/changing operations:** the `api/xxx.kt` function is a thin façade;
the real logic lives in `impl/api/xxxImpl.kt`. `impl/` mirrors the public packages (`impl/api/`, `impl/columns/`,
`impl/aggregation/`, `impl/schema/`, `impl/codeGen/`, `impl/io/`) and holds all concrete classes and helpers
(`DataFrameImpl`, `GroupByImpl`, `TypeUtils`, resolution decorators like `ColumnWithParent`). Keep new internal
logic in `impl/`, not in the public file.

Operations are annotated for the compiler / IntelliJ plugins (`@Refine`, `@Interpretable("…")`, `@AccessApiOverload`,
`@RequiredByIntellijPlugin`, from `annotations/`). Preserve these annotations when editing signatures.

## Column(s) Selection DSL

The receiver of the selector lambdas (`ColumnsSelector`/`ColumnSelector`, defined in `aliases.kt`) is
**`ColumnsSelectionDsl<out T>`** (`api/ColumnsSelectionDsl.kt`) — used by
`select`/`move`/`convert`/`group`/`remove`/`update`/etc. — which inherits the smaller base
**`ColumnSelectionDsl<out T>`** (`api/ColumnSelectionDsl.kt`). Whether a selector resolves to a **single** column or
**multiple** is decided by the lambda's **return type** (`SingleColumn<C>` vs `ColumnsResolver<C>`), not by the
receiver. `ColumnsSelectionDsl` is a large **composite interface**: it inherits one per-feature DSL interface per operation
file (e.g. `all.kt` → `AllColumnsSelectionDsl`, `cols.kt` → `ColsColumnsSelectionDsl`). It carries a `@DslMarker`
to prevent accessor leakage across nested `select`s. Lambdas resolve to a `columns/ColumnsResolver<C>`
(`SingleColumn` or `ColumnSet`) against the frame via `impl/DataFrameReceiver.kt` (`getColumnsImpl`, driven by an
`UnresolvedColumnsPolicy`). When adding an operation usable inside a selector, add its DSL interface in the same
`api/` file and wire it into `ColumnsSelectionDsl`.

## Typesafe access: compiler plugin vs codegen

Extension properties on `ColumnsScope<Schema>` (returning `DataColumn<X>`) and `DataRow<Schema>` (returning `X`),
each with a `@JvmName`, are what make `df.someColumn` type-safe — and why `T` is a schema marker. There are three
ways they get produced, and picking the wrong one is a common mistake:

- **Kotlin compiler plugin — the recommended path.** It generates accessors on-the-fly at compile time (and in the
  IDE) with no `@DataSchema` declaration needed. It lives in the Kotlin repo
  (`github.com/JetBrains/kotlin/tree/master/plugins/kotlin-dataframe`), not here. The operation annotations in this
  module (`@Refine`, `@Interpretable("…")`, `@RequiredByIntellijPlugin`, `@AccessApiOverload`, from `annotations/`)
  exist specifically to drive it, and it consumes `dataframe-compiler-plugin-core` for its interpreters.
- **KSP symbol processor + Gradle plugin — the older `@DataSchema` build-time path (now legacy/deprecated).**
  `@DataSchema` (`annotations/DataSchema.kt`) marks an interface as a schema (related: `ColumnName`,
  `ImportDataSchema`, `HasSchema`, … in `annotations/`); `plugins/symbol-processor` + `plugins/dataframe-gradle-plugin`
  invoke this module's `CodeGenerator` at build time to emit accessors. Note: `plugins/symbol-processor` is now
  disabled because KSP1 is no longer compatible with Kotlin 2.3+ (`plugins/README.md`).
- **Runtime `ReplCodeGenerator` — Jupyter/REPL only.** `impl/codeGen/ReplCodeGenerator` emits accessor source as
  strings to be `eval`'d in the next notebook cell (used by the `jupyter/` helpers); it is not a path for ordinary
  compiled projects. The underlying engine `CodeGenerator` (`codeGen/CodeGenerator.kt`, `ExtensionsCodeGenerator.kt`,
  `SchemaProcessor.kt`, `Marker*.kt`, `NameNormalizer.kt`, `DefaultReadDfMethods.kt`; impl in `impl/codeGen/`) is
  **shared** — it is what the build-time `@DataSchema` path above also calls.

The runtime schema model (used for generation and schema comparison) is in `schema/` (`DataFrameSchema.kt`,
`ColumnSchema.kt`, `CompareResult.kt`).

**Checked-in accessors for `:core` itself:** `core/src/generated-dataschema-accessors/{main,test}/…` holds
pre-generated `<SchemaSimpleName>$Extensions.kt` for `:core`'s own internal `@DataSchema` types (e.g.
`ColumnDescription`, `ValueCount`). They are committed **only because `:core` cannot attach the compiler plugin to
itself** — the plugin depends on a subset of `:core`, so doing so would be circular. `core/generated-sources/…` is
the build-time KoDEx output. **Don't hand-edit any of these** — they are generated (the build registers the
accessors dir as a source root in `build.gradle.kts`). In `core/generated-sources/` (358 files) the
**implementation and public API match** `src/main/kotlin/…` (KoDEx only expands the KDoc and strips
`@ExcludeFromSources` doc-only fragments), so don't grep/browse it for implementation — it only bloats context;
work from `src/main/kotlin/…`. The one reason to open a generated file is to read a symbol's **fully-expanded
KDoc** (KoDEx resolves all `{@include …}`/`@set`/`@get` there).

## Other packages (one line each)

- `aggregation/` — DSLs/contracts for reductions (`Aggregatable`, `AggregateDsl`, `NamedValue`), used by
  `aggregate`/`groupBy`/`pivot`.
- `math/` — statistical primitives (`mean`, `median`, `std`, `sum`, `percentile`, `cumsum`, …).
- `dataTypes/` — rich cell value types for rendering (`IMG`, `IFRAME`).
- `jupyter/` — helper utilities (`JupyterConfiguration`, `CellRenderer`, `KotlinNotebookPluginUtils`, …); the
  actual Kotlin Notebook integration lives in `:dataframe-jupyter`.
- `io/` — read/write helpers and the **deprecated** in-core csv/tsv/html integrations (`csv.kt`, `tsv.kt`,
  `html.kt`, `guess.kt`, `Compression.kt`); prefer the dedicated `dataframe-csv`/`dataframe-json` modules.
- `documentation/` — see below. `exceptions/` — custom exceptions. `util/` — deprecation-message constants.

## KoDEx documentation-only pattern (core specifics)

The general rule (reuse/`@include` fragments; see `KDOC_GUIDELINES.md` / `KODEX_KDOC_PREPROCESSING.md`) is in the
root `AGENTS.md`. Core-specific: `core` hosts the **shared cross-module KDoc-fragment library** in its
`documentation/` package (`SelectingColumns.kt`, `SelectingRows.kt`, `DocumentationUrls.kt`, `DslGrammar.kt`, …) —
`internal`/`private` interfaces and `typealias … = Nothing` declarations with no runtime behavior, `@include`d into
operation KDoc (also inline in operation files, e.g. `internal interface Select` in `api/select.kt`). Other KoDEx
modules keep their own local fragments (e.g. csv's `documentationCsv/`).

## Build specifics

- Applies `buildConfig`, `kodex`, `keywordsGenerator` convention plugins plus `korro`,
  `binary-compatibility-validator`, and `kotlinx.benchmark`. (For the `apiDump`/`apiCheck` flow, see root `AGENTS.md`.)
- Extra source sets beyond `main`/`test`: **`samples`** (`src/test/kotlin`, compiled with the expressions-converter
  compiler plugin; runs documentation samples via `samplesTest`/`korro` — see root `AGENTS.md`) and **`testJava16`**
  (`src/testJava16/`, compiled at JDK 16 for Java-Records tests).
- Run just this module: `./gradlew core:test` (add `-Pkotlin.dataframe.debug=true` to match CI). The debug checks
  are relevant for **every** module (jupyter/example integration tests, runtime `DataColumn`-vs-`KType` checks in
  jdbc, …), not just `core`.
