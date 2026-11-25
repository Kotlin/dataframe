# AGENTS.md — dataframe-arrow

Guidance for this module. See the root `AGENTS.md` for repo-wide build/style/KoDEx rules; only module-specific
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
`--add-opens java.base/java.nio=ALL-UNNAMED` (see `build.gradle.kts`). Downstream users on JDK 16+ need an
equivalent `--add-opens` at runtime. On a modular classpath (e.g. the `examples/projects/spark-parquet-dataframe`
example), open to the Arrow module specifically rather than to everything — exactly as that example does:
`--add-opens=java.base/java.nio=org.apache.arrow.memory.core,ALL-UNNAMED`.

**The IntelliJ JUnit runner does not inherit that flag** (it lives in `tasks.test`), so running a plain JUnit
configuration from the gutter fails in *every* test of this module — not just the Parquet ones — with
`UnsupportedOperationException: sun.misc.Unsafe or java.nio.DirectByteBuffer.<init>(long, int) not available`.
Use the shared **"dataframe-arrow tests"** run configuration (`.idea/runConfigurations/`), which passes the flag,
or delegate test running to Gradle. Switching JDKs does not affect this one.

**Parquet needs a native library that some JDK builds fail to load.** `readParquet` goes through Arrow Dataset,
whose `JniLoader` extracts `arrow_dataset_jni` into the temp dir and `System.load`s it. If that fails you get
`UnsatisfiedLinkError: … A dynamic link library (DLL) initialization routine failed` before any DataFrame code
runs, so *every* `readParquet` test fails while all IPC/Feather tests pass — recognize that signature instead of
hunting for a bug in `readField`. It is not a JDK version boundary and `--add-opens` does not help; observed on
Windows 10 x64 with Corretto 11 and 21, while JBR 21 and Corretto 25 loaded the same file fine.

The test task inherits the daemon JVM, and `gradle/gradle-daemon-jvm.properties` pins only the version
(`Java 21, any vendor`), so switching `JAVA_HOME` is enough — but **stop the daemon first**, or the running one
still matches the criteria and is reused (`./gradlew --stop`, then `JAVA_HOME=<jdk> ./gradlew dataframe-arrow:test`).
`-Dorg.gradle.java.home` did not work. Tests that must run everywhere should read IPC/Feather rather than Parquet —
`ArrowTimestampTzTest` keeps a Feather twin of its Parquet fixture, from the same PyArrow script, for this reason.

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

