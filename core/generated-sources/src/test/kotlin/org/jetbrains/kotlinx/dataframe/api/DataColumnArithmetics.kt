package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.Test
import java.math.BigDecimal

class DataColumnArithmeticsTests {

    // region Not

    @Test
    fun `not on DataColumn`() {
        !columnOf(true, false, true) shouldBe columnOf(false, true, false)
    }

    @Test
    fun `not on DataColumn with null`() {
        !columnOf(false, null, true) shouldBe columnOf(true, null, false)
    }

    @Test
    fun `not on ColumnAccessor`() {
        val df = dataFrameOf("isClosed")(true, false)
        df.getColumn { !col<Boolean>("isClosed") } shouldBe columnOf(false, true).named("isClosed")
    }

    @Test
    fun `not on ColumnAccessor with null`() {
        val df = dataFrameOf("isClosed")(true, null)
        df.getColumn { !col<Boolean?>("isClosed") } shouldBe columnOf(false, null).named("isClosed")
    }

    // endregion

    // region Plus

    @Test
    fun `plus on DataColumn of Int`() {
        val age = columnOf(1, 2, 3)
        val expectedInt = columnOf(2, 3, 4)
        val expectedDouble = columnOf(2.0, 3.0, 4.0)
        age + 1 shouldBe expectedInt
        1 + age shouldBe expectedInt
        age + 1.0 shouldBe expectedDouble
        1.0 + age shouldBe expectedDouble
    }

    @Test
    fun `plus with null`() {
        val age = columnOf(1, null, 3)
        val expected = columnOf(2, null, 4)
        age + 1 shouldBe expected
        1 + age shouldBe expected
    }

    @Test
    fun `plus on ColumnAccessor of Int`() {
        val df = dataFrameOf("age")(1, 2, 3)
        val expected = columnOf(2, 3, 4).named("age")
        df.getColumn { col<Int>("age") + 1 } shouldBe expected
        df.getColumn { 1 + col<Int>("age") } shouldBe expected
    }

    @Test
    fun `plus on DataColumn of Double`() {
        val distance = columnOf(1.0, 2.0, 3.0)
        val expected = columnOf(2.0, 3.0, 4.0)
        distance + 1 shouldBe expected
        1 + distance shouldBe expected
        distance + 1.0 shouldBe expected
        1.0 + distance shouldBe expected
    }

    @Test
    fun `plus on DataColumn of Long`() {
        val distance = columnOf(1L, 2L, 3L)
        val expected = columnOf(2L, 3L, 4L)
        distance + 1L shouldBe expected
        1L + distance shouldBe expected
    }

    @Test
    fun `plus on DataColumn of BigDecimal`() {
        val transactionAmount = columnOf(1.01, 2.02, 3.03).map { it.toBigDecimal() }
        val expected = columnOf(2.02, 3.03, 4.04).map { it.toBigDecimal() }
        transactionAmount + 1.01.toBigDecimal() shouldBe expected
        1.01.toBigDecimal() + transactionAmount shouldBe expected
    }

    @Test
    fun `plus on DataColumn of BigInteger`() {
        val fileSize = columnOf(1, 2, 3).map { it.toBigInteger() }
        val expected = columnOf(2, 3, 4).map { it.toBigInteger() }
        fileSize + 1.toBigInteger() shouldBe expected
        1.toBigInteger() + fileSize shouldBe expected
    }

