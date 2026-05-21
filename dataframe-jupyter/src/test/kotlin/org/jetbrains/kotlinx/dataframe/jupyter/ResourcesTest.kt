package org.jetbrains.kotlinx.dataframe.jupyter

import io.kotest.matchers.shouldNotBe
import java.io.InputStreamReader
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.junit.Test

class ResourcesTest {

    @Test
    fun `resources available`() {
        val res = DataFrame::class.java.getResourceAsStream("/table.html")
        println(InputStreamReader(res).readText())

        res shouldNotBe null
    }
}
