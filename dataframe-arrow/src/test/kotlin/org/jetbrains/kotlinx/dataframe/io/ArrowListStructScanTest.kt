package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.ints.beLessThanOrEqualTo
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.apache.arrow.memory.BufferAllocator
import org.apache.arrow.memory.RootAllocator
import org.apache.arrow.vector.FieldVector
import org.apache.arrow.vector.IntVector
import org.apache.arrow.vector.VectorSchemaRoot
import org.apache.arrow.vector.complex.ListVector
import org.apache.arrow.vector.complex.StructVector
import org.apache.arrow.vector.ipc.ArrowReader
import org.apache.arrow.vector.types.pojo.ArrowType
import org.apache.arrow.vector.types.pojo.Field
import org.apache.arrow.vector.types.pojo.FieldType
import org.apache.arrow.vector.types.pojo.Schema
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.junit.Test

/**
 * Reading a list-of-struct column scans the struct element vector's validity buffer **once per batch**, not
 * once per list row. `StructVector.getNullCount()` (via `BitVectorHelper.getNullCount`) walks the whole
 * validity buffer, so a per-row call — the regression of
 * [#2064](https://github.com/Kotlin/dataframe/issues/2064) — makes reading a `List<Struct>` column cost
 * `rows × vectorSize`, quadratic in the batch size.
 *
 * The scan count is observed by feeding `toDataFrame` a hand-built [VectorSchemaRoot] through a custom
 * [ArrowReader] — the same `readArrowImpl` path that `readArrowIPC` / `readArrowFeather` / `readParquet`
 * take — with the element vector replaced by a subclass counting [StructVector.getNullCount] invocations.
 * Every other reading behaviour is unchanged, so the null-element correctness of #2041's fix stays guarded
 * by `ArrowNullableStructTest`.
 */
internal class ArrowListStructScanTest {

    private companion object {
        private const val ROWS = 100
        private const val ELEMENTS_PER_ROW = 2
        private val intNN = FieldType.notNullable(ArrowType.Int(32, true))
    }

    /**
     * A [StructVector] counting [getNullCount] calls — each is one full validity-buffer scan. Built via the
     * `(name, FieldType)` constructor + `initializeChildrenFromFields`, exactly as `VectorSchemaRoot.create`
     * does: the `(Field, …)` constructor eagerly allocates writer-owned child buffers that a `close()` never
     * releases, which the root allocator reports as a leak.
     */
    private class CountingStructVector(name: String, fieldType: FieldType, allocator: BufferAllocator) :
        StructVector(name, allocator, fieldType, null) {

        var nullCountScans = 0
            private set

        override fun getNullCount(): Int {
            nullCountScans++
            return super.getNullCount()
        }
    }

    /** A [ListVector] reporting an injected element vector — the one the reader must work against. */
    private class InjectingListVector(field: Field, allocator: BufferAllocator, injected: FieldVector) :
        ListVector(field, allocator, null) {

        private val injectedDataVector = injected

        override fun getDataVector(): FieldVector = injectedDataVector
    }

    /** Serves exactly one batch from a pre-built [VectorSchemaRoot]; nothing else is read. */
    private class SingleBatchReader(private val root: VectorSchemaRoot, allocator: BufferAllocator) :
        ArrowReader(allocator) {

        private var served = false

        override fun getVectorSchemaRoot(): VectorSchemaRoot = root

        override fun loadNextBatch(): Boolean {
            if (served) return false
            served = true
            return true
        }

        override fun readSchema(): Schema = root.schema

        override fun bytesRead(): Long = 0L

        override fun closeReadSource() {}
    }

    /**
     * Builds `orders: list<struct{ x: Int, y: Int }>` — [ROWS] rows × [ELEMENTS_PER_ROW] elements, all
     * present, element `x` = flat element index, `y` = 3× index — and returns the root with the counting
     * element vector. Populating vectors directly is the module's established idiom (`arrowTestUtils.kt`).
     */
    private fun listStructRoot(
        allocator: BufferAllocator,
        elementNullable: Boolean,
    ): Pair<VectorSchemaRoot, CountingStructVector> {
        val elementChildren = listOf(Field("x", intNN, null), Field("y", intNN, null))
        val elementFieldType =
            if (elementNullable) FieldType.nullable(ArrowType.Struct()) else FieldType.notNullable(ArrowType.Struct())
        val elementField = Field("element", elementFieldType, elementChildren)
        val ordersField = Field("orders", FieldType.nullable(ArrowType.List()), listOf(elementField))

        val struct = CountingStructVector("element", elementFieldType, allocator)
        struct.initializeChildrenFromFields(elementChildren)
        struct.allocateNew()
        val x = struct.getChild("x") as IntVector
        val y = struct.getChild("y") as IntVector
        for (el in 0 until ROWS * ELEMENTS_PER_ROW) {
            struct.setIndexDefined(el)
            x.setSafe(el, el)
            y.setSafe(el, el * 3)
        }
        struct.setValueCount(ROWS * ELEMENTS_PER_ROW)

        val list = InjectingListVector(ordersField, allocator, struct)
        list.initializeChildrenFromFields(listOf(elementField))
        list.allocateNew()
        for (i in 0 until ROWS) {
            list.startNewValue(i)
            list.endValue(i, ELEMENTS_PER_ROW)
        }
        val root = VectorSchemaRoot(listOf(ordersField), listOf(list))
        root.setRowCount(ROWS)
        return root to struct
    }

    private fun assertOrdersData(df: AnyFrame) {
        val orders = df["orders"] as FrameColumn<*>
        orders.size() shouldBe ROWS
        val row0 = orders[0]
        row0.rowsCount() shouldBe ELEMENTS_PER_ROW
        row0[0]["x"] shouldBe 0
        row0[0]["y"] shouldBe 0
        row0[1]["x"] shouldBe 1
        row0[1]["y"] shouldBe 3
        // the last row guards the index math: its elements are the flat indices 198 and 199
        val lastRow = orders[ROWS - 1]
        lastRow[0]["x"] shouldBe 198
        lastRow[0]["y"] shouldBe 594
        lastRow[1]["x"] shouldBe 199
        lastRow[1]["y"] shouldBe 597
    }

    private fun readWithScanCount(allocator: BufferAllocator, elementNullable: Boolean) {
        val (root, struct) = listStructRoot(allocator, elementNullable)
        root.use {
            // the list closes its own internal element vector, not the injected counting one
            struct.use {
                val df = SingleBatchReader(root, allocator).toDataFrame()
                struct.nullCountScans should beLessThanOrEqualTo(1)
                assertOrdersData(df)
            }
        }
    }

    @Test
    fun `reading a list of required structs scans the element vector at most once per batch`() {
        RootAllocator().use { allocator ->
            readWithScanCount(allocator, elementNullable = false)
        }
    }

    @Test
    fun `reading a list of nullable structs scans the element vector at most once per batch`() {
        RootAllocator().use { allocator ->
            readWithScanCount(allocator, elementNullable = true)
        }
    }
}
