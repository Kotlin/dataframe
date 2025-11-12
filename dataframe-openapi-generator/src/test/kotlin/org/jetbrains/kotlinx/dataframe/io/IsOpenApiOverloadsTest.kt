package org.jetbrains.kotlinx.dataframe.io

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText

class IsOpenApiOverloadsTest {

    @Test
    fun isOpenApi_overloads_yaml_json_and_negative() {
        val tmp: Path = createTempDirectory("openapi_overloads_")
        try {
            val yaml = tmp.resolve("api.yml"); yaml.writeText("openapi: 3.0.0\ncomponents:\n  schemas: {}\n")
            val json = tmp.resolve("api.json"); json.writeText("{" + "\"openapi\":\"3.0.0\",\"components\":{\"schemas\":{}}}" )
            val txt = tmp.resolve("not_openapi.txt"); txt.writeText("just text")

            assertTrue(isOpenApi(yaml.toString()))
            assertTrue(isOpenApi(yaml))
            assertTrue(isOpenApi(yaml.toFile()))

            assertTrue(isOpenApi(json.toString()))
            assertTrue(isOpenApi(json))
            assertTrue(isOpenApi(json.toFile()))

            assertFalse(isOpenApi(txt.toString()))
            assertFalse(isOpenApi(txt))
            assertFalse(isOpenApi(txt.toFile()))
        } finally {
            Files.walk(tmp).sorted(Comparator.reverseOrder()).forEach { it.toFile().delete() }
        }
    }
}
