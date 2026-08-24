package org.jetbrains.kotlinx.dataframe.codeGen

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldBeEmpty
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.after
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.generateInterfaces
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.sumOf
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.impl.codeGen.ReplCodeGenerator
import org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl
import org.jetbrains.kotlinx.dataframe.io.readJsonStr
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.CompareResult
import org.jetbrains.kotlinx.dataframe.schema.CompareResult.IsDerived
import org.jetbrains.kotlinx.dataframe.schema.CompareResult.IsSuper
import org.jetbrains.kotlinx.dataframe.schema.CompareResult.Matches
import org.jetbrains.kotlinx.dataframe.schema.CompareResult.None
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.LENIENT
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT_FOR_NESTED_SCHEMAS
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.junit.Test
import kotlin.reflect.typeOf

class MatchSchemeTests {

    @DataSchema(isOpen = false)
    interface Snippet {
        val position: Int
        val info: String
    }

    @DataSchema(isOpen = false)
    interface Item {
        val kind: String
        val id: String
        val snippet: DataRow<Snippet>
    }

    @DataSchema(isOpen = false)
    interface PageInfo {
        val totalResults: Int
        val resultsPerPage: Int
        val snippets: DataFrame<Snippet>
    }

    @DataSchema
    interface DataRecord {
        val kind: String
        val items: DataFrame<Item>
        val pageInfo: DataRow<PageInfo>
    }

    val json =
        """
        {
            "kind": "qq",
            "pageInfo": {
                "totalResults": 2,
                "resultsPerPage": 3,
                "snippets": [
                    {
                        "position": 3,
                        "info": "str"
                    },
                    {
                        "position": 5,
                        "info": "txt"
                    }
                ]
            },
            "items": [
                {
                    "kind": "asd",
                    "id": "zxc",
                    "snippet": {
                        "position": 2,
                        "info": "qwe"
                    }
                }
            ]
        }
        """.trimIndent()

    val df = DataFrame.readJsonStr(json)

    val typed = df.cast<DataRecord>()

    @Test
    fun `marker is reused`() {
        val codeGen = ReplCodeGenerator.create()
        codeGen.process(DataRecord::class)
        codeGen.process(typed, ::typed).hasCaster shouldBe false
        val generated = codeGen.process(df, ::df)
        generated.declarations.shouldBeEmpty()
        generated.declarationsWithCastExpression("df") shouldBe
            "df.cast<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.DataRecord>()"
    }

    val modified = df.add("new") { 4 }

    @Test
    fun `marker is implemented`() {
        val codeGen = ReplCodeGenerator.create()
        codeGen.process(DataRecord::class)
        val generated = codeGen.process(modified, ::modified)
        generated.declarations.contains(DataRecord::class.simpleName!!) shouldBe true
    }

    @Test
    fun printSchema() {
        val res = df.generateInterfaces(extensionProperties = true)
        println(res)
    }

    @Test
    fun `simple data schema comparison`() {
        val scheme1 = dataFrameOf(
            "a" to columnOf(1, 2, 3, null),
            "b" to columnOf(1.0, 2.0, 3.0, 4.0),
        ).schema()

        val scheme2 = dataFrameOf(
            "a" to columnOf(1, 2, 3, 4),
            "b" to columnOf(1.0, 2.0, 3.0, 4.0),
        ).schema()

        val scheme3 = dataFrameOf(
            "c" to columnOf(1, 2, 3, 4),
        ).schema()

        scheme1.compare(scheme1, LENIENT) shouldBe Matches
        scheme2.compare(scheme2, LENIENT) shouldBe Matches
        scheme1.compare(scheme2, LENIENT) shouldBe IsSuper
        scheme2.compare(scheme1, LENIENT) shouldBe IsDerived
        scheme1.compare(scheme3, LENIENT) shouldBe None

        scheme1.compare(scheme1, STRICT_FOR_NESTED_SCHEMAS) shouldBe Matches
        scheme2.compare(scheme2, STRICT_FOR_NESTED_SCHEMAS) shouldBe Matches
        scheme1.compare(scheme2, STRICT_FOR_NESTED_SCHEMAS) shouldBe IsSuper
        scheme2.compare(scheme1, STRICT_FOR_NESTED_SCHEMAS) shouldBe IsDerived
        scheme1.compare(scheme3, STRICT_FOR_NESTED_SCHEMAS) shouldBe None

        scheme1.compare(scheme1, STRICT) shouldBe Matches
        scheme2.compare(scheme2, STRICT) shouldBe Matches
        scheme1.compare(scheme2, STRICT) shouldBe None
        scheme2.compare(scheme1, STRICT) shouldBe None
    }

