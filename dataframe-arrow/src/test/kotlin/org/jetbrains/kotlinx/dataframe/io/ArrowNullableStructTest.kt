package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.apache.arrow.dataset.file.DatasetFileWriter
import org.apache.arrow.dataset.file.FileFormat
import org.apache.arrow.memory.RootAllocator
import org.apache.arrow.vector.IntVector
import org.apache.arrow.vector.TimeStampNanoVector
import org.apache.arrow.vector.VarCharVector
import org.apache.arrow.vector.VectorSchemaRoot
import org.apache.arrow.vector.complex.ListVector
import org.apache.arrow.vector.complex.StructVector
import org.apache.arrow.vector.ipc.ArrowStreamReader
import org.apache.arrow.vector.types.TimeUnit
import org.apache.arrow.vector.types.pojo.ArrowType
import org.apache.arrow.vector.types.pojo.Field
import org.apache.arrow.vector.types.pojo.FieldType
import org.apache.arrow.vector.types.pojo.Schema
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.NullabilityOptions
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.junit.Ignore
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.toPath
import kotlin.reflect.typeOf

/**
 * Reading **nullable nested (Arrow struct / Parquet optional-group) columns**. An optional struct is read as a
 * [ColumnGroup] whose children become nullable and hold `null` where the struct is absent (a [ColumnGroup] itself
 * is never `null`). Covers flat, nested, list-of-struct and struct-in-list shapes, plus the documented
 * "absent struct vs present all-null struct" limitation.
 *
 * Reproduces and guards the bug reported in [#2041](https://github.com/Kotlin/dataframe/issues/2041).
 */
internal class ArrowNullableStructTest {

    fun testResource(resourcePath: String): URL =
        ArrowNullableStructTest::class.java.classLoader.getResource(resourcePath)!!

    // region test helpers — see arrowTestUtils.kt (arrowBytes / toArrowStreamBytes) for the Arrow-building idiom

    private val intType = FieldType.notNullable(ArrowType.Int(32, true))

    private fun channelField(name: String): Field =
        Field(
            name,
            FieldType.nullable(ArrowType.Struct()),
            listOf(Field("x", intType, null), Field("y", intType, null)),
        )

    /** The two-channel schema from the bug report: a required `timestamp` plus two optional `{ x, y }` groups. */
    private fun channelFields(): List<Field> =
        listOf(
            Field("timestamp", FieldType.notNullable(ArrowType.Timestamp(TimeUnit.NANOSECOND, null)), null),
            channelField("record_channel_1"),
            channelField("record_channel_2"),
        )

    /**
     * Fills a root created with [channelFields]. The absent group in each row is marked via the struct's own
     * validity bit ([StructVector.setNull]) while the child buffers deliberately hold **non-zero** values — the
     * physical layout that produced the bug.
     *
     * Head (hidden physical values under a null parent shown in parentheses):
     * ```
     * timestamp    record_channel_1        record_channel_2
     * 2000-01-01   { x:1, y:2 }             null  (hidden x:666, y:777)
     * 2000-01-02   null  (hidden x:555,444) { x:3, y:4 }
     * ```
     */
    private fun VectorSchemaRoot.populateChannels() {
        val ts = getVector("timestamp") as TimeStampNanoVector
        val c1 = getVector("record_channel_1") as StructVector
        val c2 = getVector("record_channel_2") as StructVector
        c1.allocateNew()
        c2.allocateNew()
        val c1x = c1.getChild("x") as IntVector
        val c1y = c1.getChild("y") as IntVector
        val c2x = c2.getChild("x") as IntVector
        val c2y = c2.getChild("y") as IntVector

        ts.setSafe(0, 946684800000L * 1_000_000L) // 2000-01-01T00:00:00Z
        ts.setSafe(1, 946771200000L * 1_000_000L) // 2000-01-02T00:00:00Z

        // row0: record_channel_1 present {1,2}; record_channel_2 absent, but with non-zero hidden child values
        c1.setIndexDefined(0)
        c1x.setSafe(0, 1)
        c1y.setSafe(0, 2)
        c2x.setSafe(0, 666)
        c2y.setSafe(0, 777)
        c2.setNull(0)

        // row1: record_channel_1 absent (non-zero hidden values); record_channel_2 present {3,4}
        c1x.setSafe(1, 555)
        c1y.setSafe(1, 444)
        c1.setNull(1)
        c2.setIndexDefined(1)
        c2x.setSafe(1, 3)
        c2y.setSafe(1, 4)

        setRowCount(2)
    }

