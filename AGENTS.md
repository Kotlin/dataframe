# AGENTS.md

This file provides guidance to coding agents (Claude Code, and other tools that read `AGENTS.md`) when
working with code in this repository.

## What this is

Kotlin DataFrame — a typesafe, immutable, hierarchical in-memory data-processing library for the JVM
(`org.jetbrains.kotlinx:dataframe`). Every `DataFrame` operation returns a new instance, reusing underlying
column storage where possible. Type-safe column access is provided through auto-generated extension properties
(on-the-fly in notebooks, via the compiler plugin / KSP in projects).

## Build & test commands

Gradle (Kotlin DSL), multi-module. Use the wrapper (`./gradlew`, or `gradlew.bat` on Windows). A JDK must be on
`JAVA_HOME`/`PATH` to launch the wrapper; the build daemon JDK is provisioned from `gradle/gradle-daemon-jvm.properties`
(version controlled by `gradle-jdk` in `gradle/libs.versions.toml`).

- `./gradlew build` — full build; runs all tests **and** the ktlint check.
- `./gradlew <module>:test` — test a single module (e.g. `./gradlew core:test`); much faster during development.
- `./gradlew <module>:test --tests "org.jetbrains.kotlinx.dataframe.SomeTest"` — run a single test class/method.
- Add `-Pkotlin.dataframe.debug=true` when validating changes. It enables extra, heavier correctness checks that
  are off in production. PR/CI builds run with this flag (`build -Pkotlin.dataframe.debug=true`), so match it locally.
- `./gradlew publishToMavenLocal -PskipKodex` — publish locally for iteration. `-PskipKodex` skips KDoc
  preprocessing (see below); the result has "broken" KDocs, so it is for local dev only.

Module names for `<module>:` come from `settings.gradle.kts` (e.g. `core`, `dataframe-csv`, `dataframe-jdbc`,
`dataframe-json`, `dataframe-arrow`, `dataframe-excel`, `dataframe-jupyter`).

## Architecture

Dependency graph is rooted at `core`, with I/O and integration split into optional modules (see `MODULES.md`):

- **`core`** — base module, no deps. DataFrame/DataColumn/DataRow types, the operations API (`core/.../api/`),
  aggregations, math, schema handling, codegen, and the `impl/` internals. Also holds deprecated csv/tsv and HTML
  integrations that now live in dedicated modules.
- **I/O modules** (each depends on `core`): `dataframe-csv`, `dataframe-json`, `dataframe-excel`,
  `dataframe-arrow`, `dataframe-jdbc`. The root `dataframe` artifact re-exports Arrow/Excel/JDBC/CSV/JSON as `api`.
- **Integrations**: `dataframe-jupyter`, `dataframe-geo` → `dataframe-geo-jupyter`, `dataframe-openapi` →
  `dataframe-openapi-generator`.