**Never convert a date-time column with `core`'s plain `convertTo`/`convertToLocalDateTime` in the writer.** They
resolve against the JVM default time zone, so the output would depend on the machine. Arrow and Parquet define a
timestamp as an offset from `1970-01-01T00:00:00Z`, so `ArrowWriterImpl` routes these conversions through its own
`convertToInstantInUtc` / `convertToLocalDateTimeInUtc`, which pin UTC for every source that needs a zone
(local date-times, dates, and numbers read as epoch millis). Guarded by
`ArrowTimestampTzTest.writing does not depend on the default time zone`, which writes one column of each under
four shifted default zones. The `core`-side root cause is tracked as
[#2072](https://github.com/Kotlin/dataframe/issues/2072) — if it is fixed there with a UTC default, these two
local helpers become redundant. The `Date(DAY)` and `Time(unit)` branches are **not** pinned yet: same bug, only
reachable with an explicit target `Schema`.

**Every write failure goes through the `ConvertingMismatch` subscriber.** A value the target field cannot hold is
reported to `mismatchSubscriber` and then either refused as a `ConvertingException` (in a strict `Mode`) or
degraded and written anyway (in a loyal one) — never thrown past the subscriber as a bare exception, which
`Mode.LOYAL` callers cannot catch by the documented type. This holds for out-of-range timestamps
(`ConvertingMismatch.ValueOutOfRange`, gated on `mode.strictType`) as it does for the older type/nullability
mismatches. Note `Mode.STRICT` is the default of `arrowWriter(targetSchema, …)`, and the only mode the no-schema
`writeArrow*`/`saveArrow*ToByteArray` path uses. **A degraded form has to be legal for the target field:** a value
dropped to `null` needs a nullable vector (the field is only widened to nullable when the *source column* has
nulls), so into a non-nullable one an out-of-range value is refused in either mode — writing it would emit a file
that `NullabilityOptions.Checking` then refuses to read back.

One known gap in that contract: a `ColumnGroup`'s columns are converted *inside* `infillVector`, outside the
`try/catch` that gates conversion failures on the mode — so a nested column that cannot be converted throws
`CellConversionException` past the subscriber and aborts the whole write even in `Mode.LOYAL`. Pre-existing and
not timestamp-specific (any child vector does it, e.g. `is IntVector -> column.convertToInt()`); tracked as
[#2075](https://github.com/Kotlin/dataframe/issues/2075).

Three more writer invariants, with a test each:

- `an instant at the bottom of the target unit round-trips` — the `Long.MIN_VALUE` boundary of a timestamp unit is
  representable and must not be rejected as out of range.
- `a write that fails leaves no leaked buffers behind` (plus `… with an Error …`) — a vector is allocated before
  it is filled, so any throw in between must close it, and every vector allocated for an earlier field must be
  closed too, or `RootAllocator.close()` reports a leak and masks the real error. Both cleanups catch `Throwable`,
  not `Exception`: an unsupported target field arrives as `NotImplementedError`.
- `a frame column has no list mapping on write` — the writer implements no list vectors, so a `FrameColumn` is
  *not* writable: a top-level one degrades to `Utf8`, a nested one fails on allocation.

The Arrow ↔ Kotlin type mapping is published as a table in `docs/StardustDocs/topics/dataSources/ApacheArrow.md`
and pinned by `ArrowTypeMappingTest`; change the code, the test and the table in the same commit. A new write
row lands in both the `writeCases` list **and** the `roundTripped` agreement map of
`the documented mappings round-trip` — nothing enforces the second, and a row missing from it is pinned
one-sided. The read and
write tables are **not** symmetric — reading builds lists and frame columns, writing has no list mapping at all.

**Legacy/deprecated:** `arrowReading.kt` also defines `ArrowFeather`, an implementation of the old
`SupportedDataFrameFormat` SPI registered in
`src/main/resources/META-INF/services/org.jetbrains.kotlinx.dataframe.io.SupportedFormat`. That SPI is deprecated
(`ERROR` level, removed in 1.0) — don't build on it; the `read*`/`write*` extension functions are the real API.

## Tests

- Tests live under `src/test/kotlin/.../io/`; fixtures in `src/test/resources/`. `duckdb-jdbc` and `arrow-c-data` are
  test-only deps (DuckDB is used to produce Parquet/Arrow test data).
- Run just this module: `./gradlew dataframe-arrow:test` (add `-Pkotlin.dataframe.debug=true` to match CI checks).
- **Never walk a whole batch-sized vector per row when reading.** `readField` runs once per Arrow batch (Parquet
  default batch: 32K rows) for a top-level column, but once per list row for any vector nested inside a list
  element, and a full-vector walk — e.g. `StructVector.getNullCount()`, which scans the entire validity buffer
  via `BitVectorHelper.getNullCount` — must not happen per list row: batch-sized work × rows turns a linear
  read into a quadratic one. `ArrowListStructScanTest` guards this deterministically: a hand-built
  `VectorSchemaRoot` fed through a custom `ArrowReader` (the same `readArrowImpl` path all readers take) with
  `getNullCount`-counting vectors pins "at most one full validity-buffer scan per batch" — for the list element
  and for a struct or list nested inside it — no timing involved.
- To *measure* reading performance, use `ArrowReadingPerfBenchmarkTest`: a manual `@Ignore`d benchmark over
  in-memory Arrow IPC datasets (the shared conversion, so no Arrow-dataset JNI is needed). Label each run with the
  `ARROW_PERF_VARIANT` env var; per-dataset medians **and fingerprints** (row/sum/null counts) are appended to
  `arrow-reading-perf.txt` in the system temp dir (`java.io.tmpdir`) — identical fingerprints across variants mean a
  pure perf change, differing ones mean the reads are not comparable. Timing is machine-dependent, so it stays out
  of CI.
- When a test hand-builds Arrow vectors instead of `VectorSchemaRoot.create`, construct struct vectors via the
  `(name, FieldType)` constructor + `initializeChildrenFromFields`: the `(Field, …)` constructor eagerly allocates
  writer-owned child buffers that `close()` never releases, and the root allocator reports the leak at the end of
  the test. A vector injected anywhere is closed by nobody but the test itself: as a list's data vector (a
  subclass overriding `getDataVector`) or as a struct element's child (a subclass overriding the one-arg
  `getChild(String)`, declared on `AbstractContainerVector` — two supertypes above `StructVector`).
  `ArrowListStructScanTest`'s KDoc shows the working shapes.
