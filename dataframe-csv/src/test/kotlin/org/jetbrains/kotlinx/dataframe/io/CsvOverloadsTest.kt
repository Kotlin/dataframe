package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.createTempFile
import kotlin.io.path.deleteExisting
import kotlin.io.path.writeText

class CsvOverloadsTest {

    private fun sampleCsv(): String = buildString {
        appendLine("name,age")
        appendLine("Alice,15")
        appendLine("Bob,20")
    }

    @Test
    fun readCsv_overloads_String_Path_File_produce_same_df() {
        val tmpDir: Path = createTempDirectory("csv_overloads_read_")
        try {
            val csvPath = tmpDir.resolve("people.csv")
            csvPath.writeText(sampleCsv())

            val dfFromString = DataFrame.readCsv(csvPath.toString())
            val dfFromPath = DataFrame.readCsv(csvPath)
            val dfFromFile = DataFrame.readCsv(csvPath.toFile())

            dfFromPath.rowsCount() shouldBe dfFromString.rowsCount()
            dfFromFile.rowsCount() shouldBe dfFromString.rowsCount()
            dfFromPath.columnNames() shouldBe dfFromString.columnNames()
            dfFromFile.columnNames() shouldBe dfFromString.columnNames()
            // compare serialized CSV text to avoid dependency on row order formatters
            dfFromPath.toCsvStr() shouldBe dfFromString.toCsvStr()
            dfFromFile.toCsvStr() shouldBe dfFromString.toCsvStr()
        } finally {
            Files.walk(tmpDir)
                .sorted(Comparator.reverseOrder())
                .forEach { it.toFile().delete() }
        }
    }

    @Test
    fun writeDelim_overloads_String_Path_File_roundtrip() {
        val df = dataFrameOf("name", "age")(
            "Alice", 15,
            "Bob", 20,
        )

        val tmpDir: Path = createTempDirectory("csv_overloads_write_")
        try {
            val pathOut = tmpDir.resolve("out_path.csv")
            val fileOut = tmpDir.resolve("out_file.csv").toFile()
            val strOut = tmpDir.resolve("out_str.csv").toString()

            // write using three overloads
            df.writeDelim(pathOut)
            df.writeDelim(fileOut)
            df.writeDelim(strOut)

            // read back with any overload (use Path for uniformity)
            val readPath = DataFrame.readCsv(pathOut)
            val readFile = DataFrame.readCsv(fileOut)
            val readStr = DataFrame.readCsv(strOut)

            readPath.toCsvStr() shouldBe df.toCsvStr()
            readFile.toCsvStr() shouldBe df.toCsvStr()
            readStr.toCsvStr() shouldBe df.toCsvStr()
        } finally {
            Files.walk(tmpDir)
                .sorted(Comparator.reverseOrder())
                .forEach { it.toFile().delete() }
        }
    }
}
