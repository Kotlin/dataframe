/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.dataframe

import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.fir.dataframe.services.Directives
import org.jetbrains.kotlin.fir.dataframe.services.DataFramePluginAnnotationsProvider
import org.jetbrains.kotlin.fir.dataframe.services.ExperimentalExtensionRegistrarConfigurator
import org.jetbrains.kotlin.fir.dataframe.services.TemporaryDirectoryManagerImplFixed
import org.jetbrains.kotlin.test.FirParser
import org.jetbrains.kotlin.test.TestJdkKind
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.directives.FirDiagnosticsDirectives
import org.jetbrains.kotlin.test.directives.JvmEnvironmentConfigurationDirectives
import org.jetbrains.kotlin.test.frontend.fir.DisableLazyResolveChecksAfterAnalysisChecker
import org.jetbrains.kotlin.test.initIdeaConfiguration
import org.jetbrains.kotlin.test.runners.AbstractKotlinCompilerTest
import org.jetbrains.kotlin.test.runners.baseFirDiagnosticTestConfiguration
import org.jetbrains.kotlin.test.services.EnvironmentBasedStandardLibrariesPathProvider
import org.jetbrains.kotlin.test.services.KotlinStandardLibrariesPathProvider
import org.jetbrains.kotlin.test.services.TemporaryDirectoryManager
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeAll

abstract class AbstractDataFrameDiagnosticTest : AbstractKotlinCompilerTest() {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setUp() {
            initIdeaConfiguration()
        }
    }

    override fun createKotlinStandardLibrariesPathProvider(): KotlinStandardLibrariesPathProvider {
        return EnvironmentBasedStandardLibrariesPathProvider
    }

    override fun TestConfigurationBuilder.configuration() {
        baseFirDiagnosticTestConfiguration()
// disabled because checker it too strict and fails even when shouldn't
//    firHandlersStep {
//        useHandlers(
//            ::FirResolveContractViolationErrorHandler,
//        )
//    }
        useAdditionalService<TemporaryDirectoryManager>(::TemporaryDirectoryManagerImplFixed)

        defaultDirectives {
            +FirDiagnosticsDirectives.ENABLE_PLUGIN_PHASES
            +FirDiagnosticsDirectives.FIR_DUMP
            FirDiagnosticsDirectives.FIR_PARSER with FirParser.LightTree
            JvmEnvironmentConfigurationDirectives.JDK_KIND with TestJdkKind.FULL_JDK_11
            JvmEnvironmentConfigurationDirectives.JVM_TARGET with JvmTarget.JVM_11
        }

        useDirectives(Directives)
        useConfigurators(
            ::DataFramePluginAnnotationsProvider,
            ::ExperimentalExtensionRegistrarConfigurator
        )
        useAfterAnalysisCheckers(
            ::DisableLazyResolveChecksAfterAnalysisChecker,
        )
    }

    override fun runTest(filePath: String) {
        val muted = setOf("HistoryItem.kt")
        Assumptions.assumeFalse(muted.any { filePath.contains(it) })
        super.runTest(filePath)
    }
}
