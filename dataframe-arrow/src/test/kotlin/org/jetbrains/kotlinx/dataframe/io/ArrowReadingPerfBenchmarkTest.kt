package org.jetbrains.kotlinx.dataframe.io

import org.apache.arrow.memory.RootAllocator
import org.apache.arrow.vector.IntVector
import org.apache.arrow.vector.ValueVector
import org.apache.arrow.vector.VectorSchemaRoot
import org.apache.arrow.vector.complex.ListVector
import org.apache.arrow.vector.complex.StructVector
import org.apache.arrow.vector.types.pojo.ArrowType
import org.apache.arrow.vector.types.pojo.Field
import org.apache.arrow.vector.types.pojo.FieldType
import org.apache.arrow.vector.types.pojo.Schema
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.junit.Ignore
import org.junit.Test
import java.io.File

/**
 * Manual benchmark for the Arrow reading pipeline. It reads single-batch in-memory Arrow IPC datasets
 * through [DataFrame.readArrowIPC] — the same shared conversion that `readArrowFeather` and `readParquet`
 * run — so no Arrow-dataset JNI is needed. Kept from the investigation of
 * [#2064](https://github.com/Kotlin/dataframe/issues/2064), where a per-list-row validity-buffer scan made
 * reading a `List<Struct>` column quadratic in the batch size.
 *
 * **Why manual:** wall-clock assertions are flaky on CI. To compare two implementations, temporarily remove
 * [Ignore], set the `ARROW_PERF_VARIANT` env var to a label for each run, and run
 * `gradlew dataframe-arrow:test --tests "org.jetbrains.kotlinx.dataframe.io.ArrowReadingPerfBenchmarkTest"`.
 * Each run appends per-dataset medians (7 measured reads after 3 warmups) and a `probe` line with the raw
 * `getNullCount` scan cost to `arrow-reading-perf.txt` in the system temp dir (`java.io.tmpdir`); diff the
 * file across runs.
 *
 * **The fingerprints make the comparison honest:** every dataset also prints a fingerprint (row counts,
 * value sums, null-cell counts). Identical fingerprints across variants mean a variant does not change
 * reading behaviour, so its timing difference is a pure perf change; a differing fingerprint means the
 * variants read different data and the timings are not comparable.
 *
 * The structural regression guard itself is deterministic and lives in CI: `ArrowListStructScanTest` pins
 * "at most one full validity-buffer scan per batch" by counting `getNullCount` invocations. This class only
 * supplies the numbers.
 */
@Ignore("manual benchmark — timing is machine-dependent; see the KDoc for how to run it")
internal class ArrowReadingPerfBenchmarkTest {

    private fun topFingerprint(df: AnyFrame): String {
        val g = df["s"] as ColumnGroup<*>
        val x = g["x"].values().toList()
        val xNulls = x.count { it == null }
        val xSum = x.sumOf { (it as Int?) ?: 0 }
        return "rows=${df.rowsCount()} xNulls=$xNulls xSum=$xSum xType=${g["x"].type()}"
    }

    private fun listFingerprint(df: AnyFrame): String {
        val fc = df["orders"] as FrameColumn<*>
        var qSum = 0L
        var qNulls = 0
        var els = 0
        var emptyFrames = 0
        for (i in 0 until fc.size()) {
            val frame = fc[i]
            if (frame.rowsCount() == 0) emptyFrames++
            val q = frame["q"].values()
            for (v in q) {
                els++
                if (v == null) qNulls++ else qSum += v as Int
            }
        }
        return "rows=${df.rowsCount()} els=$els qNulls=$qNulls qSum=$qSum emptyFrames=$emptyFrames"
    }