    @Test
    fun `plus on DataColumn with String`() {
        val name = columnOf("Alice", "Bob", "Charlie")
        val age = columnOf(1, 2, 3)
        val isStudent = columnOf(true, false, true)
        val weight = columnOf(2.5, 3.5, 4.0)
        val averageGrade = columnOf(8.2, null, 7.4)
        val distance = columnOf(1L, 2L, 3L)
        val fileSize = columnOf(1, 2, 3).map { it.toBigInteger() }
        val transactionAmount = columnOf(1.01, 2.02, 3.03).map { it.toBigDecimal() }

        name + "1" shouldBe columnOf("Alice1", "Bob1", "Charlie1")
        age + "1" shouldBe columnOf("11", "21", "31")
        isStudent + "1" shouldBe columnOf("true1", "false1", "true1")
        weight + "1" shouldBe columnOf("2.51", "3.51", "4.01")
        averageGrade + "1" shouldBe columnOf("8.21", "null1", "7.41")
        distance + "1" shouldBe columnOf("11", "21", "31")
        fileSize + "1" shouldBe columnOf("11", "21", "31")
        transactionAmount + "1" shouldBe columnOf("1.011", "2.021", "3.031")
    }

    @Test
    fun `plus on ColumnAccessor with String`() {
        val df = dataFrameOf("age")(1, 2, 3)
        val expected = columnOf("11", "21", "31").named("age")
        df.getColumn { col<Int>("age") + "1" } shouldBe expected
    }

    // endregion

    // region ColumnMinusNumber

    @Test
    fun `DataColumn of Int minus Int`() {
        val age = columnOf(2, 3, 4)
        val expected = columnOf(1, 2, 3)
        age - 1 shouldBe expected
    }

    @Test
    fun `ColumnAccessor of Int minus Int`() {
        val df = dataFrameOf("age")(2, 3, 4)
        val expected = columnOf(1, 2, 3).named("age")
        df.getColumn { col<Int>("age") - 1 } shouldBe expected
    }

    @Test
    fun `DataColumn of nullable Int minus Int`() {
        val age = columnOf(2, null, 4)
        val expected = columnOf(1, null, 3)
        age - 1 shouldBe expected
    }

    @Test
    fun `DataColumn of Int minus Double`() {
        val age = columnOf(2, 3, 4)
        val expected = columnOf(1.0, 2.0, 3.0)
        age - 1.0 shouldBe expected
    }

    @Test
    fun `DataColumn of Double minus Int`() {
        val weight = columnOf(2.5, 3.5, 4.0)
        val expected = columnOf(1.5, 2.5, 3.0)
        weight - 1 shouldBe expected
    }

    @Test
    fun `DataColumn of Double minus Double`() {
        val weight = columnOf(2.5, 3.5, 4.0)
        val expected = columnOf(1.5, 2.5, 3.0)
        weight - 1.0 shouldBe expected
    }

    @Test
    fun `DataColumn of Long minus Long`() {
        val distance = columnOf(2L, 3L, 4L)
        val expected = columnOf(1L, 2L, 3L)
        distance - 1L shouldBe expected
    }

    @Test
    fun `DataColumn of BigDecimal minus BigDecimal`() {
        val transactionAmount = columnOf(2.02, 3.03, 4.04).map { it.toBigDecimal() }
        val expected = columnOf(1.01, 2.02, 3.03).map { it.toBigDecimal() }
        transactionAmount - 1.01.toBigDecimal() shouldBe expected
    }

    @Test
    fun `DataColumn of BigInteger minus BigInteger`() {
        val fileSize = columnOf(2, 3, 4).map { it.toBigInteger() }
        val expected = columnOf(1, 2, 3).map { it.toBigInteger() }
        fileSize - 1.toBigInteger() shouldBe expected
    }

    // endregion

    // region NumberMinusColumn

    @Test
    fun `Int minus DataColumn of Int`() {
        val age = columnOf(2, 3, 4)
        val expected = columnOf(8, 7, 6)
        10 - age shouldBe expected
    }

    @Test
    fun `Int minus ColumnAccessor of Int`() {
        val df = dataFrameOf("age")(2, 3, 4)
        val expected = columnOf(8, 7, 6).named("age")
        df.getColumn { 10 - col<Int>("age") } shouldBe expected
    }

    @Test
    fun `Int minus DataColumn of nullable Int`() {
        val age = columnOf(2, null, 4)
        val expected = columnOf(8, null, 6)
        10 - age shouldBe expected
    }

