package org.jetbrains.dataframe.gradle

import io.kotest.assertions.throwables.shouldThrow
import org.gradle.api.internal.plugins.PluginApplicationException
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformAndroidPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.junit.Test

class KotlinPluginExpectationsTest {

    @Test
    fun `project can only apply 1 Kotlin plugin 1`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(KotlinPlatformJvmPlugin::class.java)
        shouldThrow<PluginApplicationException> {
            project.plugins.apply(KotlinPlatformAndroidPlugin::class.java)
            project.evaluate()
        }
    }

    @Test
    fun `project can only apply 1 Kotlin plugin 2`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(KotlinMultiplatformPluginWrapper::class.java)
        shouldThrow<PluginApplicationException> {
            project.plugins.apply(KotlinPlatformAndroidPlugin::class.java)
            project.evaluate()
        }
    }

    @Test
    fun `project can only apply 1 Kotlin plugin 3`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(KotlinMultiplatformPluginWrapper::class.java)
        shouldThrow<PluginApplicationException> {
            project.plugins.apply(KotlinPlatformJvmPlugin::class.java)
            project.evaluate()
        }
    }
}
