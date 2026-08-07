# CLAUDE.md — core

Guidance for this module. See the root `CLAUDE.md` for repo-wide build/style/KoDEx rules; only module-specific
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

## Column selection DSL

`ColumnsSelectionDsl<out T>` (`api/ColumnsSelectionDsl.kt`) and `ColumnSelectionDsl<T>` (`api/ColumnSelectionDsl.kt`)
are the receivers of the selector lambdas used by `select`/`move`/`convert`/`group`/`remove`/`update`/etc.
`ColumnsSelectionDsl` is a large **composite interface**: it inherits one per-feature DSL interface per operation
file (e.g. `all.kt` → `AllColumnsSelectionDsl`, `cols.kt` → `ColsColumnsSelectionDsl`). It carries a `@DslMarker`
to prevent accessor leakage across nested `select`s. Lambdas resolve to a `columns/ColumnsResolver<C>`
(`SingleColumn` or `ColumnSet`) against the frame via `impl/DataFrameReceiver.kt` (`getColumnsImpl`, driven by an
`UnresolvedColumnsPolicy`). When adding an operation usable inside a selector, add its DSL interface in the same
`api/` file and wire it into `ColumnsSelectionDsl`.

## Codegen & schemas (typesafe access)

`@DataSchema` (`annotations/DataSchema.kt`) marks an interface as a schema. For each schema, extension properties are
generated on `ColumnsScope<Schema>` (returning `DataColumn<X>`) and `DataRow<Schema>` (returning `X`), each with a
`@JvmName` — this is what makes `df.someColumn` type-safe, and why `T` is a schema marker. Related annotations live
in `annotations/` (`ColumnName`, `ImportDataSchema`, `HasSchema`, …).

- The generator lives in `codeGen/` (`CodeGenerator.kt`, `ExtensionsCodeGenerator.kt`, `SchemaProcessor.kt`,
  `Marker*.kt`, `NameNormalizer.kt`, `DefaultReadDfMethods.kt`) with runtime impl in `impl/codeGen/`. The runtime
  schema model (used for generation and schema comparison) is in `schema/` (`DataFrameSchema.kt`, `ColumnSchema.kt`,
  `CompareResult.kt`).
- Generated accessor source roots: **`core/src/generated-dataschema-accessors/{main,test}/…`** (checked-in accessors
  for internal schemas such as `ColumnDescription`) and `core/generated-sources/…` (build-time output). Naming
  convention: `<SchemaSimpleName>$Extensions.kt`. **Don't hand-edit these** — they are generated (and the build
  registers the accessors dir as a source root in `build.gradle.kts`).

## Other packages (one line each)

- `aggregation/` — DSLs/contracts for reductions (`Aggregatable`, `AggregateDsl`, `NamedValue`), used by
  `aggregate`/`groupBy`/`pivot`.
- `math/` — statistical primitives (`mean`, `median`, `std`, `sum`, `percentile`, `cumsum`, …).
- `dataTypes/` — rich cell value types for rendering (`IMG`, `IFRAME`).
- `jupyter/` — Kotlin Notebook integration (`JupyterConfiguration`, `CellRenderer`, `KotlinNotebookPluginUtils`).
- `io/` — read/write helpers and the **deprecated** in-core csv/tsv/html integrations (`csv.kt`, `tsv.kt`,
  `html.kt`, `guess.kt`, `Compression.kt`); prefer the dedicated `dataframe-csv`/`dataframe-json` modules.
- `documentation/` — see below. `exceptions/` — custom exceptions. `util/` — deprecation-message constants.

## KoDEx documentation-only pattern

Reusable KDoc fragments are written once as `internal`/`private` interfaces and `typealias … = Nothing`
declarations (in `documentation/`: `SelectingColumns.kt`, `SelectingRows.kt`, `DocumentationUrls.kt`,
`DslGrammar.kt`, … — and inline in operation files, e.g. `internal interface Select` in `api/select.kt`). They carry
**no runtime behavior**; they exist purely to compose docs via `@include [X]`, `{@set [KEY] value}`, `{@get}`, and
`@ExcludeFromSources`. When editing operation KDoc, edit the fragment and use the templating — don't duplicate prose.

## Build specifics

- Applies `buildConfig`, `kodex`, `keywordsGenerator` convention plugins plus `korro`, `binary-compatibility-validator`,
  and `kotlinx.benchmark`. Public-API changes require `apiDump`/`apiCheck` and committing the `.api` dump.
- Extra source sets beyond `main`/`test`: **`samples`** (`src/test/kotlin`, compiled with the expressions-converter
  compiler plugin; runs documentation samples via `samplesTest`/`korro` — see root `CLAUDE.md`) and **`testJava16`**
  (`src/testJava16/`, compiled at JDK 16 for Java-Records tests).
- Run just this module: `./gradlew core:test` (add `-Pkotlin.dataframe.debug=true` to match CI — this module has the
  most debug-only checks).
