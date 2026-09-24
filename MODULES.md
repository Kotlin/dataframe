# Kotlin DataFrame Modules

An overview of every module in the repository: what it publishes and what it depends on. Module-internal
structure, patterns and gotchas live in each module's own `README.md` and `AGENTS.md` — this file does not
repeat them.
The built set is defined by `settings.gradle.kts`; anything not listed there is not compiled or tested.
All published modules use the group `org.jetbrains.kotlinx`.

## Aggregate Artifact

### dataframe (root project)
**Description**: What a user normally depends on — `core` plus the I/O modules, re-exported
- **Location**: `./`
- **Artifact**: `dataframe`
- **Dependencies**: `api` on core, dataframe-arrow, dataframe-csv, dataframe-excel, dataframe-jdbc, dataframe-json
- **Note**: `dataframe-openapi` is deliberately left out of the aggregate — still experimental.

## Core Module

### core
**Description**: DataFrame/DataColumn/DataRow types, the operations API, aggregations, math, schema handling, codegen
- **Location**: `./core/`
- **Artifact**: `dataframe-core`
- **Dependencies**: none (base module)
- **Note**: I/O was split off into the modules below; core still holds the deprecated csv/tsv and HTML integrations.

## I/O and Data Format Modules

Each depends on `core` and publishes under its own directory name.

### dataframe-arrow
**Description**: Apache Arrow IPC read/write — the streaming format (`readArrowIPC`/`writeArrowIPC`) and the
random-access/Feather format (`readArrowFeather`/`writeArrowFeather`) — plus Parquet reading via `readParquet`
- **Location**: `./dataframe-arrow/`
- **Dependencies**: core, Apache Arrow (vector/format/memory/dataset), commons-compress

### dataframe-csv
**Description**: CSV/TSV reading and writing (supersedes the deprecated implementation in core)
- **Location**: `./dataframe-csv/`
- **Dependencies**: core, dataframe-json, Deephaven CSV, commons-csv

### dataframe-excel
**Description**: Excel read/write, `.xlsx` and legacy `.xls`
- **Location**: `./dataframe-excel/`
- **Dependencies**: core, dataframe-json, Apache POI

### dataframe-json
**Description**: JSON reading, writing and schema inference
- **Location**: `./dataframe-json/`
- **Dependencies**: core, kotlinx.serialization

### dataframe-jdbc
**Description**: Reading from SQL databases over JDBC
- **Location**: `./dataframe-jdbc/`
- **Dependencies**: core; JDBC drivers are `compileOnly` or test-only, never shipped
- **Note**: built-in `DbType` support for H2, MariaDB, MySQL, MS SQL Server, PostgreSQL, SQLite and DuckDB; any
  other JDBC database works through a user-supplied `DbType`. See `dataframe-jdbc/AGENTS.md`.

## Integration Modules

### dataframe-jupyter
**Description**: Kotlin Jupyter integration — rendering and kernel setup
- **Location**: `./dataframe-jupyter/`
- **Dependencies**: the root `dataframe` aggregate (not `core` alone), Log4j

### dataframe-geo
**Description**: Geospatial column types and operations. **Experimental**
- **Location**: `./dataframe-geo/`
- **Dependencies**: core, GeoTools, JTS, Ktor client
- **Note**: targets Java 11, a restriction inherited from `org.jetbrains.kotlin.jupyter`.

### dataframe-geo-jupyter
**Description**: Jupyter rendering for geospatial data
- **Location**: `./dataframe-geo-jupyter/`
- **Dependencies**: dataframe-geo, dataframe-jupyter

### dataframe-openapi
**Description**: Runtime support for data schemas generated from OpenAPI 3.0.0 specifications. **Experimental**
- **Location**: `./dataframe-openapi/`
- **Dependencies**: core

### dataframe-openapi-generator
**Description**: Generates data schemas from OpenAPI 3.0.0 specifications; the sister module of
`dataframe-openapi`, consumed by the Gradle and Jupyter integrations. **Experimental**
- **Location**: `./dataframe-openapi-generator/`
- **Dependencies**: core, dataframe-openapi, Swagger parser, KotlinPoet

## Compiler and Build Plugins

### dataframe-compiler-plugin-core
**Description**: A shaded subset of `:core`, bundled *inside* the Kotlin DataFrame compiler plugin and IntelliJ to
run compile-time interpreters of operations. Not itself a compiler plugin.
- **Location**: `./dataframe-compiler-plugin-core/`
- **Artifact**: `dataframe-compiler-plugin-core`
- **Dependencies**: core, with most transitive dependencies excluded
- **Note**: the **compiler plugin itself is not in this repository** — it is developed in the Kotlin repository at
  `github.com/JetBrains/kotlin/tree/master/plugins/kotlin-dataframe`. See `dataframe-compiler-plugin-core/AGENTS.md`.

### plugins/expressions-converter
**Description**: Kotlin compiler plugin that extracts the intermediate DataFrame expressions out of
`@TransformDataFrameExpressions`-annotated functions, so the website's "explainer dataframe" iframes can be
generated from real sample runs
- **Location**: `./plugins/expressions-converter/`
- **Used by**: `:core`, on the `kotlinCompilerPluginClasspathSamples` configuration

