package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createTempDirectory

class ExcelOverloadsTest {

    @Test
    fun writeExcel_overloads_String_Path_File_create_nonempty_files() {
        val df = dataFrameOf("name", "age")(
            "Alice",
            15,
            "Bob",
            20,
        )

        val tmpDir: Path = createTempDirectory("excel_overloads_write_")
        try {
            val pathOut = tmpDir.resolve("out_path.xlsx")
            val fileOut = tmpDir.resolve("out_file.xlsx").toFile()
            val strOut = tmpDir.resolve("out_str.xlsx").toString()

            df.writeExcel(pathOut)
            df.writeExcel(fileOut)
            df.writeExcel(strOut)

            assertTrue(Files.exists(pathOut) && Files.size(pathOut) > 0L)
            assertTrue(Files.exists(fileOut.toPath()) && Files.size(fileOut.toPath()) > 0L)
            val strOutPath = Paths.get(strOut)
            assertTrue(Files.exists(strOutPath) && Files.size(strOutPath) > 0L)
        } finally {
            Files.walk(tmpDir).sorted(Comparator.reverseOrder()).forEach { it.toFile().delete() }
        }
    }
}
