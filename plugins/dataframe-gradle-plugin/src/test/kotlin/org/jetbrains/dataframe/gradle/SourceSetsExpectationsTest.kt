package org.jetbrains.dataframe.gradle

import io.kotest.assertions.asClue
import io.kotest.inspectors.forAny
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.junit.Test

class SourceSetsExpectationsTest {
    @Test
    fun `there is main in default JVM project`() {
        val project = makeProject()
        project.plugins.apply("org.jetbrains.kotlin.jvm")
        project.extensions.getByType(KotlinJvmProjectExtension::class.java).let { extension ->
            val main = extension.sourceSets.getByName("main")
            main.kotlin.sourceDirectories.toList().forAny {
                it.shouldEndWith("src", "main", "kotlin")
            }
        }
    }

    @Test
    fun `there is no jvmMain in default Multiplatform project`() {
        val project = makeProject()
        project.plugins.apply("org.jetbrains.kotlin.multiplatform")
        project.extensions.getByType(KotlinMultiplatformExtension::class.java).let {
            it.sourceSets.findByName("jvmMain") shouldBe null
        }
    }

    @Test
    fun `there is main in android project`() {
        val project = makeProject()
        project.plugins.apply("com.android.application")
        project.plugins.apply("org.jetbrains.kotlin.android")
        project.extensions.getByType(KotlinAndroidProjectExtension::class.java).let { extension ->
            val main = extension.sourceSets.getByName("main")
            main.kotlin.sourceDirectories.toList().asClue { files ->
                files.forAny { it.shouldEndWith("src", "main", "java") }
                files.forAny { it.shouldEndWith("src", "main", "kotlin") }
            }
        }
    }
}
