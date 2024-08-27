package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.util.ANY
import org.jetbrains.kotlinx.dataframe.util.INT
import org.jetbrains.kotlinx.dataframe.util.NULLABLE_ANY
import org.jetbrains.kotlinx.dataframe.util.NULLABLE_INT
import org.junit.Test

class ExprTests : ColumnsSelectionDslTests() {

    @Test
    fun expr() {
        listOf(
            df.select { age },
            df.select { expr(age.name) { age } },
            df.select { expr { age } named age },
            df.select { mapToColumn(age) { age } },
        ).shouldAllBeEqual()

        df
            .get {
                expr("fibonacci") {
                    if (index() < 2) {
                        1
                    } else {
                        prev()!!.newValue<Int>() + prev()!!.prev()!!.newValue<Int>()
                    }
                }
            }.toList() shouldBe listOf(1, 1, 2, 3, 5, 8, 13)

        df.select {
            expr<_, Int?>(infer = Infer.None) { 1 }.type() shouldBe NULLABLE_INT
            expr<_, Int>(infer = Infer.None) { 1 }.type() shouldBe INT
            expr<_, Any?>(infer = Infer.None) { 1 }.type() shouldBe NULLABLE_ANY

            expr<_, Int?>(infer = Infer.Nulls) { 1 }.type() shouldBe INT
            expr<_, Int>(infer = Infer.Nulls) { 1 }.type() shouldBe INT
            expr<_, Any?>(infer = Infer.Nulls) { 1 }.type() shouldBe ANY

            expr<_, Int?>(infer = Infer.Type) { 1 }.type() shouldBe INT
            expr<_, Int>(infer = Infer.Type) { 1 }.type() shouldBe INT
            expr<_, Any?>(infer = Infer.Type) { 1 }.type() shouldBe INT

            none()
        }
    }
}
