package org.jetbrains.dataframe.gradle.taskProperties

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.gradle.api.ProjectConfigurationException
import org.jetbrains.dataframe.gradle.GenerateDataSchemaTask
import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension
import org.jetbrains.dataframe.gradle.SchemaGeneratorPlugin
import org.jetbrains.dataframe.gradle.makeProject
import org.junit.Test
import java.io.File

class TaskPackageNamePropertyTest {
    @Test
    fun `task inherit default packageName from extension`() {
        val project = makeProject()
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
        (project.tasks.getByName("generateDataFrame321") as GenerateDataSchemaTask).packageName.get() shouldBe "org.example.test"
    }

    @Test
    fun `task packageName overrides packageName from extension`() {
        val project = makeProject()
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
        (project.tasks.getByName("generateDataFrame321") as GenerateDataSchemaTask).packageName.get() shouldBe "org.example.my"
    }

    @Test
    fun `task packageName convention is package part of FQ name`() {
        val project = makeProject()
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
        (project.tasks.findByName("generateDataFrame321") as GenerateDataSchemaTask).packageName.get() shouldBe "org.example.my"
    }

    @Test
    fun `name package part overrides packageName`() {
        val project = makeProject()
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
        (project.tasks.findByName("generateDataFrame321") as GenerateDataSchemaTask).packageName.get() shouldBe "org.example.my"
    }

    @Test
    fun `illegal characters in package part of name cause exception`() {
        val project = makeProject()
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
        val project = makeProject()
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.plugins.apply("org.jetbrains.kotlin.jvm")
        File(project.projectDir, "/src/main/kotlin/org/test/").also { it.mkdirs() }
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            schema {
                data = "123"
                name = "321"
            }
        }
        project.evaluate()
        (project.tasks.getByName("generateDataFrame321") as GenerateDataSchemaTask).packageName.get() shouldBe "org.test.dataframe"
    }

    @Test
    fun `task infers packageName from directory structure on android`() {
        val project = makeProject()
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.plugins.apply("com.android.application")
        project.plugins.apply("org.jetbrains.kotlin.android")
        (project.extensions.getByName("android") as BaseAppModuleExtension).let {
            it.compileSdk = 30
        }
        File(project.projectDir, "/src/main/kotlin/org/test/").also { it.mkdirs() }
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            schema {
                data = "123"
                name = "321"
            }
        }
        project.evaluate()
        (project.tasks.getByName("generateDataFrame321") as GenerateDataSchemaTask).packageName.get() shouldBe "org.test.dataframe"
    }

    @Test
    fun `task will not add _dataframe_ if inferred package ends with _dataframe_`() {
        val project = makeProject()
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.plugins.apply("org.jetbrains.kotlin.jvm")
        File(project.projectDir, "/src/main/kotlin/org/dataframe/").also { it.mkdirs() }
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            schema {
                data = "123"
                name = "321"
            }
        }
        project.evaluate()
        (project.tasks.getByName("generateDataFrame321") as GenerateDataSchemaTask).packageName.get() shouldBe "org.dataframe"
    }
}
