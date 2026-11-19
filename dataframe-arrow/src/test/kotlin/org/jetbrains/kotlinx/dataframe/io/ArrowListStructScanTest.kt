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
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.junit.Test

/**
 * Reading a list-of-struct column scans the struct element vector's validity buffer **once per batch**, not
 * once per list row. `StructVector.getNullCount()` (via `BitVectorHelper.getNullCount`) walks the whole
 * validity buffer, so a per-row call — the regression of
 * [#2064](https://github.com/Kotlin/dataframe/issues/2064) — makes reading a `List<Struct>` column cost
 * `rows × vectorSize`, quadratic in the batch size.
 *
 * The same invariant holds one nesting level deeper and is pinned here too: a struct or a list nested
 * *inside* the list element re-enters the same reading code once per outer row — `readField`'s struct
 * branch for a struct child, whose null-count check is a whole-vector scan, and `readListVector` for a
 * list child, whose mask must not be built over the whole inner element vector — so a whole-vector pass
 * there is the same quadratic pitfall.
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
        private const val INNER_ELEMENTS = 2
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

    /** An element struct whose every child lookup returns the injected vector — the one the reader must work against. */
    private class InjectingElementStruct(allocator: BufferAllocator, private val injected: FieldVector) :
        StructVector("element", allocator, FieldType.notNullable(ArrowType.Struct()), null) {

        override fun getChild(name: String?): FieldVector = injected
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

    /**
     * Builds `orders: list<struct{ s: struct{ x: Int } }>` — [ROWS] rows × [ELEMENTS_PER_ROW] elements, the
     * inner struct always present, `x` = flat element index — with the **inner** `s` vector counting
     * [StructVector.getNullCount] invocations, and runs [block] with everything open. One nesting level
     * deeper than [listStructRoot]: `s` is read through `readField`'s struct branch once per list row, so
     * a whole-vector null-count there is the same quadratic pitfall.
     *
     * The injected vectors (`element`, `s`) are closed by this function, in reverse order — the list closes
     * only its own internal element vector, not the injected one.
     */
    private fun <T> withStructInListElement(
        allocator: BufferAllocator,
        block: (VectorSchemaRoot, CountingStructVector) -> T,
    ): T {
        val xField = Field("x", intNN, null)
        val sField = Field("s", FieldType.nullable(ArrowType.Struct()), listOf(xField))
        val elementField = Field("element", FieldType.notNullable(ArrowType.Struct()), listOf(sField))
        val ordersField = Field("orders", FieldType.nullable(ArrowType.List()), listOf(elementField))

        val s = CountingStructVector("s", FieldType.nullable(ArrowType.Struct()), allocator)
        s.initializeChildrenFromFields(listOf(xField))
        s.allocateNew()
        val x = s.getChild("x") as IntVector
        for (el in 0 until ROWS * ELEMENTS_PER_ROW) {
            s.setIndexDefined(el)
            x.setSafe(el, el)
        }
        x.setValueCount(ROWS * ELEMENTS_PER_ROW)
        s.setValueCount(ROWS * ELEMENTS_PER_ROW)

        val element = InjectingElementStruct(allocator, s)
        element.allocateNew()
        for (el in 0 until ROWS * ELEMENTS_PER_ROW) element.setIndexDefined(el)
        element.setValueCount(ROWS * ELEMENTS_PER_ROW)

        val list = InjectingListVector(ordersField, allocator, element)
        list.initializeChildrenFromFields(listOf(elementField))
        list.allocateNew()
        for (i in 0 until ROWS) {
            list.startNewValue(i)
            list.endValue(i, ELEMENTS_PER_ROW)
        }
        val root = VectorSchemaRoot(listOf(ordersField), listOf(list))
        root.setRowCount(ROWS)

        return root.use {
            element.use {
                s.use { block(root, s) }
            }
        }
    }

    /**
     * Builds `orders: list<struct{ inner: list<struct{ q: Int } } }>` — [ROWS] rows × [ELEMENTS_PER_ROW]
     * elements × [INNER_ELEMENTS] inner elements, all present, `q` = flat inner index — with the inner
     * list's struct vector counting, and runs [block] with everything open. A list nested inside a list
     * element re-enters `readListVector` once per outer row, so its element mask must not be built over
     * the whole inner element vector.
     *
     * The injected vectors (`element`, `inner`, the counting struct) are closed by this function, in
     * reverse order — each list closes only its own internal element vector, not the injected one.
     */
    private fun <T> withListInListElement(
        allocator: BufferAllocator,
        block: (VectorSchemaRoot, CountingStructVector) -> T,
    ): T {
        val qField = Field("q", intNN, null)
        val innerElementField = Field("qstruct", FieldType.nullable(ArrowType.Struct()), listOf(qField))
        val innerField = Field("inner", FieldType.nullable(ArrowType.List()), listOf(innerElementField))
        val elementField = Field("element", FieldType.notNullable(ArrowType.Struct()), listOf(innerField))
        val ordersField = Field("orders", FieldType.nullable(ArrowType.List()), listOf(elementField))

        val qStruct = CountingStructVector("qstruct", FieldType.nullable(ArrowType.Struct()), allocator)
        qStruct.initializeChildrenFromFields(listOf(qField))
        qStruct.allocateNew()
        val q = qStruct.getChild("q") as IntVector
        val innerCount = ROWS * ELEMENTS_PER_ROW * INNER_ELEMENTS
        for (i in 0 until innerCount) {
            qStruct.setIndexDefined(i)
            q.setSafe(i, i)
        }
        q.setValueCount(innerCount)
        qStruct.setValueCount(innerCount)

        val inner = InjectingListVector(innerField, allocator, qStruct)
        inner.initializeChildrenFromFields(listOf(innerElementField))
        inner.allocateNew()
        for (el in 0 until ROWS * ELEMENTS_PER_ROW) {
            inner.startNewValue(el)
            inner.endValue(el, INNER_ELEMENTS)
        }
        inner.setValueCount(ROWS * ELEMENTS_PER_ROW)

        val element = InjectingElementStruct(allocator, inner)
        element.allocateNew()
        for (el in 0 until ROWS * ELEMENTS_PER_ROW) element.setIndexDefined(el)
        element.setValueCount(ROWS * ELEMENTS_PER_ROW)

        val list = InjectingListVector(ordersField, allocator, element)
        list.initializeChildrenFromFields(listOf(elementField))
        list.allocateNew()
        for (i in 0 until ROWS) {
            list.startNewValue(i)
            list.endValue(i, ELEMENTS_PER_ROW)
        }
        val root = VectorSchemaRoot(listOf(ordersField), listOf(list))
        root.setRowCount(ROWS)

        return root.use {
            element.use {
                inner.use {
                    qStruct.use { block(root, qStruct) }
                }
            }
        }
    }

    private fun assertNestedStructData(df: AnyFrame) {
        val orders = df["orders"] as FrameColumn<*>
        orders.size() shouldBe ROWS
        val row0 = orders[0]
        row0.rowsCount() shouldBe ELEMENTS_PER_ROW
        val s0 = row0["s"] as ColumnGroup<*>
        s0["x"].values().toList() shouldBe listOf(0, 1)
        // the last row guards the index math: its elements are the flat indices 198 and 199
        val sLast = orders[ROWS - 1]["s"] as ColumnGroup<*>
        sLast["x"].values().toList() shouldBe listOf(198, 199)
    }

    private fun assertNestedListData(df: AnyFrame) {
        val orders = df["orders"] as FrameColumn<*>
        orders.size() shouldBe ROWS
        val row0Inner = orders[0]["inner"] as FrameColumn<*>
        row0Inner.size() shouldBe ELEMENTS_PER_ROW
        row0Inner[0]["q"].values().toList() shouldBe listOf(0, 1)
        // the last row's last element guards the index math: its inner q values are the flat indices 398 and 399
        val lastInner = orders[ROWS - 1]["inner"] as FrameColumn<*>
        lastInner[ELEMENTS_PER_ROW - 1]["q"].values().toList() shouldBe listOf(398, 399)
    }

    @Test
    fun `reading a struct nested inside a list element scans it at most once per batch`() {
        RootAllocator().use { allocator ->
            withStructInListElement(allocator) { root, s ->
                val df = SingleBatchReader(root, allocator).toDataFrame()
                s.nullCountScans should beLessThanOrEqualTo(1)
                assertNestedStructData(df)
            }
        }
    }

    @Test
    fun `reading a list nested inside a list element scans its element vector at most once per batch`() {
        RootAllocator().use { allocator ->
            withListInListElement(allocator) { root, qStruct ->
                val df = SingleBatchReader(root, allocator).toDataFrame()
                qStruct.nullCountScans should beLessThanOrEqualTo(1)
                assertNestedListData(df)
            }
        }
    }
}
