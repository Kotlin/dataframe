# AGENTS.md — dataframe-excel

Guidance for this module. See the root `AGENTS.md` for repo-wide build/style/KoDEx rules; only module-specific
details are here.

## What this module is

`dataframe-excel` (artifact `dataframe-excel`, "Excel support for Kotlin DataFrame"). Reads and writes `.xls` and
`.xlsx` spreadsheets to/from `DataFrame`, built on **Apache POI** (`poi` exposed as `api`, `poi-ooxml` as
`implementation`).

Depends on `core` and, as `api`, on `dataframe-json` — JSON is used to write nested `DataFrame`/`DataRow` values into
Excel cells. That JSON dependency can be excluded downstream if you only write flat frames.

## Public API surface (`src/main/kotlin/.../io/xlsx.kt`)

The whole public API is one file, `xlsx.kt`:

- **`DataFrame.Companion.readExcel(...)`** — many overloads over the input source (`String` path/URL, `File`, `URL`,
  `InputStream`, an already-open POI `Workbook`, or a single `Sheet`). Common parameters: `sheetName`, `skipRows`,
  `columns` (Excel-style column range like `"A:C"`), `stringColumns`, `rowsCount`, `nameRepairStrategy`,
  `firstRowIsHeader`, `formattingOptions`, `parseEmptyAsNull`.
- **`FormattingOptions`** / **`StringColumns.toFormattingOptions(...)`** — force specific columns to be read as
  strings using a POI `DataFormatter` (useful for zip codes, IDs, etc. that shouldn't be parsed as numbers).
- **`DataFrame<T>.writeExcel(...)`** — writes to `String` path / `File`; parameters include `columnsSelector`,
  `sheetName`, `writeHeader`, `workBookType` (`WorkBookType.XLS` / `WorkBookType.XLSX`, default XLSX), and `keepFile`
  (append a new sheet to an existing workbook instead of overwriting).

**Legacy/deprecated:** `xlsx.kt` also defines `Excel`, an implementation of the old `SupportedDataFrameFormat` SPI
registered in `src/main/resources/META-INF/services/org.jetbrains.kotlinx.dataframe.io.SupportedFormat`. That SPI is
deprecated (`ERROR` level, removed in 1.0) — don't build on it; `readExcel`/`writeExcel` are the real API.

## Tests

- Tests under `src/test/kotlin/.../io/`; spreadsheet fixtures in `src/test/resources/`.
- Run just this module: `./gradlew dataframe-excel:test` (add `-Pkotlin.dataframe.debug=true` to match CI checks).