### plugins/public-api-modifier
**Description**: Exploratory compiler plugin that turns `@AccessApiOverload`-annotated functions internal, to
allow a full-API and a reduced-API artifact from one source tree (PR #959)
- **Location**: `./plugins/public-api-modifier/`
- **Note**: built, but **not enabled anywhere** — no module puts it on a compiler-plugin classpath.

### Present in the tree but not built
None of these is in `settings.gradle.kts`, so nothing here is compiled or tested — but they are *not* all the
same kind of dead: two are deprecated products, one moved upstream, one moved into the build. Each has its own
`README.md`; read it and `plugins/AGENTS.md` before touching anything.

**`plugins/dataframe-gradle-plugin`** — the Gradle plugin, published as `org.jetbrains.kotlinx.dataframe` on the
Gradle Plugin Portal. Generated data schemas from a data sample through the `dataframes {}` task, and pulled in
the symbol processor automatically to produce column accessors.

**`plugins/symbol-processor`** — the KSP plugin, published as `symbol-processor-all`. Generated schemas from
`@file:ImportDataSchema`, and the extension properties for `@DataSchema` classes and interfaces.

Both are **deprecated as products**, not merely switched off in this build: their last release is
`1.0.0-Beta4` and there will be no further ones, because KSP1 is no longer compatible with Kotlin 2.3+. Schema
generation now goes through dedicated runtime methods and the compiler plugin — see
`docs/StardustDocs/topics/schemas/Migration-From-Plugins.md`,
`docs/StardustDocs/topics/schemas/DataSchemaGenerationMethods.md` and
`docs/StardustDocs/topics/schemas/gradle/Gradle-Plugin.md`; the next iteration of the feature is issue #1844.

**`plugins/kotlin-dataframe`** — the Kotlin 2.x compiler plugin, which is the **recommended** way to get
compile-time extension properties. It is not deprecated: development simply moved to the Kotlin repository
(`github.com/JetBrains/kotlin/tree/master/plugins/kotlin-dataframe`), and the copy here is an out-of-date
snapshot with its tests turned off (issue #1290). See `docs/StardustDocs/topics/compiler-plugin.md`.

**`plugins/keywords-generator`** — not in the repository at all (`git ls-files` returns nothing); it is now the
`dfbuild.keywordsGenerator` convention plugin in `build-logic`. An untracked `build/` directory may still sit in
your checkout.

## Development and Support Modules

### samples
**Description**: Runnable documentation samples; korro executes them and injects code and output into the website
- **Location**: `./samples/`
- **Dependencies**: the root `dataframe` aggregate (`runtimeOnly`, so the compiler plugin sees real jars)
- **Note**: not published. See `samples/AGENTS.md`.

### common-test-utils
**Description**: Shared test helpers and assertions
- **Location**: `./common-test-utils/`
- **Dependencies**: core, Kotest assertions
- **Note**: not published; added as `testImplementation` to every JVM module by the `dfbuild.kotlinJvmCommon`
  convention plugin.

### build-logic, build-settings-logic
**Description**: The build itself — convention plugins (`dfbuild.*`, `dfsettings.*`) instead of inline build logic
- **Location**: `./build-logic/`, `./build-settings-logic/`
- **Note**: included with `includeBuild`, not as regular modules.

## Examples and Documentation

- `examples/projects/` — standalone Gradle/Maven example projects against the latest stable release, with
  `examples/projects/dev/` holding the same set built against master.
- `examples/notebooks/` — Kotlin/Jupyter analytics notebooks.
- `docs/StardustDocs/` — the documentation website (WriterSide), with generated snippets and iframes.

Each has its own `AGENTS.md` (`examples/AGENTS.md`, `docs/StardustDocs/AGENTS.md`) with the authoritative
layout and build instructions.

## Module Dependencies Overview

```
dataframe (root aggregate)
├── core                           — base module, no module dependencies
├── dataframe-arrow                — core
├── dataframe-csv                  — core, dataframe-json
├── dataframe-excel                — core, dataframe-json
├── dataframe-jdbc                 — core
└── dataframe-json                 — core

outside the aggregate:
dataframe-jupyter                  — dataframe (the whole aggregate)
dataframe-geo                      — core
dataframe-geo-jupyter              — dataframe-geo, dataframe-jupyter
dataframe-openapi                  — core
dataframe-openapi-generator        — core, dataframe-openapi
dataframe-compiler-plugin-core     — core (shaded subset)
samples                            — dataframe (runtimeOnly)
common-test-utils                  — core (test-only, applied to every JVM module)
```

## Build Configuration

- **Root project**: `dataframe`; group `org.jetbrains.kotlinx`
- **Build tool**: Gradle with the Kotlin DSL; the daemon JDK comes from `gradle/gradle-daemon-jvm.properties`
- **Target platform**: JVM (the Android example builds against the published artifacts)
- **Documentation**: Dokka for API docs, KoDEx for KDoc preprocessing, korro for the website samples
- **Public API**: guarded by binary-compatibility-validator in every publishable module (`api/*.api` dumps)
