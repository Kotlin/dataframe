package org.jetbrains.dataframe.gradle

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.shouldNotBe
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Test
import java.io.File

class TaskNamePropertyTest {
    @Test
    fun `task name is last part of FQ name`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            schema {
                data = "123"
                name = "org.example.my.321"
                src = project.projectDir
            }
        }
        project.evaluate()
        (project.tasks.findByName("generate321") shouldNotBe null)
    }

    @Test
    fun `name from task property have higher priority then inferred from data`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            schema {
                data = "/test/data.json"
                name = "org.example.my.321"
                src = project.projectDir
            }
        }
        project.evaluate()
        (project.tasks.findByName("generate321") shouldNotBe null)
    }

    @Test
    fun `task name contains invalid characters`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            schema {
                data = "123"
                name = "org.test.example.[321]"
                src = project.projectDir
            }
        }
        val exception = shouldThrow<ProjectConfigurationException> {
            project.evaluate()
        }
        exception.causes.single().message shouldContain "[321] contains illegal characters: [,]"
    }

    @Test
    fun `name convention is data file name`() {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, "data.csv")
            dataFile.writeText(TestData.csvSample)

            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("jvm") version "1.4.10"
                    id("org.jetbrains.dataframe.schema-generator")
                }
                
                repositories {
                    mavenCentral() 
                }
                
                dependencies {
                    implementation("org.jetbrains.kotlinx:dataframe:0.7.3-dev-277-0.10.0.53")
                }
                
                schemaGenerator {
                    schema {
                        data = "$dataFile"
                        packageName = ""
                    }
                }
            """.trimIndent()
        }
        result.task(":generateData")?.outcome shouldBe TaskOutcome.SUCCESS
    }
}