    @Test
    fun `Double minus DataColumn of Int`() {
        val age = columnOf(2, 3, 4)
        val expected = columnOf(8.0, 7.0, 6.0)
        10.0 - age shouldBe expected
    }

    @Test
    fun `Int minus DataColumn of Double`() {
        val weight = columnOf(2.5, 3.5, 4.0)
        val expected = columnOf(7.5, 6.5, 6.0)
        10 - weight shouldBe expected
    }

    @Test
    fun `Double minus DataColumn of Double`() {
        val weight = columnOf(2.5, 3.5, 4.0)
        val expected = columnOf(7.5, 6.5, 6.0)
        10.0 - weight shouldBe expected
    }

    @Test
    fun `Long minus DataColumn of Long`() {
        val distance = columnOf(2L, 3L, 4L)
        val expected = columnOf(8L, 7L, 6L)
        10L - distance shouldBe expected
    }

    @Test
    fun `BigDecimal minus DataColumn of BigDecimal`() {
        val transactionAmount = columnOf(2.02, 3.03, 4.04).map { it.toBigDecimal() }
        val expected = columnOf(3.03, 2.02, 1.01).map { it.toBigDecimal() }
        5.05.toBigDecimal() - transactionAmount shouldBe expected
    }

    @Test
    fun `BigInteger minus DataColumn of BigInteger`() {
        val fileSize = columnOf(2, 3, 4).map { it.toBigInteger() }
        val expected = columnOf(8, 7, 6).map { it.toBigInteger() }
        10.toBigInteger() - fileSize shouldBe expected
    }

    // endregion

    // region UnaryMinus

    @Test
    fun `unary minus on DataColumn of Int`() {
        val age = columnOf(1, 2, 3)
        val expected = columnOf(-1, -2, -3)
        -age shouldBe expected
    }

    @Test
    fun `unary minus on ColumnAccessor of Int`() {
        val df = dataFrameOf("age")(1, 2, 3)
        val expected = columnOf(-1, -2, -3).named("age")
        df.getColumn { -col<Int>("age") } shouldBe expected
    }

    @Test
    fun `unary minus on DataColumn of nullable Int`() {
        val age = columnOf(1, null, 3)
        val expected = columnOf(-1, null, -3)
        -age shouldBe expected
    }

    @Test
    fun `unary minus on DataColumn of Double`() {
        val distance = columnOf(1.0, 2.0, 3.0)
        val expected = columnOf(-1.0, -2.0, -3.0)
        -distance shouldBe expected
    }

    @Test
    fun `unary minus on DataColumn of Long`() {
        val distance = columnOf(1L, 2L, 3L)
        val expected = columnOf(-1L, -2L, -3L)
        -distance shouldBe expected
    }

    @Test
    fun `unary minus on DataColumn of BigDecimal`() {
        val transactionAmount = columnOf(1.01, 2.02, 3.03).map { it.toBigDecimal() }
        val expected = columnOf(-1.01, -2.02, -3.03).map { it.toBigDecimal() }
        -transactionAmount shouldBe expected
    }

    @Test
    fun `unary minus on DataColumn of BigInteger`() {
        val fileSize = columnOf(1, 2, 3).map { it.toBigInteger() }
        val expected = columnOf(-1, -2, -3).map { it.toBigInteger() }
        -fileSize shouldBe expected
    }

    // endregion

    // region Times

    @Test
    fun `DataColumn of Int times Int`() {
        val age = columnOf(1, 2, 3)
        val expected = columnOf(2, 4, 6)
        age * 2 shouldBe expected
    }

    @Test
    fun `times on ColumnAccessor of Int`() {
        val df = dataFrameOf("age")(1, 2, 3)
        val expected = columnOf(2, 4, 6).named("age")
        df.getColumn { col<Int>("age") * 2 } shouldBe expected
    }

