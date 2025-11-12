package org.jetbrains.kotlinx.dataframe.jupyter

import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createTempDirectory

class ImportDataSchemaOverloadsTest {

    @Test
    fun importDataSchema_constructors_and_functions_produce_same_url() {
        val tmp: Path = createTempDirectory("ids_overloads_")
        try {
            val file = tmp.resolve("schema.json").toFile().also { it.writeText("{}") }
            val path = file.toPath()
            val str = path.toUri().toString() // String-оверлоад ожидает URL/URI-строку

            // constructors
            val c1 = ImportDataSchema(str).url
            val c2 = ImportDataSchema(path).url
            val c3 = ImportDataSchema(file).url

            assertEquals(c1, c2)
            assertEquals(c1, c3)

            // top-level functions
            val f1 = importDataSchema(str).url
            val f2 = importDataSchema(path).url
            val f3 = importDataSchema(file).url

            assertEquals(f1, f2)
            assertEquals(f1, f3)
        } finally {
            Files.walk(tmp).sorted(Comparator.reverseOrder()).forEach { it.toFile().delete() }
        }
    }
}
