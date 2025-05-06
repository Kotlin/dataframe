package org.jetbrains.kotlinx.dataframe.testSets.animals

import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.transpose
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.value
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.api.withNull
import org.junit.Test
import kotlin.reflect.typeOf

class AnimalsTests {

    val animal by columnOf("cat", "cat", "snake", "dog", "dog", "cat", "snake", "cat", "dog", "dog")
    val age by columnOf(2.5, 3.0, 0.5, Double.NaN, 5.0, 2.0, 4.5, Double.NaN, 7.0, 3.0)
    val visits by columnOf(1, 3, 2, 3, 2, 3, 1, 1, 2, 1)
    val priority by columnOf("yes", "yes", "no", "yes", "no", "no", "no", "yes", "no", "no")

    val df = dataFrameOf(animal, age, visits, priority)

    @Test
    fun `ignore nans`() {
        df.mean("age", skipNaN = true) shouldBe 3.4375
    }

    @Test
    fun `mean transpose`() {
        val mean = df.mean().transpose()
        mean.columnsCount() shouldBe 2
        mean.rowsCount() shouldBe 2
        mean.name.values() shouldBe listOf("age", "visits")
        mean.value.type() shouldBe typeOf<Double>()
    }

    @Test
    fun `mean of empty`() {
        val cleared = df
            .update { age }.with { Double.NaN }
            .update { visits }.withNull()
        val mean = cleared.mean()
        mean[age].shouldBeNaN()
        (mean[visits.name()] as Double).shouldBeNaN()
    }
}
