package org.jetbrains.dataframe.gradle

import io.kotest.matchers.shouldBe
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.junit.Test

class SourceSetsExpectationsTest {
    @Test
    fun `there is main in default JVM project`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("org.jetbrains.kotlin.jvm")
        project.extensions.getByType(KotlinJvmProjectExtension::class.java).let {
            val main = it.sourceSets.getByName("main")
            assert(main.kotlin.sourceDirectories.any { it.absolutePath.endsWith("/src/main/kotlin") })
        }
    }

    @Test
    fun `there is no jvmMain in default Multiplatform project`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("org.jetbrains.kotlin.multiplatform")
        project.extensions.getByType(KotlinMultiplatformExtension::class.java).let {
            it.sourceSets.findByName("jvmMain") shouldBe null
        }
    }

    @Test
    fun `there is main in android project`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply("com.android.application")
        project.plugins.apply("org.jetbrains.kotlin.android")
        project.extensions.getByType(KotlinAndroidProjectExtension::class.java).let {
            val main = it.sourceSets.getByName("main")
            assert(main.kotlin.sourceDirectories.any { it.absolutePath.endsWith("/src/main/java") })
            assert(main.kotlin.sourceDirectories.any { it.absolutePath.endsWith("/src/main/kotlin") })
        }
    }
}