    /**
     * Writes the [channelFields] / [populateChannels] data to a single Parquet file at [target] via Arrow Dataset
     * (`arrow-dataset`, already on the classpath). Pure JVM, so it runs on Windows/Mac/Linux. `notNullable` Arrow
     * child fields are written as parquet `required`, reproducing the exact bug-report schema; DuckDB and the
     * DataFrame Arrow writer instead emit nullable children and would not reproduce it. `DatasetFileWriter` writes
     * into a directory, so the produced part-file is copied to [target].
     */
    private fun writeNullableStructParquet(target: File) {
        RootAllocator().use { allocator ->
            VectorSchemaRoot.create(Schema(channelFields()), allocator).use { root ->
                root.populateChannels()
                val outDir = Files.createTempDirectory("nullable-struct-parquet")
                try {
                    ArrowStreamReader(ByteArrayInputStream(root.toArrowStreamBytes()), allocator).use { reader ->
                        DatasetFileWriter.write(allocator, reader, FileFormat.PARQUET, outDir.toUri().toString())
                    }
                    val producedFiles = outDir.toFile().walkTopDown().filter { it.isFile }.toList()
                    check(producedFiles.isNotEmpty()) { "DatasetFileWriter produced no files in $outDir" }
                    val produced = producedFiles.firstOrNull { it.extension == "parquet" } ?: producedFiles.first()
                    target.parentFile?.mkdirs()
                    Files.copy(produced.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
                } finally {
                    outDir.toFile().deleteRecursively()
                }
            }
        }
    }

    /**
     * Shared assertion for the two-channel data. An optional Arrow struct is read as a [ColumnGroup] whose
     * children become nullable and `null` where the group is absent. Used by both the file-based and the
     * generated tests so they cannot diverge.
     *
     * Head (as read):
     * ```
     * record_channel_1   record_channel_2
     * { x:1, y:2 }        null
     * null                { x:3, y:4 }
     * ```
     */
    private fun assertNullableChannels(df: AnyFrame) {
        val ch1 = df["record_channel_1"].shouldBeInstanceOf<ColumnGroup<*>>()
        val ch2 = df["record_channel_2"].shouldBeInstanceOf<ColumnGroup<*>>()

        ch1["x"].type() shouldBe typeOf<Int?>()
        ch1["y"].type() shouldBe typeOf<Int?>()
        ch2["x"].type() shouldBe typeOf<Int?>()
        ch2["y"].type() shouldBe typeOf<Int?>()

        ch1["x"].values().toList() shouldBe listOf(1, null)
        ch1["y"].values().toList() shouldBe listOf(2, null)
        ch2["x"].values().toList() shouldBe listOf(null, 3)
        ch2["y"].values().toList() shouldBe listOf(null, 4)
    }

    // endregion

    /** Deterministic in-memory reproduction: null carried by the parent bit over non-zero hidden child values. */
    @Test
    fun `nullable struct masks hidden child values`() {
        val bytes = arrowBytes(*channelFields().toTypedArray()) { it.populateChannels() }
        assertNullableChannels(DataFrame.readArrowIPC(bytes))
    }

    /**
     * A null parent must not be treated as a nullability violation of its `required` children (read with
     * [NullabilityOptions.Checking]).
     */
    @Test
    fun `nullable parent does not violate required child in checking mode`() {
        val bytes = arrowBytes(*channelFields().toTypedArray()) { it.populateChannels() }
        assertNullableChannels(DataFrame.readArrowIPC(bytes, nullability = NullabilityOptions.Checking))
    }

    /**
     * A `required` (non-null) child that is **physically `null`** under a null-parent row must not be reported as a
     * nullability violation when reading with [NullabilityOptions.Checking] — the parent-null is pushed down, so the
     * child cell is legitimately `null`. This is the real Parquet layout (an optional group's required children are
     * materialized as null when the group is absent), which the two-channel fixture above does not reproduce because
     * it fills the hidden child cells via `setSafe`.
     *
     * Schema: `rec: { x: Int (required) }?` — row0 present `{x:1}`; row1 struct null with `x` physically null.
     */
    @Test
    fun `required child physically null under null parent passes checking mode`() {
        val rec = Field("rec", FieldType.nullable(ArrowType.Struct()), listOf(Field("x", intType, null)))

        val bytes = arrowBytes(rec) { root ->
            val recV = root.getVector("rec") as StructVector
            recV.allocateNew()
            val x = recV.getChild("x") as IntVector
            recV.setIndexDefined(0)
            x.setSafe(0, 1)
            recV.setNull(1)
            x.setNull(1) // required child is physically null at the null-parent row
            root.setRowCount(2)
        }

        val group = DataFrame.readArrowIPC(bytes, nullability = NullabilityOptions.Checking)["rec"]
            .shouldBeInstanceOf<ColumnGroup<*>>()
        group["x"].type() shouldBe typeOf<Int?>()
        group["x"].values().toList() shouldBe listOf(1, null)
    }

    /**
     * End-to-end parquet path, generated in code (team-controlled, cross-platform).
     * Head (expected): row0 `{x:1,y:2}, null`; row1 `null, {x:3,y:4}`.
     */
    @Test
    fun `read parquet with nullable nested struct generated via arrow dataset`() {
        val target = File.createTempFile("nullable-struct-", ".parquet")
        try {
            writeNullableStructParquet(target)
            val df = DataFrame.readParquet(target)
            df.columnNames() shouldBe listOf("timestamp", "record_channel_1", "record_channel_2")
            assertNullableChannels(df)
        } finally {
            target.delete()
        }
    }

    /**
     * Regenerates the committed fixture `src/test/resources/nullable_nested_struct.parquet` (row0
     * `ch1={1,2}, ch2=null`; row1 `ch1=null, ch2={3,4}`). Not part of CI (Gradle's test working directory is the
     * module dir, so it writes into the source tree); run manually on any OS when the schema/data changes:
     * temporarily remove `@Ignore` and run `dataframe-arrow:test --tests "*regenerate nullable struct*"`.
     */
    @Ignore("run manually to regenerate src/test/resources/nullable_nested_struct.parquet")
    @Test
    fun `regenerate nullable struct parquet fixture`() {
        writeNullableStructParquet(File("src/test/resources/nullable_nested_struct.parquet"))
    }

    /**
     * End-to-end parquet path, reading the committed fixture (see TestFiles.md).
     * Head (expected): row0 `{x:1,y:2}, null`; row1 `null, {x:3,y:4}`.
     */
    @Test
    fun `read parquet with nullable nested struct from resources`() {
        val df = DataFrame.readParquet(testResource("nullable_nested_struct.parquet").toURI().toPath())
        df.columnNames() shouldBe listOf("timestamp", "record_channel_1", "record_channel_2")
        assertNullableChannels(df)
    }

    /**
     * A null parent nulls all descendant leaves (nested struct); a sibling required struct is untouched.
     *
     * Head (hidden physical values under the null parent in parentheses):
     * ```
     * req        rec
     * { p:7 }    { x:1, inner:{ a:100 } }
     * { p:8 }    { x:null, inner:{ a:null } }   // rec is null (hidden x:999, a:777) -> all descendants null
     * ```
     */
    @Test
    fun `nullable nested struct nulls descendants and keeps required struct`() {
        val required = Field("req", FieldType.notNullable(ArrowType.Struct()), listOf(Field("p", intType, null)))
        val rec = Field(
            "rec",
            FieldType.nullable(ArrowType.Struct()),
            listOf(
                Field("x", intType, null),
                Field("inner", FieldType.nullable(ArrowType.Struct()), listOf(Field("a", intType, null))),
            ),
        )

        val bytes = arrowBytes(required, rec) { root ->
            val req = root.getVector("req") as StructVector
            val recV = root.getVector("rec") as StructVector
            req.allocateNew()
            recV.allocateNew()
            val reqP = req.getChild("p") as IntVector
            val x = recV.getChild("x") as IntVector
            val innerV = recV.getChild("inner") as StructVector
            val a = innerV.getChild("a") as IntVector

            req.setIndexDefined(0)
            reqP.setSafe(0, 7)
            recV.setIndexDefined(0)
            x.setSafe(0, 1)
            innerV.setIndexDefined(0)
            a.setSafe(0, 100)

            req.setIndexDefined(1)
            reqP.setSafe(1, 8)
            x.setSafe(1, 999) // hidden value under the null parent
            innerV.setIndexDefined(1)
            a.setSafe(1, 777) // hidden value under the null parent
            recV.setNull(1)

            root.setRowCount(2)
        }

        val df = DataFrame.readArrowIPC(bytes)

        val req = df["req"].shouldBeInstanceOf<ColumnGroup<*>>()
        req["p"].type() shouldBe typeOf<Int>()
        req["p"].values().toList() shouldBe listOf(7, 8)

        val rec2 = df["rec"].shouldBeInstanceOf<ColumnGroup<*>>()
        rec2["x"].type() shouldBe typeOf<Int?>()
        rec2["x"].values().toList() shouldBe listOf(1, null)
        val innerGroup = rec2["inner"].shouldBeInstanceOf<ColumnGroup<*>>()
        innerGroup["a"].type() shouldBe typeOf<Int?>()
        innerGroup["a"].values().toList() shouldBe listOf(100, null)

        // our representation round-trips through Feather and IPC
        DataFrame.readArrowFeather(df.saveArrowFeatherToByteArray()) shouldBe df
        DataFrame.readArrowIPC(df.saveArrowIPCToByteArray()) shouldBe df
    }

    /**
     * Documented limitation: an absent struct and a present struct whose children are all null both read
     * as `{x:null, y:null}` — a [ColumnGroup] has no per-row null mask to distinguish them.
     *
     * Schema: `rec: { x: Int?, y: Int? }`
     * Head (built -> as read, both rows collapse to the same value):
     * ```
     * rec (built)                     rec (as read)
     * null  (whole struct is null)    { x:null, y:null }
     * { x:null, y:null }              { x:null, y:null }
     * ```
     */
    @Test
    fun `null struct and present struct with null children are indistinguishable`() {
        val nullableInt = FieldType.nullable(ArrowType.Int(32, true))
        val rec = Field(
            "rec",
            FieldType.nullable(ArrowType.Struct()),
            listOf(Field("x", nullableInt, null), Field("y", nullableInt, null)),
        )

        val bytes = arrowBytes(rec) { root ->
            val recV = root.getVector("rec") as StructVector
            recV.allocateNew()
            val x = recV.getChild("x") as IntVector
            val y = recV.getChild("y") as IntVector
            // row0: whole struct null (with hidden values); row1: struct present, children null
            x.setSafe(0, 111)
            y.setSafe(0, 222)
            recV.setNull(0)
            recV.setIndexDefined(1)
            x.setNull(1)
            y.setNull(1)
            root.setRowCount(2)
        }

        val group = DataFrame.readArrowIPC(bytes)["rec"].shouldBeInstanceOf<ColumnGroup<*>>()
        group["x"].values().toList() shouldBe listOf(null, null)
        group["y"].values().toList() shouldBe listOf(null, null)
    }

    /**
     * A **null struct element inside a list**, where the first element of the second row's list is a null
     * struct that physically holds non-zero hidden values. After the fix that element reads as
     * `{item:null, qty:null}` instead of the phantom hidden values.
     *
     * Schema: `orders: list<struct{ item: String, qty: Int }>`
     * Head (as read):
     * ```
     * orders
     * [ {item:"A", qty:1}, {item:"B", qty:2} ]
     * [ {item:null, qty:null}, {item:"C", qty:4} ]   // 1st element was a null struct (hidden "HID", 999)
     * ```
     */
    @Test
    fun `null struct element inside a list is nulled`() {
        val element = Field(
            "element",
            FieldType.nullable(ArrowType.Struct()),
            listOf(Field("item", FieldType.notNullable(ArrowType.Utf8()), null), Field("qty", intType, null)),
        )
        val orders = Field("orders", FieldType.nullable(ArrowType.List()), listOf(element))

        val listWithNullableStructElement = arrowBytes(orders) { root ->
            val list = root.getVector("orders") as ListVector
            list.allocateNew()
            val struct = list.dataVector as StructVector
            val item = struct.getChild("item") as VarCharVector
            val qty = struct.getChild("qty") as IntVector

            // flat elements: 0={A,1} 1={B,2} 2=null(hidden HID,999) 3={C,4}
            item.setSafe(0, "A".toByteArray())
            qty.setSafe(0, 1)
            struct.setIndexDefined(0)
            item.setSafe(1, "B".toByteArray())
            qty.setSafe(1, 2)
            struct.setIndexDefined(1)
            item.setSafe(2, "HID".toByteArray())
            qty.setSafe(2, 999)
            struct.setNull(2)
            item.setSafe(3, "C".toByteArray())
            qty.setSafe(3, 4)
            struct.setIndexDefined(3)
            struct.setValueCount(4)

            // row0 = [0,2) ; row1 = [2,4)
            list.startNewValue(0)
            list.endValue(0, 2)
            list.startNewValue(1)
            list.endValue(1, 2)
            root.setRowCount(2)
        }

        val ordersColumn = DataFrame.readArrowIPC(listWithNullableStructElement)["orders"]
            .shouldBeInstanceOf<FrameColumn<*>>()

        // row0: both list elements present
        val row0 = ordersColumn[0]
        row0[0]["item"] shouldBe "A"
        row0[0]["qty"] shouldBe 1
        row0[1]["item"] shouldBe "B"
        row0[1]["qty"] shouldBe 2

        // row1: its 1st element was a null struct -> that element's cells are null, not the hidden "HID"/999
        val row1 = ordersColumn[1]
        row1[0]["item"] shouldBe null
        row1[0]["qty"] shouldBe null
        row1[1]["item"] shouldBe "C"
        row1[1]["qty"] shouldBe 4
    }

    /**
     * "Boss" case exercising every branch of the null-propagation helper in one frame, with nulls at two
     * nesting levels. All null-parent slots physically hold non-zero hidden values, so a fix relying on
     * zero-fill would fail.
     *
     * Schema (columns below are inside the top-level `outer` group):
     * ```
     * outer: {                      // nullable struct
     *   a:     Int?,                // value
     *   tags:  List<String>?,       // value (list of primitive)
     *   items: [ { q: Int? } ],     // FrameColumn (list of struct)
     *   mid:   {                    // nullable struct
     *     b:     Int?,
     *     inner: { c: Int? },       // nullable struct (3rd level)
     *   },
     * }
     * ```
     * Head (as read; `outer` is null in row1, `mid` is null in row2 — nulls compose across levels):
     * ```
     * a     tags       items             mid
     * 1     [p, q]     [ {q:10} ]        { b:2, inner:{ c:3 } }
     * null  null       []  (empty)       { b:null, inner:{ c:null } }   // outer == null
     * 4     [r]        [ {q:20},{q:30} ] { b:null, inner:{ c:null } }   // mid == null
     * ```
     */
    @Test
    fun `boss case - nulls at multiple levels across value list frame and group children`() {
        val tags = Field(
            "tags",
            FieldType.nullable(ArrowType.List()),
            listOf(Field("element", FieldType.notNullable(ArrowType.Utf8()), null)),
        )
        val itemElement = Field("element", FieldType.nullable(ArrowType.Struct()), listOf(Field("q", intType, null)))
        val items = Field("items", FieldType.nullable(ArrowType.List()), listOf(itemElement))
        val inner = Field("inner", FieldType.nullable(ArrowType.Struct()), listOf(Field("c", intType, null)))
        val mid = Field("mid", FieldType.nullable(ArrowType.Struct()), listOf(Field("b", intType, null), inner))
        val outer = Field(
            "outer",
            FieldType.nullable(ArrowType.Struct()),
            listOf(Field("a", intType, null), tags, items, mid),
        )

        val bytes = arrowBytes(outer) { root ->
            val outerV = root.getVector("outer") as StructVector
            outerV.allocateNew()
            val a = outerV.getChild("a") as IntVector
            val tagsV = outerV.getChild("tags") as ListVector
            val tagData = tagsV.dataVector as VarCharVector
            val itemsV = outerV.getChild("items") as ListVector
            val itemStruct = itemsV.dataVector as StructVector
            val q = itemStruct.getChild("q") as IntVector
            val midV = outerV.getChild("mid") as StructVector
            val b = midV.getChild("b") as IntVector
            val innerV = midV.getChild("inner") as StructVector
            val c = innerV.getChild("c") as IntVector

            // a: present, hidden, present
            a.setSafe(0, 1)
            a.setSafe(1, -111)
            a.setSafe(2, 4)

            // tags flat: r0=["p","q"](0,1) r1=["HID"](2) r2=["r"](3)
            tagData.setSafe(0, "p".toByteArray())
            tagData.setSafe(1, "q".toByteArray())
            tagData.setSafe(2, "HID".toByteArray())
            tagData.setSafe(3, "r".toByteArray())
            tagData.setValueCount(4)
            tagsV.startNewValue(0)
            tagsV.endValue(0, 2)
            tagsV.startNewValue(1)
            tagsV.endValue(1, 1)
            tagsV.startNewValue(2)
            tagsV.endValue(2, 1)

            // items flat: r0=[{10}](0) r1=[{999 hidden}](1) r2=[{20},{30}](2,3)
            q.setSafe(0, 10)
            itemStruct.setIndexDefined(0)
            q.setSafe(1, 999)
            itemStruct.setIndexDefined(1)
            q.setSafe(2, 20)
            itemStruct.setIndexDefined(2)
            q.setSafe(3, 30)
            itemStruct.setIndexDefined(3)
            itemStruct.setValueCount(4)
            itemsV.startNewValue(0)
            itemsV.endValue(0, 1)
            itemsV.startNewValue(1)
            itemsV.endValue(1, 1)
            itemsV.startNewValue(2)
            itemsV.endValue(2, 2)

            // mid: r0 present {b:2, c:3}; r1 present-but-hidden {888,777}; r2 null {666,555 hidden}
            b.setSafe(0, 2)
            c.setSafe(0, 3)
            innerV.setIndexDefined(0)
            midV.setIndexDefined(0)
            b.setSafe(1, 888)
            c.setSafe(1, 777)
            innerV.setIndexDefined(1)
            midV.setIndexDefined(1)
            b.setSafe(2, 666)
            c.setSafe(2, 555)
            innerV.setIndexDefined(2)
            midV.setNull(2)
            c.setValueCount(3)
            innerV.setValueCount(3)
            b.setValueCount(3)
            midV.setValueCount(3)

            // outer: present, null, present
            outerV.setIndexDefined(0)
            outerV.setNull(1)
            outerV.setIndexDefined(2)
            root.setRowCount(3)
        }

        val outerGroup = DataFrame.readArrowIPC(bytes)["outer"].shouldBeInstanceOf<ColumnGroup<*>>()

        // value child
        outerGroup["a"].type() shouldBe typeOf<Int?>()
        outerGroup["a"].values().toList() shouldBe listOf(1, null, 4)

        // value<list> child
        outerGroup["tags"].type() shouldBe typeOf<List<String>?>()
        outerGroup["tags"].values().toList() shouldBe listOf(listOf("p", "q"), null, listOf("r"))

        // frame child (list of struct): null parent -> empty frame
        val itemsColumn = outerGroup["items"].shouldBeInstanceOf<FrameColumn<*>>()
        itemsColumn[0]["q"].values().toList() shouldBe listOf(10)
        itemsColumn[1].rowsCount() shouldBe 0
        itemsColumn[2]["q"].values().toList() shouldBe listOf(20, 30)

        // nested group children: nulls compose across the outer (row1) and mid (row2) levels
        val midGroup = outerGroup["mid"].shouldBeInstanceOf<ColumnGroup<*>>()
        midGroup["b"].type() shouldBe typeOf<Int?>()
        midGroup["b"].values().toList() shouldBe listOf(2, null, null)
        val innerGroup = midGroup["inner"].shouldBeInstanceOf<ColumnGroup<*>>()
        innerGroup["c"].type() shouldBe typeOf<Int?>()
        innerGroup["c"].values().toList() shouldBe listOf(3, null, null)
    }
}