    private fun bench(label: String, bytes: ByteArray, fingerprint: (AnyFrame) -> String) {
        var fp = ""
        repeat(3) { fp = fingerprint(DataFrame.readArrowIPC(bytes)) } // warmup
        val times = LongArray(7) {
            val t = System.nanoTime()
            val df = DataFrame.readArrowIPC(bytes)
            (System.nanoTime() - t).also { fp = fingerprint(df) }
        }
        val sorted = times.clone().also { it.sort() }
        log(
            "bench $label: median=${sorted[3] / 1_000_000.0}ms min=${sorted[0] / 1_000_000.0}ms all=${times.map {
                it / 1_000_000.0
            }} fp=[$fp]",
        )
    }

    @Test
    fun `bench top-level required struct 1M rows`() {
        bench("topRequired", topRequired, ::topFingerprint)
    }

    @Test
    fun `bench top-level nullable struct 1M rows 1pct nulls`() {
        bench("topNullable", topNullable, ::topFingerprint)
    }

    @Test
    fun `bench list of required struct 20K x 10`() {
        bench("listRequiredElement", listRequiredElement, ::listFingerprint)
    }

    @Test
    fun `bench list of nullable struct no nulls 20K x 10`() {
        bench("listNullableNoNulls", listNullableNoNulls, ::listFingerprint)
    }

    @Test
    fun `bench list of nullable struct with nulls 20K x 10`() {
        bench("listNullableWithNulls", listNullableWithNulls, ::listFingerprint)
    }

    private companion object {
        private const val TOP_ROWS = 1_000_000
        private const val LIST_ROWS = 20_000
        private const val LIST_LEN = 10

        private val results = File(System.getProperty("java.io.tmpdir"), "arrow-reading-perf.txt")

        private fun log(line: String) {
            val variant = System.getenv("ARROW_PERF_VARIANT") ?: "UNSET"
            results.appendText("[$variant] $line${System.lineSeparator()}")
            println("[$variant] $line")
        }

        private val intNN: FieldType = FieldType.notNullable(ArrowType.Int(32, true))

        private fun structField(name: String, nullable: Boolean, children: List<Field>): Field =
            Field(
                name,
                if (nullable) FieldType.nullable(ArrowType.Struct()) else FieldType.notNullable(ArrowType.Struct()),
                children,
            )

        private fun listField(name: String, element: Field): Field =
            Field(name, FieldType.nullable(ArrowType.List()), listOf(element))

        /** Times `v.nullCount` (a full validity-buffer scan for [StructVector]) — the primitive #2064 blamed. */
        private fun probeScan(label: String, v: ValueVector) {
            val sv = v as? StructVector ?: return
            repeat(10) { sv.nullCount }
            val t = System.nanoTime()
            repeat(100) { sv.nullCount }
            val scan = (System.nanoTime() - t) / 100
            log(
                "probe $label: class=${sv.javaClass.simpleName} fieldIsNullable=${sv.field.isNullable} " +
                    "nullCount=${sv.nullCount} scan=${scan}ns/call valueCount=${sv.valueCount}",
            )
        }

        private fun buildTop(nullable: Boolean, nullEvery: Int): ByteArray =
            RootAllocator().use { allocator ->
                VectorSchemaRoot.create(
                    Schema(
                        listOf(structField("s", nullable, listOf(Field("x", intNN, null), Field("y", intNN, null)))),
                    ),
                    allocator,
                ).use { root ->
                    val s = root.getVector("s") as StructVector
                    s.allocateNew()
                    val x = s.getChild("x") as IntVector
                    val y = s.getChild("y") as IntVector
                    for (i in 0 until TOP_ROWS) {
                        if (nullable && i % nullEvery == 0) {
                            s.setNull(i) // hidden non-zero child values under the null parent
                            x.setSafe(i, -i)
                            y.setSafe(i, -i)
                        } else {
                            s.setIndexDefined(i)
                            x.setSafe(i, i)
                            y.setSafe(i, i * 2)
                        }
                    }
                    root.setRowCount(TOP_ROWS)
                    probeScan("top(nullable=$nullable)", s)
                    root.toArrowStreamBytes()
                }
            }

        private fun buildList(nullableElement: Boolean, nullEvery: Int): ByteArray =
            RootAllocator().use { allocator ->
                VectorSchemaRoot.create(
                    Schema(
                        listOf(
                            listField(
                                "orders",
                                structField(
                                    "element",
                                    nullableElement,
                                    listOf(Field("q", intNN, null), Field("r", intNN, null)),
                                ),
                            ),
                        ),
                    ),
                    allocator,
                ).use { root ->
                    val list = root.getVector("orders") as ListVector
                    list.allocateNew()
                    val st = list.dataVector as StructVector
                    st.allocateNew()
                    val q = st.getChild("q") as IntVector
                    val r = st.getChild("r") as IntVector
                    var el = 0
                    for (i in 0 until LIST_ROWS) {
                        list.startNewValue(i)
                        repeat(LIST_LEN) {
                            if (nullEvery > 0 && el % nullEvery == 0) {
                                st.setNull(el) // hidden non-zero child values under the null element
                                q.setSafe(el, -el)
                                r.setSafe(el, -el)
                            } else {
                                st.setIndexDefined(el)
                                q.setSafe(el, el)
                                r.setSafe(el, el * 3)
                            }
                            el++
                        }
                        list.endValue(i, LIST_LEN)
                    }
                    st.setValueCount(el)
                    root.setRowCount(LIST_ROWS)
                    probeScan("list(nullableElement=$nullableElement, nullEvery=$nullEvery)", st)
                    root.toArrowStreamBytes()
                }
            }

        private val topRequired by lazy { buildTop(nullable = false, nullEvery = 0) }
        private val topNullable by lazy { buildTop(nullable = true, nullEvery = 100) }
        private val listRequiredElement by lazy { buildList(nullableElement = false, nullEvery = 0) }
        private val listNullableNoNulls by lazy { buildList(nullableElement = true, nullEvery = 0) }
        private val listNullableWithNulls by lazy { buildList(nullableElement = true, nullEvery = 10) }
    }
}
