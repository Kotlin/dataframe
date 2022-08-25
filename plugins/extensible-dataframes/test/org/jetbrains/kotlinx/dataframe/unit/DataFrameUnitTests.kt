/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("IncorrectFormatting")

package org.jetbrains.kotlinx.dataframe.runners

import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.extensions.FirExpressionResolutionExtension
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.directives.FirDiagnosticsDirectives.ENABLE_PLUGIN_PHASES
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.runners.baseFirDiagnosticTestConfiguration
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.TestServices
import org.junit.jupiter.api.Test

abstract class DataFrameUnitTests(val assertion: List<(FirSession, FirFunctionCall) -> Unit>) : BaseTestRunner() {

    constructor(assertion: (FirSession, FirFunctionCall) -> Unit): this(listOf(assertion))

    override fun TestConfigurationBuilder.configuration() {
        baseFirDiagnosticTestConfiguration()
        defaultDirectives {
            +ENABLE_PLUGIN_PHASES
        }

        class Subject(testServices: TestServices) : EnvironmentConfigurator(testServices) {
            override fun CompilerPluginRegistrar.ExtensionStorage.registerCompilerExtensions(
                module: TestModule,
                configuration: CompilerConfiguration
            ) {
                FirExtensionRegistrarAdapter.registerExtension(object : FirExtensionRegistrar() {
                    override fun ExtensionRegistrarContext.configurePlugin() {
                        +{ it: FirSession -> object : FirExpressionResolutionExtension(it) {
                            var i = 0
                            override fun addNewImplicitReceivers(functionCall: FirFunctionCall): List<ConeKotlinType> {
                                if (assertion.size == 1) {
                                    assertion[0](it, functionCall)
                                } else {
                                    assertion[i](it, functionCall)
                                    i++
                                }
                                return emptyList()
                            }
                        }
                        }
                    }
                })
            }
        }
        useConfigurators(
            ::Subject,
            ::DataFramePluginAnnotationsProvider
        )
    }
}

class DataFramePluginAnnotationsProvider(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    companion object {
        const val ANNOTATIONS_JAR =
            "plugins/kotlin-dataframe/plugin-annotations/build/libs/plugin-annotations-1.6.255-SNAPSHOT.jar"
    }

    override fun configureCompilerConfiguration(configuration: CompilerConfiguration, module: TestModule) {
//        val jar = File(ANNOTATIONS_JAR)
//        testServices.assertions.assertTrue(jar.exists()) { "Jar with annotations does not exist. Please run :plugins:kotlin-dataframe:plugin-annotations:jar" }
//        configuration.addJvmClasspathRoots(jar)
        configuration.addJvmClasspathRoots(classpathFromClassloader(javaClass.classLoader) ?: error("no classpath"))
    }
}

@Suppress("IncorrectFormatting")
class Test1 : DataFrameUnitTests({ session, functionCall ->
//    KotlinTypeFacadeImpl(this).fff()
//     val annotation = assertNotNull(functionCall.functionSymbol().annotations.getOrNull(0))
//     val name = annotation.typeRef.coneTypeSafe<ConeClassLikeType>()?.classId?.shortClassName
//    assertTrue { name!!.identifier == "A" }
 }) {

    @Test
    fun test() {
        runTest("testData/diagnostics/dummy.kt")
    }
}

