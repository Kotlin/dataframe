package org.jetbrains.dataframe.gradle

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.shouldNotBe
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.TaskOutcome
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.junit.Test
import java.io.File

class TaskSourceSetPropertyTest {
    @Test
    fun `extension sourceSet present in project`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.plugins.apply(KotlinPlatformJvmPlugin::class.java)
        project.extensions.getByType(KotlinJvmProjectExtension::class.java).apply {
            sourceSets.create("main1")
        }
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            sourceSet = "main1"
            schema {
                data = "123"
                name = "org.example.my.321"
            }
        }
        shouldNotThrow<ProjectConfigurationException> {
            project.evaluate()
        }
        (project.tasks.getByName("generate321") as GenerateDataSchemaTask).src.get()
            .shouldBe(project.file("src/main1/kotlin/"))
    }

    @Test
    fun `extension sourceSet not present in project`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.plugins.apply(KotlinPlatformJvmPlugin::class.java)
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            sourceSet = "main1"
            schema {
                data = "123"
                name = "org.example.my.321"
            }
        }
        val exception = shouldThrow<ProjectConfigurationException> {
            project.evaluate()
        }
        exception.causes.single().message shouldContain "KotlinSourceSet with name 'main1' not found"
    }

    @Test
    fun `extension sourceSet not specified`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.plugins.apply(KotlinPlatformJvmPlugin::class.java)
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            schema {
                data = "123"
                name = "org.example.my.321"
            }
        }
        project.evaluate()
        (project.tasks.getByName("generate321") as GenerateDataSchemaTask).src.get()
            .shouldBe(project.file("src/main/kotlin/"))
    }

    @Test
    fun `choose most specific sourceSet`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.plugins.apply(KotlinPlatformJvmPlugin::class.java)
        project.extensions.getByType(KotlinJvmProjectExtension::class.java).apply {
            sourceSets.create("main1")
        }
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            sourceSet = "main"
            schema {
                sourceSet = "main1"
                data = "123"
                name = "321"
            }
        }
        project.evaluate()
        (project.tasks.getByName("generate321") as GenerateDataSchemaTask).src.get()
            .shouldBe(project.file("src/main1/kotlin/"))
    }

    @Test
    fun `extension sourceSet specified but no kotlin plugin found`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            packageName = "org.example.test"
            sourceSet = "myMain"
            schema {
                data = "123"
                name = "org.example.my.321"
            }
        }
        val exception = shouldThrow<ProjectConfigurationException> {
            project.evaluate()
        }
        exception.causes.single().message shouldContain "No supported Kotlin plugin was found. Please apply one or specify src for task 321 explicitly"
    }

    @Test
    fun `task with explicit src don't evaluates sourceSet`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            packageName = "org.example.test"
            sourceSet = "myMain"
            schema {
                data = "123"
                src = project.file("src/main/kotlin")
                name = "org.example.my.321"
            }
        }
        shouldNotThrow<ProjectConfigurationException> {
            project.evaluate()
        }
        (project.tasks.getByName("generate321") as GenerateDataSchemaTask).dataSchema.get()
            .shouldBe(project.file("src/main/kotlin/org/example/my/Generated321.kt"))
    }

    @Test
    fun `task and extension sourceSet not present in project`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            packageName = "org.example.test"
            sourceSet = "myMain"
            schema {
                data = "123"
                sourceSet = "myMain1"
                name = "org.example.my.321"
            }
        }
        val exception = shouldThrow<ProjectConfigurationException> {
            project.evaluate()
        }
        exception.causes.single().message shouldContain "No supported Kotlin plugin was found. Please apply one or specify src for task 321 explicitly"
    }

    @Test
    fun `src convention is main source set`() {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, "data.csv")
            dataFile.writeText(TestData.csvSample)

            val kotlin = File(buildDir, "src/main/kotlin").also { it.mkdirs() }
            val main = File(kotlin, "Main.kt")
            main.writeText("""
                import org.jetbrains.dataframe.DataFrame
                import org.jetbrains.dataframe.io.read
                import org.jetbrains.dataframe.typed
                import org.jetbrains.dataframe.filter
                
                fun main() {
                    val df = DataFrame.read("$dataFile").typed<Schema>()
                    val df1 = df.filter { age != null }
                }
            """.trimIndent())

            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("jvm") version "1.4.10"
                    id("org.jetbrains.dataframe.schema-generator-base")
                }
                
                repositories {
                    mavenCentral() 
                }
                
                dependencies {
                    implementation("org.jetbrains.kotlinx:dataframe:0.7.3-dev-277-0.10.0.53")
                }
                
                dataframes {
                    schema {
                        data = "$dataFile"
                        name = "Schema"
                        packageName = ""
                    }
                }
            """.trimIndent()
        }
        result.task(":build")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `src convention is jvmMain source set for multiplatform project`() {
        val (_, result) = runGradleBuild(":generateAll") { buildDir ->
            val dataFile = File(buildDir, "data.csv")
            dataFile.writeText(TestData.csvSample)
            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("multiplatform") version "1.4.10"
                    id("org.jetbrains.dataframe.schema-generator-base")
                }
                
                repositories {
                    mavenCentral() 
                }
                
                kotlin {
                    jvm()
                    
                    sourceSets {
                        val jvmMain by getting {
                            dependencies {
                                implementation("org.jetbrains.kotlinx:dataframe:0.7.3-dev-277-0.10.0.53")
                            }
                        }
                    }
                }
                
                dataframes {
                    schema {
                        data = "$dataFile"
                        name = "Schema"
                        packageName = ""
                    }
                }
            """.trimIndent()

        }
        result.task(":generateAll")?.outcome shouldBe TaskOutcome.SUCCESS
    }
}
