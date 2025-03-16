/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.dataframe

import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.fir.dataframe.services.DataFramePluginAnnotationsProvider
import org.jetbrains.kotlin.fir.dataframe.services.ExperimentalExtensionRegistrarConfigurator
import org.jetbrains.kotlin.fir.dataframe.services.TemporaryDirectoryManagerImplFixed
import org.jetbrains.kotlin.fir.dataframe.services.classpath.classpathFromClassloader
import org.jetbrains.kotlin.test.TestJdkKind
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives.IGNORE_DEXING
import org.jetbrains.kotlin.test.directives.JvmEnvironmentConfigurationDirectives
import org.jetbrains.kotlin.test.directives.model.RegisteredDirectives
import org.jetbrains.kotlin.test.initIdeaConfiguration
import org.jetbrains.kotlin.test.model.TestFile
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.runners.codegen.AbstractFirLightTreeBlackBoxCodegenTest
import org.jetbrains.kotlin.test.services.AdditionalSourceProvider
import org.jetbrains.kotlin.test.services.EnvironmentBasedStandardLibrariesPathProvider
import org.jetbrains.kotlin.test.services.KotlinStandardLibrariesPathProvider
import org.jetbrains.kotlin.test.services.RuntimeClasspathProvider
import org.jetbrains.kotlin.test.services.TemporaryDirectoryManager
import org.jetbrains.kotlin.test.services.TestServices
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeAll
import java.io.File

open class AbstractDataFrameBlackBoxCodegenTest : AbstractFirLightTreeBlackBoxCodegenTest() {
    companion object {
        @BeforeAll
        @JvmStatic
        fun setUp() {
            initIdeaConfiguration()
        }
    }

    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)
        builder.defaultDirectives {
            JvmEnvironmentConfigurationDirectives.JDK_KIND with TestJdkKind.FULL_JDK
            JvmEnvironmentConfigurationDirectives.JVM_TARGET with JvmTarget.JVM_1_8
            +JvmEnvironmentConfigurationDirectives.WITH_REFLECT
            +IGNORE_DEXING
        }
        builder.forTestsMatching("*/csDsl/*") {
            builder.useAdditionalSourceProviders(::SelectionDslUtilsSourceProvider)
        }
        builder.useAdditionalService<TemporaryDirectoryManager>(::TemporaryDirectoryManagerImplFixed)
        builder.useConfigurators(::DataFramePluginAnnotationsProvider)
        builder.useConfigurators(::ExperimentalExtensionRegistrarConfigurator)
        builder.useCustomRuntimeClasspathProviders(::MyClasspathProvider)
        builder.useAdditionalSourceProviders(::TestUtilsSourceProvider)
    }

    override fun runTest(filePath: String) {
        val muted = setOf("main.kt", "readJson.kt")
        Assumptions.assumeFalse(muted.any { filePath.contains(it) })
        super.runTest(filePath)
    }

    class MyClasspathProvider(testServices: TestServices) : RuntimeClasspathProvider(testServices) {
        override fun runtimeClassPaths(module: TestModule): List<File> {
            return (classpathFromClassloader(javaClass.classLoader) ?: error("no classpath"))
        }
    }

    override fun createKotlinStandardLibrariesPathProvider(): KotlinStandardLibrariesPathProvider {
        return EnvironmentBasedStandardLibrariesPathProvider
    }

    class TestUtilsSourceProvider(testServices: TestServices) : AdditionalSourceProvider(testServices) {
        companion object {
            const val COMMON_SOURCE_PATH = "testData/testUtils.kt"
        }

        override fun produceAdditionalFiles(globalDirectives: RegisteredDirectives, module: TestModule): List<TestFile> {
            return listOf(File(COMMON_SOURCE_PATH).toTestFile())
        }
    }

    class SelectionDslUtilsSourceProvider(testServices: TestServices) : AdditionalSourceProvider(testServices) {
        companion object {
            const val SELECTION_DSL_UTILS = "testData/selectionDslTestUtils.kt"
        }

        override fun produceAdditionalFiles(globalDirectives: RegisteredDirectives, module: TestModule): List<TestFile> {
            return listOf(File(SELECTION_DSL_UTILS).toTestFile())
        }
    }
}

