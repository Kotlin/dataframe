# CLAUDE.md — dataframe-arrow

Guidance for this module. See the root `CLAUDE.md` for repo-wide build/style/KoDEx rules; only module-specific
details are here.

## What this module is

`dataframe-arrow` (artifact `dataframe-arrow`, "Apache Arrow support for Kotlin DataFrame"). Reads and writes the two
Apache Arrow IPC formats and **reads Parquet**:

- **Arrow IPC / streaming format** — `readArrowIPC` / `writeArrowIPC`.
- **Arrow random-access / Feather format** — `readArrowFeather` / `writeArrowFeather`.
- **Parquet (read-only)** — `readParquet`, implemented on top of **Arrow Dataset** (`FileFormat.PARQUET`). There is no
  Parquet *writer* here.

Depends only on `core`. Uses the Apache Arrow Java libraries (`arrow-vector`, `arrow-format`, `arrow-memory`, and
`arrow-dataset` — the last is what enables Parquet) plus `commons-compress` for in-memory seekable channels.

**JVM flag:** Arrow uses off-heap memory via `java.nio`, so the test task passes
`--add-opens java.base/java.nio=ALL-UNNAMED` (see `build.gradle.kts`). Downstream users on JDK 16+ need the same
`--add-opens` at runtime.

## Public API surface (`src/main/kotlin/.../io/`)

- `arrowReading.kt` — reading entry points, all `DataFrame.Companion` extensions:
  - `readArrowIPC(...)` and `readArrowFeather(...)`, each overloaded for `File` / `Path` / `String` / `URL` /
    `InputStream` / `ByteArray` / channel.
  - `readArrow(reader: ArrowReader)` and `ArrowReader.toDataFrame()` — read any Arrow stream from an existing reader.
  - `readParquet(vararg ...)` for `URL` / `String` / `Path` / `File`, with `batchSize`
    (default `ARROW_PARQUET_DEFAULT_BATCH_SIZE = 32768`).
  - All reads take a `NullabilityOptions` (default `Infer`) controlling how column nullability is decided.
- `arrowWriting.kt` — `AnyFrame` write extensions: `writeArrowIPC` / `writeArrowFeather` (to channel / stream /
  `File` / `Path`), `saveArrowIPCToByteArray` / `saveArrowFeatherToByteArray`, and `AnyFrame.arrowWriter(...)`.
- `ArrowWriter.kt` — the `ArrowWriter` interface (`AutoCloseable`) that writing delegates to. Key concepts:
  - **`Mode`** — `restrictWidening` / `restrictNarrowing` / `strictType` / `strictNullable`, with presets
    `Mode.STRICT` and `Mode.LOYAL`. Controls how the frame is coerced to a target Arrow `Schema`.
  - **`ConvertingMismatch`** subscriber — `ignoreMismatchMessage` / `writeMismatchMessage` / `logMismatchMessage`
    handle schema/type mismatches during writing.

Implementation is in `arrowReadingImpl.kt`, `ArrowWriterImpl.kt`, and `arrowTypesMatching.kt` (Arrow ↔ Kotlin type
mapping); `ConvertingMismatch.kt` defines the mismatch model. Follow the `api → impl` split when editing.

**Legacy/deprecated:** `arrowReading.kt` also defines `ArrowFeather`, an implementation of the old
`SupportedDataFrameFormat` SPI registered in
`src/main/resources/META-INF/services/org.jetbrains.kotlinx.dataframe.io.SupportedFormat`. That SPI is deprecated
(`ERROR` level, removed in 1.0) — don't build on it; the `read*`/`write*` extension functions are the real API.

## Public-API stability

`binary-compatibility-validator` is enabled — run `apiDump`/`apiCheck` when public signatures change and commit the
`.api` dump.

## Tests

- Tests live under `src/test/kotlin/.../io/`; fixtures in `src/test/resources/`. `duckdb-jdbc` and `arrow-c-data` are
  test-only deps (DuckDB is used to produce Parquet/Arrow test data).
- Run just this module: `./gradlew dataframe-arrow:test` (add `-Pkotlin.dataframe.debug=true` to match CI checks).
