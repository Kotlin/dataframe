# CLAUDE.md — dataframe-json

Guidance for this module. See the root `CLAUDE.md` for repo-wide build/style/KoDEx rules; only module-specific
details are here.

## What this module is

`dataframe-json` (artifact `dataframe-json`, "Kotlin DataFrame JSON integration"). Reads JSON into a `DataFrame`/
`DataRow` with schema/type inference, and serializes frames back to JSON. Built on **kotlinx.serialization**
(`serialization-core`, `serialization-json` are exposed as `api`). Depends only on `core`.

This module is a dependency of several others (`dataframe-csv`, and it's a `testImplementation` of `core`) because
JSON is how nested `DataFrame`/`DataRow` values are encoded inside other formats and how frames are rendered.

**Compiles as a friend of `core`:** `build.gradle.kts` sets `friendPaths` to the `core` project, so this module can
use `core`'s `internal` declarations. Keep that in mind — code here may call internals not visible to normal consumers.

## Public API surface (`src/main/kotlin/.../io/json.kt`)

All are `DataFrame.Companion` / `DataRow.Companion` / `AnyFrame` / `AnyRow` extensions:

- Reading: `readJson` (from `File`/`Path`/`String` path/`URL`/`InputStream`) and `readJsonStr` (from a JSON string),
  each available for both `DataFrame` and `DataRow`.
- Writing: `toJson` / `writeJson` (to `File`/`Path`/`String`/`Appendable`) for frames and rows.
- `toJsonWithMetadata(...)` — emits JSON plus type metadata; used specifically by the Kotlin Notebook IntelliJ plugin
  for rich rendering. Not a general-purpose serializer.

Key reading behavior to understand before touching inference:

- **`TypeClashTactic`** (in `json.kt`) controls what happens when the same JSON path holds different shapes across
  records. `ARRAY_AND_VALUE_COLUMNS` (default) builds a `ColumnGroup` with `value`/`array`/unwrapped-object columns;
  `ANY_COLUMNS` produces a single `Any` column. The KDoc on the enum has worked examples — read it first.
- **`keyValuePaths: List<JsonPath>`** — at these paths a JSON object is read as a `FrameColumn<NameValueProperty>`
  (key/value rows) instead of a `ColumnGroup`. Use for maps with dynamic/unbounded keys.
- **`unifyNumbers`** — promotes mixed numeric types in a column to a common widest type (`UnifyingNumbers`).

Extensibility points: `CustomEncoder` (plug in serialization for otherwise-unsupported value types during write) and
`Base64ImageEncodingOptions` (image → Base64, with gzip / size-limit flags), used by the notebook rendering path.

Implementation lives in `impl/io/readJson.kt` (`readJsonImpl`) and `impl/io/writeJson.kt` (`encodeFrame`,
`encodeRow`, `encodeDataFrameWithMetadata`); `BytesUtils.kt` and `compression.kt` are internal helpers.
`JsonFacadeForDebugger.java` is a small Java shim used by the debugger integration.

## Public-API stability

`binary-compatibility-validator` is enabled. When you change public signatures, run `apiDump`/`apiCheck` and commit the
updated `.api` dump. Note the `@Deprecated(level = HIDDEN)` `readJson(stream, ...)` overloads exist purely for binary
compatibility — leave them in place.

## Tests

- JUnit 5 (`useJUnitPlatform()`), kotest assertions. Main tests: `JsonTests.kt`, `ImageSerializationTests.kt`.
- Fixtures in `src/test/resources/` (`repositories.json`, `imgs/`).
- Run just this module: `./gradlew dataframe-json:test` (add `-Pkotlin.dataframe.debug=true` to match CI checks).
