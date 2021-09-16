package org.jetbrains.dataframe.gradle

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.junit.Test

class TaskDataSchemaPropertyTest {
    @Test
    fun `extension sourceSet present in project`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
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
        (project.tasks.getByName("generateDataFrame321") as GenerateDataSchemaTask).dataSchema.get()
            .shouldBe(project.file("src/main1/kotlin/org/example/my/321.Generated.kt"))
    }

    @Test
    fun `extension sourceSet present in android project`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.plugins.apply("com.android.application")
        project.plugins.apply("org.jetbrains.kotlin.android")
        (project.extensions.getByName("android") as BaseAppModuleExtension).let {
            it.compileSdk = 30
        }
        project.extensions.getByType(KotlinAndroidProjectExtension::class.java).apply {
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
        (project.tasks.getByName("generateDataFrame321") as GenerateDataSchemaTask).dataSchema.get()
            .shouldBe(project.file("src/main1/kotlin/org/example/my/321.Generated.kt"))
    }

}
