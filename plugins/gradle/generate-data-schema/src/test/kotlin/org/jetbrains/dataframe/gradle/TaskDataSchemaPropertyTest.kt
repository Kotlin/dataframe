package org.jetbrains.dataframe.gradle

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.junit.Test

class TaskDataSchemaPropertyTest {
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
        (project.tasks.getByName("generate321") as GenerateDataSchemaTask).dataSchema.get()
            .shouldBe(project.file("src/main1/kotlin/org/example/my/Generated321.kt"))
    }
}