    @Test
    fun `times on DataColumn of nullable Int`() {
        val age = columnOf(1, null, 3)
        val expected = columnOf(2, null, 6)
        age * 2 shouldBe expected
    }

    @Test
    fun `DataColumn of Int times Double`() {
        val age = columnOf(1, 2, 3)
        val expected = columnOf(2.0, 4.0, 6.0)
        age * 2.0 shouldBe expected
    }

    @Test
    fun `DataColumn of Double times Int`() {
        val distance = columnOf(1.0, 2.0, 3.0)
        val expected = columnOf(2.0, 4.0, 6.0)
        distance * 2 shouldBe expected
    }

    @Test
    fun `DataColumn of Double times Double`() {
        val distance = columnOf(1.0, 2.0, 3.0)
        val expected = columnOf(2.0, 4.0, 6.0)
        distance * 2.0 shouldBe expected
    }

    @Test
    fun `DataColumn of Long times Long`() {
        val distance = columnOf(1L, 2L, 3L)
        val expected = columnOf(2L, 4L, 6L)
        distance * 2L shouldBe expected
    }

    @Test
    fun `DataColumn of BigDecimal times BigDecimal`() {
        val transactionAmount = columnOf(1.01, 2.02, 3.03).map { it.toBigDecimal() }
        // need to use this constructor here to avoid scale loss (when 2.020 becomes 2.02)
        val expected = columnOf(BigDecimal("2.020"), BigDecimal("4.040"), BigDecimal("6.060"))
        transactionAmount * 2.0.toBigDecimal() shouldBe expected
        transactionAmount * 2.toBigDecimal() shouldNotBe expected
    }

    @Test
    fun `DataColumn of BigInteger times BigInteger`() {
        val fileSize = columnOf(1, 2, 3).map { it.toBigInteger() }
        val expected = columnOf(2, 4, 6).map { it.toBigInteger() }
        fileSize * 2.toBigInteger() shouldBe expected
    }

    // endregion

    // region ColumnDivNumber

    @Test
    fun `DataColumn of Int div Int`() {
        val age = columnOf(2, 4, 6)
        val expected = columnOf(1, 2, 3)
        age / 2 shouldBe expected
    }

    @Test
    fun `ColumnAccessor of Int div Int`() {
        val df = dataFrameOf("age")(2, 4, 6)
        val expected = columnOf(1, 2, 3).named("age")
        df.getColumn { col<Int>("age") / 2 } shouldBe expected
    }

    @Test
    fun `DataColumn of nullable Int div Int`() {
        val age = columnOf(2, null, 6)
        val expected = columnOf(1, null, 3)
        age / 2 shouldBe expected
    }

    @Test
    fun `DataColumn of Int div Double`() {
        val age = columnOf(2, 4, 6)
        val expected = columnOf(1.0, 2.0, 3.0)
        age / 2.0 shouldBe expected
    }

    @Test
    fun `DataColumn of Double div Int`() {
        val weight = columnOf(2.5, 3.5, 4.0)
        val expected = columnOf(1.25, 1.75, 2.0)
        weight / 2 shouldBe expected
    }

    @Test
    fun `DataColumn of Double div Double`() {
        val weight = columnOf(2.5, 3.5, 4.0)
        val expected = columnOf(1.25, 1.75, 2.0)
        weight / 2.0 shouldBe expected
    }

    @Test
    fun `DataColumn of Long div Long`() {
        val distance = columnOf(2L, 4L, 6L)
        val expected = columnOf(1L, 2L, 3L)
        distance / 2L shouldBe expected
    }

    @Test
    fun `DataColumn of BigDecimal div BigDecimal`() {
        val transactionAmount = columnOf(2.02, 4.04, 6.06).map { it.toBigDecimal() }
        val expected = columnOf(1.01, 2.02, 3.03).map { it.toBigDecimal() }
        transactionAmount / 2.00.toBigDecimal() shouldBe expected
    }

