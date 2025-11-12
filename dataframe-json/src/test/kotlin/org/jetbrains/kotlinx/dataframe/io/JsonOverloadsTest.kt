package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import kotlin.test.Test
import kotlin.test.assertTrue
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText

class JsonOverloadsTest {

    private fun sampleJson(): String = """
        [
          {"name":"Alice","age":15},
          {"name":"Bob","age":20}
        ]
    """.trimIndent()

    @Test
    fun readJson_overloads_String_Path_File_produce_same_df() {
        val tmpDir: Path = createTempDirectory("json_overloads_read_")
        try {
            val jsonPath = tmpDir.resolve("people.json")
            jsonPath.writeText(sampleJson())

            val dfFromString = DataFrame.readJson(jsonPath.toString())
            val dfFromPath = DataFrame.readJson(jsonPath)
            val dfFromFile = DataFrame.readJson(jsonPath.toFile())

            dfFromPath.rowsCount() shouldBe dfFromString.rowsCount()
            dfFromFile.rowsCount() shouldBe dfFromString.rowsCount()
            dfFromPath.columnNames() shouldBe dfFromString.columnNames()
            dfFromFile.columnNames() shouldBe dfFromString.columnNames()

            // serialize back to JSON (order-insensitive enough for this sample) and compare sizes
            dfFromPath.toJson() shouldBe dfFromString.toJson()
            dfFromFile.toJson() shouldBe dfFromString.toJson()
        } finally {
            Files.walk(tmpDir).sorted(Comparator.reverseOrder()).forEach { it.toFile().delete() }
        }
    }

    @Test
    fun writeJson_overloads_String_Path_File_roundtrip_df_and_row() {
        val df = dataFrameOf("name", "age")(
            "Alice", 15,
            "Bob", 20,
        )
        val row: AnyRow = df[0]

        val tmpDir: Path = createTempDirectory("json_overloads_write_")
        try {
            val pathOut = tmpDir.resolve("out_path.json")
            val fileOut = tmpDir.resolve("out_file.json").toFile()
            val strOut = tmpDir.resolve("out_str.json").toString()

            df.writeJson(pathOut, prettyPrint = true)
            df.writeJson(fileOut, prettyPrint = true)
            df.writeJson(strOut, prettyPrint = true)

            // read back using three overloads
            val readP = DataFrame.readJson(pathOut)
            val readF = DataFrame.readJson(fileOut)
            val readS = DataFrame.readJson(strOut)

            readP.toJson() shouldBe df.toJson()
            readF.toJson() shouldBe df.toJson()
            readS.toJson() shouldBe df.toJson()

            // AnyRow writeJson overloads do not read back; verify they produce files
            row.writeJson(tmpDir.resolve("row_path.json"))
            row.writeJson(tmpDir.resolve("row_file.json").toFile())
            row.writeJson(tmpDir.resolve("row_str.json").toString())

            assertTrue(Files.size(tmpDir.resolve("row_path.json")) > 0L)
            assertTrue(Files.size(tmpDir.resolve("row_file.json")) > 0L)
            assertTrue(Files.size(tmpDir.resolve("row_str.json")) > 0L)
        } finally {
            Files.walk(tmpDir).sorted(Comparator.reverseOrder()).forEach { it.toFile().delete() }
        }
    }
}
