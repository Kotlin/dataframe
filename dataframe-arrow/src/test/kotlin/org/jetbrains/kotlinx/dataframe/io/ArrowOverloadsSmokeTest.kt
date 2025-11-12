package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createTempDirectory

class ArrowOverloadsSmokeTest {

    @Test
    fun feather_overloads_roundtrip() {
        val df = dataFrameOf("name", "age")(
            "Alice",
            15,
            "Bob",
            20,
        )
        val tmp: Path = createTempDirectory("arrow_overloads_")
        try {
            val p = tmp.resolve("people_path.feather")
            val f = tmp.resolve("people_file.feather").toFile()
            val s = tmp.resolve("people_str.feather").toString()

            // write with three overloads
            df.writeArrowFeather(p)
            df.writeArrowFeather(f)
            // no String overload for write; only File/Path are supported

            // read back the SAME file using different overloads
            val r1 = DataFrame.readArrowFeather(p)
            val r2 = DataFrame.readArrowFeather(p.toFile())
            val r3 = DataFrame.readArrowFeather(p.toString())

            r1.rowsCount() shouldBe df.rowsCount()
            r2.rowsCount() shouldBe df.rowsCount()
            r3.rowsCount() shouldBe df.rowsCount()
            r1.columnNames() shouldBe df.columnNames()
            r2.columnNames() shouldBe df.columnNames()
            r3.columnNames() shouldBe df.columnNames()
        } finally {
            Files.walk(tmp).sorted(Comparator.reverseOrder()).forEach { it.toFile().delete() }
        }
    }

    @Test
    fun ipc_overloads_write_and_read() {
        val df = dataFrameOf("x")(
            1,
            2,
        )
        val tmp: Path = createTempDirectory("arrow_overloads_ipc_")
        try {
            val p = tmp.resolve("data_path.arrow")
            val f = tmp.resolve("data_file.arrow").toFile()
            val s = tmp.resolve("data_str.arrow").toString()

            df.writeArrowIPC(p)
            df.writeArrowIPC(f)
            // no String overload for write; only File/Path are supported

            // read back the SAME file using a different overload (String)
            val r = DataFrame.readArrowIPC(p.toString())
            r.rowsCount() shouldBe df.rowsCount()
            r.columnNames() shouldBe df.columnNames()
        } finally {
            Files.walk(tmp).sorted(Comparator.reverseOrder()).forEach { it.toFile().delete() }
        }
    }
}
