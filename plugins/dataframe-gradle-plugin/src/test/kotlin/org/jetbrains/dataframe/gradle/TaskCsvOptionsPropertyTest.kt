package org.jetbrains.dataframe.gradle

import io.kotest.matchers.shouldBe
import org.junit.Test

class TaskCsvOptionsPropertyTest {
    @Test
    fun `configure delimiter`() {
        val project = makeProject()
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        val tab = '\t'
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            schema {
                data = "/test/data.csv"
                name = "org.example.Data"
                src = project.projectDir
                csvOptions {
                    delimiter = tab
                }
            }
        }
        project.evaluate()
        (project.tasks.getByName("generateDataFrameData") as GenerateDataSchemaTask).csvOptions.get().delimiter shouldBe tab
    }
}