    @Test
    fun `nested data schema comparison`() {
        val scheme1 = dataFrameOf(
            "a" to columnOf(
                "b" to columnOf(1.0, 2.0, 3.0, null),
            ),
        ).schema()

        val scheme2 = dataFrameOf(
            "a" to columnOf(
                "b" to columnOf(1.0, 2.0, 3.0, 4.0),
            ),
        ).schema()

        val scheme3 = dataFrameOf(
            "c" to columnOf(1, 2, 3, 4),
        ).schema()

        val scheme4 = dataFrameOf(
            "a" to columnOf(
                "b" to columnOf(1.0, 2.0, 3.0, null),
            ),
            "c" to columnOf(1, 2, 3, 4),
        ).schema()

        scheme1.compare(scheme1, LENIENT) shouldBe Matches
        scheme2.compare(scheme2, LENIENT) shouldBe Matches
        scheme1.compare(scheme2, LENIENT) shouldBe IsSuper
        scheme2.compare(scheme1, LENIENT) shouldBe IsDerived
        scheme1.compare(scheme3, LENIENT) shouldBe None

        scheme1.compare(scheme4, LENIENT) shouldBe IsSuper
        scheme4.compare(scheme1, LENIENT) shouldBe IsDerived

        scheme1.compare(scheme1, STRICT_FOR_NESTED_SCHEMAS) shouldBe Matches
        scheme2.compare(scheme2, STRICT_FOR_NESTED_SCHEMAS) shouldBe Matches
        scheme1.compare(scheme2, STRICT_FOR_NESTED_SCHEMAS) shouldBe None
        scheme2.compare(scheme1, STRICT_FOR_NESTED_SCHEMAS) shouldBe None
        scheme1.compare(scheme3, STRICT_FOR_NESTED_SCHEMAS) shouldBe None

        scheme1.compare(scheme4, STRICT_FOR_NESTED_SCHEMAS) shouldBe IsSuper
        scheme4.compare(scheme1, STRICT_FOR_NESTED_SCHEMAS) shouldBe IsDerived
        scheme2.compare(scheme4, STRICT_FOR_NESTED_SCHEMAS) shouldBe None
        scheme4.compare(scheme2, STRICT_FOR_NESTED_SCHEMAS) shouldBe None

        scheme1.compare(scheme1, STRICT) shouldBe Matches
        scheme2.compare(scheme2, STRICT) shouldBe Matches
        scheme1.compare(scheme2, STRICT) shouldBe None
        scheme2.compare(scheme1, STRICT) shouldBe None
        scheme1.compare(scheme3, STRICT) shouldBe None
        scheme3.compare(scheme1, STRICT) shouldBe None
    }

    @Test
    fun `comparison with order`() {
        val scheme1 = dataFrameOf(
            "a" to columnOf(1, 2, 3, 4),
            "b" to columnOf(1.0, 2.0, 3.0, 4.0),
        ).schema()

        val scheme1a = dataFrameOf(
            "a" to columnOf(1, 2, 3, 4),
            "b" to columnOf(1.0, 2.0, 3.0, 4.0),
        ).schema()

        val scheme2 = dataFrameOf(
            "b" to columnOf(1.0, 2.0, 3.0, 4.0),
            "a" to columnOf(1, 2, 3, 4),
        ).schema()

        scheme1.compare(scheme1a).matches() shouldBe true
        (scheme1 == scheme1a) shouldBe true

        scheme1.compare(scheme2).matches() shouldBe true
        (scheme1 == scheme2) shouldBe false
    }

    @Test
    fun `nested comparison with order`() {
        typed.schema().compare(typed.schema()).matches() shouldBe true
        (typed.schema() == typed.schema()) shouldBe true

        val movedInGroup = typed.move { pageInfo.resultsPerPage }.after { pageInfo.snippets }

        typed.schema().compare(movedInGroup.schema()).matches() shouldBe true
        (typed.schema() == movedInGroup.schema()) shouldBe false

        val movedInFrameAndGroup = typed
            .update { items }.with {
                it.move { kind }.after { id }
                    .move { snippet.position }.after { snippet.info }
            }

        typed.schema().compare(movedInFrameAndGroup.schema()).matches() shouldBe true
        (typed.schema() == movedInFrameAndGroup.schema()) shouldBe false
    }

