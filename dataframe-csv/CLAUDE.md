# CLAUDE.md — dataframe-csv

Guidance for this module. See the root `CLAUDE.md` for repo-wide build/style/KoDEx rules; only module-specific
details are here.

## What this module is

`dataframe-csv` (artifact `dataframe-csv`, "CSV support for Kotlin DataFrame"). Reads and writes CSV / TSV / arbitrary
delimited text to and from `DataFrame`, with type inference for cell values. This is the modern, enhanced delimited-text
integration; the older CSV/TSV code left in `core` is deprecated in favor of this module.

Depends on `core` and, as `api`, on `dataframe-json` — JSON is used to read/write nested `DataFrame`/`DataRow` values
inside CSV cells. That JSON dependency can be safely excluded downstream if you only handle flat frames.

Third-party libraries: **Deephaven CSV** (`libs.deephavenCsv`) for fast *reading*, **Apache Commons CSV**
(`libs.commonsCsv`) for *writing*. `fastDoubleParser` handles numeric parsing.

## Public API surface (`src/main/kotlin/.../io/`)

One file per operation family, each a set of `DataFrame.Companion` / `AnyFrame` extension functions:

- Reading: `readCsv.kt`, `readTsv.kt`, `readDelim.kt` (from `File`/`Path`/`URL`/`InputStream`), and the string variants
  `readCsvStr.kt`, `readTsvStr.kt`, `readDelimStr.kt`.
- Writing: `writeCsv.kt`, `writeTsv.kt`, `writeDelim.kt`, plus in-memory `toCsvStr.kt`, `toTsvStr.kt`, `toDelimStr.kt`.
- `QuoteMode.kt` and the `documentationCsv/DelimParams.kt` object hold the shared parameter set and defaults used across
  all of the above (delimiter, header, quoting, compression, etc.). CSV = comma, TSV = tab; both are thin wrappers over
  the generic delim functions.

All the public functions delegate to `impl/io/readDelim.kt` and `impl/io/writeDelim.kt`; `ListSink.kt` and
`DataFrameCustomDoubleParser.kt` are internal helpers. Reading transparently handles gzip and zip input (see the
`.gz`/`.zip` fixtures in test resources).

`documentationCsv/CommonReadDelimDocs.kt` / `CommonWriteDelimDocs.kt` are KoDEx documentation-only sources: the shared
KDoc for every read/write function is written once there and `@include`d into the individual operation files. Edit the
docs there, not in each function.

**Legacy/deprecated:** `io/csv.kt` and `io/tsv.kt` define `CsvDeephaven` / `TsvDeephaven`, implementations of the old
`SupportedDataFrameFormat` SPI registered in
`src/main/resources/META-INF/services/org.jetbrains.kotlinx.dataframe.io.SupportedFormat`. That SPI is deprecated
(`ERROR` level, removed in 1.0) — don't build on it; the `read*`/`write*` extension functions above are the real API.

## Public-API stability

`binary-compatibility-validator` is enabled — run the `apiDump`/`apiCheck` Gradle tasks when public signatures change,
and commit the updated `.api` dump.

## Tests & benchmarks

- `src/test/kotlin/.../io/DelimCsvTsvTests.kt` is the main test; fixtures (various encodings, BOMs, gzip, zip, locales)
  live in `src/test/resources/`.
- `kotlinx.benchmark` is configured (`benchmark { targets { register("test") } }`); `BenchmarkTest.kt` holds read
  benchmarks.
- Run just this module: `./gradlew dataframe-csv:test` (add `-Pkotlin.dataframe.debug=true` to match CI checks).