    @Test
    fun `DataColumn of BigInteger div BigInteger`() {
        val fileSize = columnOf(2, 4, 6).map { it.toBigInteger() }
        val expected = columnOf(1, 2, 3).map { it.toBigInteger() }
        fileSize / 2.toBigInteger() shouldBe expected
    }

    @Test
    fun `DataColumn of Int div zero`() {
        val age = columnOf(2, 4, 6)
        shouldThrow<ArithmeticException> { age / 0 }
        age / 0.0 shouldBe columnOf(
            Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY,
        )
    }

    @Test
    fun `ColumnAccessor of Int div zero`() {
        val df = dataFrameOf("age")(2, 4, 6)
        shouldThrow<ArithmeticException> { df.getColumn { col<Int>("age") / 0 } }
    }

    @Test
    fun `DataColumn of nullable Int div zero`() {
        val age = columnOf(2, null, 6)
        shouldThrow<ArithmeticException> { age / 0 }
    }

    @Test
    fun `DataColumn of Double div zero`() {
        val age = columnOf(2.0, 4.0, 6.0)
        age / 0 shouldBe columnOf(
            Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY,
        )
        age / 0.0 shouldBe columnOf(
            Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY,
        )
    }

    @Test
    fun `DataColumn of Long div zero`() {
        val age = columnOf(2L, 4L, 6L)
        shouldThrow<ArithmeticException> { age / 0L }
    }

    @Test
    fun `DataColumn of BigDecimal div zero`() {
        val age = columnOf(2, 4, 6).map { it.toBigDecimal() }
        shouldThrow<ArithmeticException> { age / 0.toBigDecimal() }
    }

    @Test
    fun `DataColumn of BigInteger div zero`() {
        val age = columnOf(2, 4, 6).map { it.toBigInteger() }
        shouldThrow<ArithmeticException> { age / 0.toBigInteger() }
    }

    // endregion

    // region NumberDivColumn

    @Test
    fun `Int div DataColumn of Int`() {
        val age = columnOf(2, 4, 6)
        val expected = columnOf(6, 3, 2)
        12 / age shouldBe expected
    }

    @Test
    fun `Int div ColumnAccessor of Int`() {
        val df = dataFrameOf("age")(2, 4, 6)
        val expected = columnOf(6, 3, 2).named("age")
        df.getColumn { 12 / col<Int>("age") } shouldBe expected
    }

    @Test
    fun `Int div DataColumn of nullable Int`() {
        val age = columnOf(2, null, 6)
        val expected = columnOf(6, null, 2)
        12 / age shouldBe expected
    }

    @Test
    fun `Double div DataColumn of Int`() {
        val age = columnOf(2, 4, 6)
        val expected = columnOf(6.0, 3.0, 2.0)
        12.0 / age shouldBe expected
    }

    @Test
    fun `Int div DataColumn of Double`() {
        val weight = columnOf(2.0, 4.0, 6.0)
        val expected = columnOf(6.0, 3.0, 2.0)
        12 / weight shouldBe expected
    }

    @Test
    fun `Double div DataColumn of Double`() {
        val weight = columnOf(2.0, 4.0, 6.0)
        val expected = columnOf(6.0, 3.0, 2.0)
        12.0 / weight shouldBe expected
    }

    @Test
    fun `Long div DataColumn of Long`() {
        val distance = columnOf(2L, 4L, 6L)
        val expected = columnOf(6L, 3L, 2L)
        12L / distance shouldBe expected
    }

    @Test
    fun `BigDecimal div DataColumn of BigDecimal`() {
        val transactionAmount = columnOf(2.00, 4.00, 6.00).map { it.toBigDecimal() }
        val expected = columnOf(6.06, 3.03, 2.02).map { it.toBigDecimal() }
        12.12.toBigDecimal() / transactionAmount shouldBe expected
    }

    @Test
    fun `BigInteger div DataColumn of BigInteger`() {
        val fileSize = columnOf(2, 4, 6).map { it.toBigInteger() }
        val expected = columnOf(6, 3, 2).map { it.toBigInteger() }
        12.toBigInteger() / fileSize shouldBe expected
    }

