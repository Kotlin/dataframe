package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.exceptions.CellConversionException
import org.jetbrains.kotlinx.dataframe.exceptions.TypeConversionException
import org.jetbrains.kotlinx.dataframe.exceptions.TypeConverterNotFoundException
import org.jetbrains.kotlinx.dataframe.hasNulls
import org.junit.Test
import java.time.LocalTime
import kotlin.reflect.*
import kotlin.time.Duration.Companion.hours

class ConvertTests {

    @Test
    fun `convert nullable strings to time`() {
        val time by columnOf("11?22?33", null)
        val converted = time.toDataFrame().convert { time }.toLocalTime("HH?mm?ss")[time]
        converted.hasNulls shouldBe true
        converted[0] shouldBe LocalTime.of(11, 22, 33)
    }

    @Test
    fun `nullability persistence after conversion`() {
        val col by columnOf("1", null)
        col.convertToInt().forEach {
        }
    }

    @DataSchema
    data class Schema(val time: Instant)

    @Test
    fun `Instant to LocalDateTime`() {
        val df = listOf(Schema(Clock.System.now())).toDataFrame()
        df.convert { time }.toLocalDateTime()
    }

    enum class EnumClass { A, B }

    @Test
    fun `convert string to enum`() {
        columnOf("A", "B").convertTo<EnumClass>() shouldBe columnOf(EnumClass.A, EnumClass.B)
    }

    @JvmInline
    value class IntClass(val v: Int)

    @JvmInline
    value class StringClass(val s: String?)

    @JvmInline
    value class PrivateInt(private val v: Int)

    @Test
    fun `convert string to value class`() {
        columnOf("1").convertTo<IntClass>() shouldBe columnOf(IntClass(1))
    }

    @Test
    fun `convert double to value class`() {
        columnOf(1.0).convertTo<IntClass>() shouldBe columnOf(IntClass(1))
    }

    @Test
    fun `convert from value class `() {
        columnOf(IntClass(1)).convertTo<Double>() shouldBe columnOf(1.0)
        columnOf(StringClass("1"), StringClass(null)).convertTo<Double?>() shouldBe columnOf(1.0, null)
    }

    @Test
    fun `convert to value class exceptions`() {
        shouldThrow<TypeConversionException> {
            columnOf("a").convertTo<IntClass>()
        }

        shouldThrow<CellConversionException> {
            columnOf("1", "10", "a").convertTo<IntClass>()
        }.row shouldBe 2

        shouldThrow<TypeConverterNotFoundException> {
            columnOf(EnumClass.A).convertTo<IntClass>()
        }
    }

    @Test
    fun `convert from value class exceptions`() {
        shouldThrow<TypeConversionException> {
            columnOf(StringClass("a")).convertTo<Int>()
        }.from shouldBe typeOf<String>()

        shouldThrow<TypeConverterNotFoundException> {
            columnOf(IntClass(1)).convertTo<EnumClass>()
        }

        shouldThrow<TypeConversionException> {
            columnOf(StringClass(null)).convertTo<Double>()
        }

        shouldThrow<TypeConversionException> {
            columnOf(PrivateInt(1)).convertTo<Double>()
        }
    }

    @Test
    fun `convert null strings`() {
        val col = columnOf("none")

        shouldThrow<TypeConversionException> {
            col.convertTo<Int>()
        }

        shouldThrow<TypeConversionException> {
            col.convertTo<Int?>()
        }

        DataFrame.parser.addNullString("none")

        shouldThrow<TypeConversionException> {
            col.convertTo<Int>()
        }

        col.convertTo<Int?>() shouldBe DataColumn.createValueColumn("", listOf(null), typeOf<Int?>())

        DataFrame.parser.resetToDefault()
    }

    @Test
    fun `convert to not nullable`() {
        val col = columnOf(1.0, null)

        col.convertToInt() shouldBe columnOf(1, null)

        shouldThrow<TypeConversionException> {
            col.cast<Double>().convertToInt()
        }

        col.convertTo<Int?>() shouldBe columnOf(1, null)
    }

    @Test
    fun `convert to nullable without nulls`() {
        val col = columnOf(1.0, 2.0)

        col.convertTo<Int?>().hasNulls() shouldBe false
    }

    @Test
    fun `convert instant`() {
        println(Clock.System.now().toEpochMilliseconds())
        val kotlinxInstants = columnOf(Instant.fromEpochMilliseconds(1657283006955))
        shouldNotThrow<TypeConverterNotFoundException> {
            val javaInstant = kotlinxInstants.convertTo<java.time.Instant>()
            javaInstant.convertTo<Instant>() shouldBe kotlinxInstants
        }
    }

    @Test
    fun `convert duration to string`() {
        val col = columnOf(1.hours)
        val res = col.convertTo<String>()
        res.print()
    }
}
