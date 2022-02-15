package org.jetbrains.dataframe.gradle.taskProperties

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.inspectors.forOne
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.gradle.api.ProjectConfigurationException
import org.jetbrains.dataframe.gradle.GenerateDataSchemaTask
import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension
import org.jetbrains.dataframe.gradle.SchemaGeneratorPlugin
import org.jetbrains.dataframe.gradle.makeProject
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.junit.Test

class TaskSourceSetPropertyTest {
    @Test
    fun `extension sourceSet present in project`() {
        val project = makeProject()
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.plugins.apply("org.jetbrains.kotlin.jvm")
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
        (project.tasks.getByName("generateDataFrame321") as GenerateDataSchemaTask).src.get()
            .shouldBe(project.file("build/generated/dataframe/main1/kotlin/"))
    }

    @Test
    fun `extension sourceSet not present in project`() {
        val project = makeProject()
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.plugins.apply("org.jetbrains.kotlin.jvm")
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
        exception.causes.shouldHaveSize(1)
        exception.causes.forOne { it.message shouldContain "KotlinSourceSet with name 'main1' not found" }
    }

    @Test
    fun `extension sourceSet not specified`() {
        val project = makeProject()
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.plugins.apply("org.jetbrains.kotlin.jvm")
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            schema {
                data = "123"
                name = "org.example.my.321"
            }
        }
        project.evaluate()
        (project.tasks.getByName("generateDataFrame321") as GenerateDataSchemaTask).src.get()
            .shouldBe(project.file("build/generated/dataframe/main/kotlin/"))
    }

    @Test
    fun `choose most specific sourceSet`() {
        val project = makeProject()
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.plugins.apply("org.jetbrains.kotlin.jvm")
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
        (project.tasks.getByName("generateDataFrame321") as GenerateDataSchemaTask).src.get()
            .shouldBe(project.file("build/generated/dataframe/main1/kotlin/"))
    }

    @Test
    fun `extension sourceSet specified but no kotlin plugin found`() {
        val project = makeProject()
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
        exception.causes.shouldHaveSize(1)
        exception.causes.forOne { it.message shouldContain "No supported Kotlin plugin was found. Please apply one or specify property src for schema 321 explicitly" }
    }

    @Test
    fun `task with explicit src don't evaluates sourceSet`() {
        val project = makeProject()
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
        (project.tasks.getByName("generateDataFrame321") as GenerateDataSchemaTask).dataSchema.get()
            .shouldBe(project.file("src/main/kotlin/org/example/my/321.Generated.kt"))
    }

    @Test
    fun `task and extension sourceSet not present in project`() {
        val project = makeProject()
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
        exception.causes.shouldHaveSize(1)
        exception.causes.forOne {
            it.message shouldContain "No supported Kotlin plugin was found. Please apply one or specify property src for schema 321 explicitly"
        }
    }
}
