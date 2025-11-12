package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText

class GuessOverloadsTest {

    private fun sampleJson(): String =
        """
        [
          {"name":"Alice","age":15},
          {"name":"Bob","age":20}
        ]
        """.trimIndent()

    @Test
    fun read_guess_overloads_String_Path_File_produce_same_df() {
        val tmp: Path = createTempDirectory("guess_overloads_")
        try {
            val p = tmp.resolve("people.json")
            p.writeText(sampleJson())

            val d1 = DataFrame.read(p.toString())
            val d2 = DataFrame.read(p)
            val d3 = DataFrame.read(p.toFile())

            d2.rowsCount() shouldBe d1.rowsCount()
            d3.rowsCount() shouldBe d1.rowsCount()
            d2.columnNames() shouldBe d1.columnNames()
            d3.columnNames() shouldBe d1.columnNames()
            d2.toJson() shouldBe d1.toJson()
            d3.toJson() shouldBe d1.toJson()
        } finally {
            Files.walk(tmp).sorted(Comparator.reverseOrder()).forEach { it.toFile().delete() }
        }
    }
}