    @Test
    fun `column order inside a column group affects equality but not compare`() {
        val scheme1 = dataFrameOf(
            "group" to columnOf(
                "a" to columnOf(1),
                "b" to columnOf(2),
            ),
        ).schema()

        val scheme2 = dataFrameOf(
            "group" to columnOf(
                "b" to columnOf(2),
                "a" to columnOf(1),
            ),
        ).schema()

        scheme1.compare(scheme2).matches() shouldBe true

        (scheme1 == scheme2) shouldBe false

        scheme1.hashCode() shouldNotBe scheme2.hashCode()
    }

    @Test
    fun `equals is exact where compare is lenient`() {
        val nullableScheme = dataFrameOf(
            "a" to columnOf(1, 2, 3, null),
        ).schema()

        val notNullScheme = dataFrameOf(
            "a" to columnOf(1, 2, 3, 4),
        ).schema()

        // compare is lenient about nullability, equals is not
        nullableScheme.compare(notNullScheme, LENIENT) shouldBe IsSuper
        notNullScheme.compare(nullableScheme, LENIENT) shouldBe IsDerived
        (nullableScheme == notNullScheme) shouldBe false
        (notNullScheme == nullableScheme) shouldBe false

        // and a superset is never equal either
        val superScheme = dataFrameOf(
            "a" to columnOf(1, 2, 3, 4),
            "b" to columnOf(1.0, 2.0, 3.0, 4.0),
        ).schema()

        notNullScheme.compare(superScheme, LENIENT) shouldBe IsSuper
        (notNullScheme == superScheme) shouldBe false
        (superScheme == notNullScheme) shouldBe false
    }

    @Test
    fun `equals of frame columns takes nullability into account`() {
        val nestedScheme = dataFrameOf(
            "a" to columnOf(1, 2, 3),
        ).schema()

        val notNullScheme = DataFrameSchemaImpl(
            mapOf("frame" to ColumnSchema.Frame(nestedScheme, nullable = false, contentType = null)),
        )

        val nullableScheme = DataFrameSchemaImpl(
            mapOf("frame" to ColumnSchema.Frame(nestedScheme, nullable = true, contentType = null)),
        )

        (notNullScheme == nullableScheme) shouldBe false
        (nullableScheme == notNullScheme) shouldBe false

        notNullScheme.compare(nullableScheme, LENIENT) shouldBe IsDerived
        nullableScheme.compare(notNullScheme, LENIENT) shouldBe IsSuper
    }

    @Test
    fun `equals delegates to ColumnSchema equals`() {
        val nestedScheme = dataFrameOf(
            "a" to columnOf(1, 2, 3),
        ).schema()

        val otherNestedScheme = dataFrameOf(
            "b" to columnOf(1, 2, 3),
        ).schema()

        // for each column kind, equality of a single-column schema must follow equality of that ColumnSchema
        val pairs = listOf(
            ColumnSchema.Value(typeOf<Int>()) to ColumnSchema.Value(typeOf<Int?>()),
            ColumnSchema.Group(nestedScheme, contentType = null) to
                ColumnSchema.Group(otherNestedScheme, contentType = null),
            ColumnSchema.Frame(nestedScheme, nullable = false, contentType = null) to
                ColumnSchema.Frame(otherNestedScheme, nullable = false, contentType = null),
        )

        for ((col, otherCol) in pairs) {
            (col == otherCol) shouldBe false
            val schema = DataFrameSchemaImpl(mapOf("x" to col))
            (schema == DataFrameSchemaImpl(mapOf("x" to col))) shouldBe true
            (schema == DataFrameSchemaImpl(mapOf("x" to otherCol))) shouldBe false
        }
    }

    @Test
    fun `equals does not throw for other DataFrameSchema implementations`() {
        val scheme = dataFrameOf(
            "a" to columnOf(1, 2, 3),
        ).schema()

        fun foreignSchemaOf(columns: Map<String, ColumnSchema>) =
            object : DataFrameSchema {
                override val columns: Map<String, ColumnSchema> = columns

                override fun compare(other: DataFrameSchema, comparisonMode: ComparisonMode): CompareResult =
                    throw UnsupportedOperationException("should not be called by equals")
            }

        (scheme == foreignSchemaOf(scheme.columns)) shouldBe true
        (scheme == foreignSchemaOf(emptyMap())) shouldBe false
    }

    @Test
    fun `equal schemas have equal hash codes`() {
        val scheme = typed.schema()
        val same = typed.schema()

        (scheme == same) shouldBe true
        scheme.hashCode() shouldBe same.hashCode()

        val reordered = typed.move { pageInfo.resultsPerPage }.after { pageInfo.snippets }.schema()

        (scheme == reordered) shouldBe false
        scheme.compare(reordered, LENIENT) shouldBe Matches
        same.hashCode() shouldNotBe reordered.hashCode()
    }
}
