package org.jetbrains.dataframe.gradle

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.TaskOutcome
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformAndroidPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.junit.Test
import java.io.File

class TaskPackageNamePropertyTest {
    @Test
    fun `task inherit default packageName from extension`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            packageName = "org.example.test"
            schema {
                data = "123"
                name = "321"
                src = project.projectDir
            }
        }
        project.evaluate()
        (project.tasks.getByName("generate321") as GenerateDataSchemaTask).packageName.get() shouldBe "org.example.test"
    }

    @Test
    fun `task packageName overrides packageName from extension`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            packageName = "org.example.test"
            schema {
                data = "123"
                packageName = "org.example.my"
                name = "321"
                src = project.projectDir
            }
        }
        project.evaluate()
        (project.tasks.getByName("generate321") as GenerateDataSchemaTask).packageName.get() shouldBe "org.example.my"
    }

    @Test
    fun `task packageName convention is package part of FQ name`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            packageName = "org.example.test"
            schema {
                data = "123"
                name = "org.example.my.321"
                src = project.projectDir
            }
        }
        project.evaluate()
        (project.tasks.findByName("generate321") as GenerateDataSchemaTask).packageName.get() shouldBe "org.example.my"
    }

    @Test
    fun `name package part overrides packageName`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            schema {
                data = "123"
                packageName = "org.example.test"
                name = "org.example.my.321"
                src = project.projectDir
            }
        }
        project.evaluate()
        (project.tasks.findByName("generate321") as GenerateDataSchemaTask).packageName.get() shouldBe "org.example.my"
    }

    @Test
    fun `illegal characters in package part of name cause exception`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            schema {
                data = "123"
                name = "`[org]`.321"
            }
        }
        shouldThrow<ProjectConfigurationException> {
            project.evaluate()
        }
    }

    @Test
    fun `task infers packageName from directory structure`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.plugins.apply(KotlinPlatformJvmPlugin::class.java)
        File(project.projectDir, "/src/main/kotlin/org/test/").also { it.mkdirs() }
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            schema {
                data = "123"
                name = "321"
            }
        }
        project.evaluate()
        (project.tasks.getByName("generate321") as GenerateDataSchemaTask).packageName.get() shouldBe "org.test.dataframe"
    }

    @Test
    fun `task infers packageName from directory structure on android`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.plugins.apply("com.android.application")
        project.plugins.apply(KotlinPlatformAndroidPlugin::class.java)
        (project.extensions.getByName("android") as BaseAppModuleExtension).let {
            it.compileSdk = 30
        }
        File(project.projectDir, "/src/main/java/org/test/").also { it.mkdirs() }
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            schema {
                data = "123"
                name = "321"
            }
        }
        project.evaluate()
        (project.tasks.getByName("generate321") as GenerateDataSchemaTask).packageName.get() shouldBe "org.test.dataframe"
    }

    @Test
    fun `task won't add "dataframe" if inferred package ends with "dataframe"`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.plugins.apply(KotlinPlatformJvmPlugin::class.java)
        File(project.projectDir, "/src/main/kotlin/org/dataframe/").also { it.mkdirs() }
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            schema {
                data = "123"
                name = "321"
            }
        }
        project.evaluate()
        (project.tasks.getByName("generate321") as GenerateDataSchemaTask).packageName.get() shouldBe "org.dataframe"
    }

    @Test
    fun `packageName convention is 'dataframe'`() {
        val (dir, result) = runGradleBuild(":build") { buildDir ->
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
                        name = "Data"
                    }
                }
            """.trimIndent()
        }
        result.task(":generateData")?.outcome shouldBe TaskOutcome.SUCCESS
        File(dir, "src/main/kotlin/dataframe/GeneratedData.kt").exists() shouldBe true
    }
}
