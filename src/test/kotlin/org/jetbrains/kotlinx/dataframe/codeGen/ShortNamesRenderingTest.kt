package org.jetbrains.kotlinx.dataframe.internal.codeGen

import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.codeGen.MarkersExtractor
import org.jetbrains.kotlinx.dataframe.impl.codeGen.ShortNames
import org.jetbrains.kotlinx.dataframe.impl.codeGen.TypeRenderingStrategy
import org.junit.Test

internal class ShortNamesRenderingTest : TypeRenderingStrategy by ShortNames {

    interface Marker

    @DataSchema
    interface DataSchemaMarker

    interface A {
        val a: DataSchemaMarker
        val b: Int
        val c: List<DataSchemaMarker>
        val d: () -> Unit
        val e: DataRow<Marker>
        val f: DataFrame<Marker>
    }

    private val fields by lazy {
        MarkersExtractor[A::class].allFields.associateBy { it.fieldName.unquoted }
    }

    @Test
    fun `data schema type`() {
        fields.keys.asClue {
            fields["a"]!!.renderFieldType() shouldBe "DataRow<org.jetbrains.kotlinx.dataframe.internal.codeGen.ShortNamesRenderingTest.DataSchemaMarker>"
        }
    }

    @Test
    fun `builtin type`() {
        fields.keys.asClue {
            fields["b"]!!.renderFieldType() shouldBe "Int"
        }
    }

    @Test
    fun `list parametrized by data schema type`() {
        fields.keys.asClue {
            fields["c"]!!.renderFieldType() shouldBe "DataFrame<org.jetbrains.kotlinx.dataframe.internal.codeGen.ShortNamesRenderingTest.DataSchemaMarker>"
        }
    }

    @Test
    fun `short functional types are not supported`() {
        fields.keys.asClue {
            fields["d"]!!.renderFieldType() shouldBe "() -> kotlin.Unit"
        }
    }

    @Test
    fun `data row`() {
        fields.keys.asClue {
            fields["e"]!!.renderFieldType() shouldBe "DataRow<org.jetbrains.kotlinx.dataframe.internal.codeGen.ShortNamesRenderingTest.Marker>"
        }
    }

    @Test
    fun `data frame`() {
        fields.keys.asClue {
            fields["f"]!!.renderFieldType() shouldBe "DataFrame<org.jetbrains.kotlinx.dataframe.internal.codeGen.ShortNamesRenderingTest.Marker>"
        }
    }

    @Test
    fun `column for data schema type`() {
        fields.keys.asClue {
            fields["a"]!!.renderColumnType() shouldBe "ColumnGroup<org.jetbrains.kotlinx.dataframe.internal.codeGen.ShortNamesRenderingTest.DataSchemaMarker>"
        }
    }

    @Test
    fun `builtin type column`() {
        fields.keys.asClue {
            fields["b"]!!.renderColumnType() shouldBe "DataColumn<Int>"
        }
    }

    @Test
    fun `column for list parametrized by data schema type`() {
        fields.keys.asClue {
            fields["c"]!!.renderColumnType() shouldBe "DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.internal.codeGen.ShortNamesRenderingTest.DataSchemaMarker>>"
        }
    }

    @Test
    fun `functional type column`() {
        fields.keys.asClue {
            fields["d"]!!.renderColumnType() shouldBe "DataColumn<() -> kotlin.Unit>"
        }
    }

    @Test
    fun `data row column`() {
        fields.keys.asClue {
            fields["e"]!!.renderColumnType() shouldBe "ColumnGroup<org.jetbrains.kotlinx.dataframe.internal.codeGen.ShortNamesRenderingTest.Marker>"
        }
    }

    @Test
    fun `data frame column`() {
        fields.keys.asClue {
            fields["f"]!!.renderColumnType() shouldBe "DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.internal.codeGen.ShortNamesRenderingTest.Marker>>"
        }
    }

    interface GenericDataSchema<A> {
        val a: A
    }

    @Test
    fun `generic field`() {
        MarkersExtractor[GenericDataSchema::class].allFields[0].renderFieldType() shouldBe "A"
    }

    @Test
    fun `generic column`() {
        MarkersExtractor[GenericDataSchema::class].allFields[0].renderColumnType() shouldBe "DataColumn<A>"
    }
}