- **Compiler/build tooling**:
  - The **Kotlin DataFrame compiler plugin** — the recommended way to get compile-time type-safe extension
    properties — is **NOT in this repo**. It is developed in the Kotlin repository at
    `github.com/JetBrains/kotlin/tree/master/plugins/kotlin-dataframe`. This repo's `plugins/kotlin-dataframe`
    is a **disabled, out-of-date legacy copy** (its tests are turned off; see issue #1290) — do not treat it as
    the live plugin.
  - `dataframe-compiler-plugin-core` — a shaded subset of `:core` bundled *inside* that compiler plugin (and
    IntelliJ) to run compile-time interpreters of operations; it is not itself a compiler plugin. See its
    `AGENTS.md`.
  - `plugins/*`: `plugins/symbol-processor` (KSP codegen for `@DataSchema`), `plugins/dataframe-gradle-plugin`,
    and support plugins (`expressions-converter`, `public-api-modifier`, `keywords-generator` — the last is a
    separate build with its own Kotlin version).
- **Build logic** lives in `build-logic/` and `build-settings-logic/` as convention plugins
  (e.g. `conventions.plugins.dfbuild.*`), not inline in the module build files.

The public operations API is organized as one file per operation under
`core/src/main/kotlin/org/jetbrains/kotlinx/dataframe/api/` (e.g. `filter.kt`, `groupBy.kt`, `join.kt`). When adding
or changing an operation, find the sibling file for the closest existing operation and follow its structure.

## Generated sources & KDoc preprocessing (KoDEx)

Public KDocs use KoDEx notations (`{@include [X]}`, `@set`/`@get`/`$`, `@sample`, `@ExcludeFromSources`,
`@ExportAsHtml`). See `KODEX_KDOC_PREPROCESSING.md` and `KDOC_GUIDELINES.md`.

- **KoDEx** (an external KDoc-preprocessing tool, applied via the `kodex` convention plugin) expands the templated
  KDocs. `core:processKDocsMain` processes KDocs into `generated-sources`; `changeJarTask` makes `sources.jar` use
  those.
- **Don't hand-edit generated sources, and don't grep/crawl the tree.** Every module built with KoDEx (`core`,
  `dataframe-csv`, `dataframe-excel`, `dataframe-geo`, `dataframe-jdbc`) has a tracked `generated-sources/` copy,
  plus checked-in accessors under `**/src/generated-dataschema-accessors/` and generated **HTML** under
  `docs/StardustDocs/resources/`. In `generated-sources/` the **implementation and public API are the same** as the
  hand-written `src/main/kotlin/…` — KoDEx only expands the KDoc and strips the doc-only fragments (declarations
  marked `@ExcludeFromSources`). So searching the tree (`**/generated-sources/**`,
  `**/generated-dataschema-accessors/**`) just doubles every hit and bloats context — do code work from
  `src/main/kotlin/…`. The one reason to open a generated file is the KDoc: it is the **fully-expanded**
  documentation (all `{@include …}`/`@set`/`@get` resolved, URLs inlined). If you specifically need a symbol's
  final rendered docs, open that single generated file deliberately — don't explore. A CI bot regenerates and
  auto-commits all of these on `master` after merge — you don't run/commit generation yourself.
- For KDocs: never write from scratch — reuse/`@include` an existing operation's KDoc and adapt it. Reusable KDoc
  fragments are written once as `internal`/`private` interfaces and `typealias … = Nothing` declarations and
  composed via `@include`; this pattern is used in every KoDEx module (the shared cross-module fragment library
  lives in `core`'s `documentation/` package).

## Documentation samples

Documentation snippets are runnable tests. The `samples` module and `core`'s `samplesTest`/`korro` tasks execute
sample code, save outputs, and inject them into the WriterSide docs under `docs/StardustDocs/`. Migration of samples
into the `:samples` module is in progress; new documentation samples belong there.

## Code style

- Kotlin official style, enforced by **ktlint** (`ktlint_official`, experimental rules enabled) via `.editorconfig`.
  IntelliJ Ktlint plugin recommended. In IDEA, delegate build actions to Gradle.
- 4-space indent, LF, UTF-8, 120-column max line length (see `.editorconfig` for exact per-glob overrides).
- **No wildcard imports** — star-import thresholds are set to effectively infinite.
- Class/function signatures with ≥4 parameters are forced multiline.
- **`ktlint_standard_chain-method-continuation = disabled`** — deliberate. Group operations on one line when
  possible so a chain reads like a sentence; only start a new line when a *new* operation begins:

  ```kt
  df
      .update { x and y }.where { true }.with { it + 1 }
      .split { z }.by(",").into("z1", "z2")
      .groupBy {
          cols(a, b, c)
      }.aggregate {
          count { d } into "count"
          mean { d } into "mean"
      }
      .print()
  ```

## Constraints

- Base all PRs on `master`; the current checked-out branch may be a feature branch (`git status` at session start).
- Bug fixes require a reproducing test; new public APIs require docs + tests.
- Public API is guarded by **binary-compatibility-validator** in every publishable module — when you change public
  signatures, run the `apiDump`/`apiCheck` Gradle tasks and commit the module's updated `api/*.api` dump.
- DB tests in `dataframe-jdbc` are structured to run against either Dockerized or locally installed DBMS
  (`io/local/` per-database tests, `io/db/` type tests, `io/h2/`). Do not point tests at production databases.
