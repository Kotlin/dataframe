package org.jetbrains.kotlinx.dataframe.internal.codeGen

import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.codeGen.MarkersExtractor
import org.jetbrains.kotlinx.dataframe.impl.codeGen.ShortNames
import org.jetbrains.kotlinx.dataframe.impl.codeGen.TypeRenderingStrategy
import org.junit.Test

internal class ShortNamesRenderingTest : TypeRenderingStrategy by ShortNames {

    interface Marker
    interface A {
        val b: Int
        val d: () -> Unit
        val e: DataRow<Marker>
        val f: DataFrame<Marker>
    }

    private val fields by lazy {
        MarkersExtractor[A::class].allFields.associateBy { it.fieldName.unquoted }
    }

    @Test
    fun `builtin type`() {
        fields.keys.asClue {
            fields["b"]!!.renderFieldType() shouldBe "Int"
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
    fun `builtin type column`() {
        fields.keys.asClue {
            fields["b"]!!.renderColumnType() shouldBe "DataColumn<Int>"
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
}