    @Test
    fun `Int div DataColumn of Int with zero`() {
        val age = columnOf(2, 0, 6)
        shouldThrow<ArithmeticException> { 10 / age }
    }

    @Test
    fun `Int div ColumnAccessor with zero`() {
        val df = dataFrameOf("age")(2, 0, 6)
        shouldThrow<ArithmeticException> { df.getColumn { 10 / col<Int>("age") } }
    }

    @Test
    fun `Int div DataColumn of nullable Int with zero`() {
        val age = columnOf(2, null, 0)
        shouldThrow<ArithmeticException> { 10 / age }
    }

    @Test
    fun `Double div DataColumn of Int with zero`() {
        val age = columnOf(2, 0, 5)
        10.0 / age shouldBe columnOf(5.0, Double.POSITIVE_INFINITY, 2.0)
    }

    @Test
    fun `Int div DataColumn of Double with zero`() {
        val weight = columnOf(2.0, 0.0, 5.0)
        10 / weight shouldBe columnOf(5.0, Double.POSITIVE_INFINITY, 2.0)
    }

    @Test
    fun `Double div DataColumn of Double with zero`() {
        val weight = columnOf(2.0, 0.0, 5.0)
        10.0 / weight shouldBe columnOf(5.0, Double.POSITIVE_INFINITY, 2.0)
    }

    @Test
    fun `Long div DataColumn of Long with zero`() {
        val distance = columnOf(2L, 0L, 5L)
        shouldThrow<ArithmeticException> { 10L / distance }
    }

    @Test
    fun `BigDecimal div DataColumn of BigDecimal with zero`() {
        val transactionAmount = columnOf(2.02, 0.00, 5.02).map { it.toBigDecimal() }
        shouldThrow<ArithmeticException> { 10.12.toBigDecimal() / transactionAmount }
    }

    @Test
    fun `BigInteger div DataColumn of BigInteger with zero`() {
        val fileSize = columnOf(2, 0, 5).map { it.toBigInteger() }
        shouldThrow<ArithmeticException> { 10.toBigInteger() / fileSize }
    }

    // endregion

    // region Compare

    @Test
    fun `eq on DataColumn`() {
        val name = columnOf("Alice", "Bob", "Charlie")
        val age = columnOf(1, 2, 3)
        val isStudent = columnOf(true, false, true)
        val weight = columnOf(2.5, 3.5, 4.0)
        val averageGrade = columnOf(8.2, null, 7.4)
        val distance = columnOf(1L, 2L, 3L)
        val transactionAmount = columnOf(1.01, 2.02, 3.03).map { it.toBigDecimal() }
        val fileSize = columnOf(1, 2, 3).map { it.toBigInteger() }

        name eq "Alice" shouldBe columnOf(true, false, false)
        name eq 1 shouldBe columnOf(false, false, false)
        age eq 2 shouldBe columnOf(false, true, false)
        age eq "1" shouldBe columnOf(false, false, false)
        isStudent eq false shouldBe columnOf(false, true, false)
        isStudent eq 2.5 shouldBe columnOf(false, false, false)
        weight eq 4.0 shouldBe columnOf(false, false, true)
        weight eq null shouldBe columnOf(false, false, false)
        averageGrade eq 8.2 shouldBe columnOf(true, false, false)
        averageGrade eq null shouldBe columnOf(false, true, false)
        averageGrade eq 8 shouldBe columnOf(false, false, false)
        distance eq 2L shouldBe columnOf(false, true, false)
        distance eq 3.0 shouldBe columnOf(false, false, false)
        transactionAmount eq 1.01.toBigDecimal() shouldBe columnOf(true, false, false)
        transactionAmount eq 1.01 shouldBe columnOf(false, false, false)
        fileSize eq 3.toBigInteger() shouldBe columnOf(false, false, true)
        fileSize eq 3 shouldBe columnOf(false, false, false)
    }

    @Test
    fun `neq on DataColumn`() {
        val name = columnOf("Alice", "Bob", "Charlie")
        val age = columnOf(1, 2, 3)
        val isStudent = columnOf(true, false, true)
        val weight = columnOf(2.5, 3.5, 4.0)
        val averageGrade = columnOf(8.2, null, 7.4)
        val distance = columnOf(1L, 2L, 3L)
        val transactionAmount = columnOf(1.01, 2.02, 3.03).map { it.toBigDecimal() }
        val fileSize = columnOf(1, 2, 3).map { it.toBigInteger() }

        name neq "Alice" shouldBe columnOf(false, true, true)
        name neq 1 shouldBe columnOf(true, true, true)
        age neq 2 shouldBe columnOf(true, false, true)
        age neq "1" shouldBe columnOf(true, true, true)
        isStudent neq false shouldBe columnOf(true, false, true)
        isStudent neq 2.5 shouldBe columnOf(true, true, true)
        weight neq 4.0 shouldBe columnOf(true, true, false)
        weight neq null shouldBe columnOf(true, true, true)
        averageGrade neq 8.2 shouldBe columnOf(false, true, true)
        averageGrade neq null shouldBe columnOf(true, false, true)
        averageGrade neq 8 shouldBe columnOf(true, true, true)
        distance neq 2L shouldBe columnOf(true, false, true)
        distance neq 3.0 shouldBe columnOf(true, true, true)
        transactionAmount neq 1.01.toBigDecimal() shouldBe columnOf(false, true, true)
        transactionAmount neq 1.01 shouldBe columnOf(true, true, true)
        fileSize neq 3.toBigInteger() shouldBe columnOf(true, true, false)
        fileSize neq 3 shouldBe columnOf(true, true, true)
    }

    @Test
    fun `gt on DataColumn`() {
        val name = columnOf("Alice", "Bob", "Charlie")
        val age = columnOf(1, 2, 3)
        val isStudent = columnOf(true, false, true)
        val weight = columnOf(2.5, 3.5, 4.0)
        val distance = columnOf(1L, 2L, 3L)
        val transactionAmount = columnOf(1.01, 2.02, 3.03).map { it.toBigDecimal() }
        val fileSize = columnOf(1, 2, 3).map { it.toBigInteger() }

        name gt "Alice" shouldBe columnOf(false, true, true)
        age gt 2 shouldBe columnOf(false, false, true)
        isStudent gt false shouldBe columnOf(true, false, true)
        weight gt 3.0 shouldBe columnOf(false, true, true)
        distance gt 2L shouldBe columnOf(false, false, true)
        transactionAmount gt 1.01.toBigDecimal() shouldBe columnOf(false, true, true)
        fileSize gt 2.toBigInteger() shouldBe columnOf(false, false, true)
    }

    @Test
    fun `lt on DataColumn`() {
        val name = columnOf("Alice", "Bob", "Charlie")
        val age = columnOf(1, 2, 3)
        val isStudent = columnOf(true, false, true)
        val weight = columnOf(2.5, 3.5, 4.0)
        val distance = columnOf(1L, 2L, 3L)
        val transactionAmount = columnOf(1.01, 2.02, 3.03).map { it.toBigDecimal() }
        val fileSize = columnOf(1, 2, 3).map { it.toBigInteger() }

        name lt "Bob" shouldBe columnOf(true, false, false)
        age lt 2 shouldBe columnOf(true, false, false)
        isStudent lt false shouldBe columnOf(false, false, false)
        weight lt 3.0 shouldBe columnOf(true, false, false)
        distance lt 2L shouldBe columnOf(true, false, false)
        transactionAmount lt 2.05.toBigDecimal() shouldBe columnOf(true, true, false)
        fileSize lt 2.toBigInteger() shouldBe columnOf(true, false, false)
    }

    // endregion
}
