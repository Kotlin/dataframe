package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.test.TargetBackend
import org.jetbrains.kotlin.test.TestJdkKind
import org.jetbrains.kotlin.test.backend.BlackBoxCodegenSuppressor
import org.jetbrains.kotlin.test.backend.handlers.IrPrettyKotlinDumpHandler
import org.jetbrains.kotlin.test.backend.handlers.IrTextDumpHandler
import org.jetbrains.kotlin.test.backend.handlers.IrTreeVerifierHandler
import org.jetbrains.kotlin.test.backend.handlers.JvmBoxRunner
import org.jetbrains.kotlin.test.backend.ir.JvmIrBackendFacade
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.builders.classicFrontendHandlersStep
import org.jetbrains.kotlin.test.builders.irHandlersStep
import org.jetbrains.kotlin.test.builders.jvmArtifactsHandlersStep
import org.jetbrains.kotlin.test.builders.psi2IrStep
import org.jetbrains.kotlin.test.directives.JvmEnvironmentConfigurationDirectives
import org.jetbrains.kotlin.test.frontend.classic.ClassicFrontendFacade
import org.jetbrains.kotlin.test.frontend.classic.handlers.ClassicDiagnosticsHandler
import org.jetbrains.kotlin.test.frontend.classic.handlers.DeclarationsDumpHandler
import org.jetbrains.kotlin.test.model.DependencyKind
import org.jetbrains.kotlin.test.model.FrontendKinds
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.RuntimeClasspathProvider
import org.jetbrains.kotlin.test.services.TemporaryDirectoryManager
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.test.services.configuration.CommonEnvironmentConfigurator
import org.jetbrains.kotlin.test.services.configuration.JvmEnvironmentConfigurator
import org.jetbrains.kotlinx.dataframe.services.TemporaryDirectoryManagerImplFixed
import java.io.File

open class AbstractExplainerBlackBoxCodegenTest : BaseTestRunner() {

    override fun configure(builder: TestConfigurationBuilder): Unit =
        with(builder) {
            globalDefaults {
                frontend = FrontendKinds.ClassicAndFIR
                targetPlatform = JvmPlatforms.jvm8
                dependencyKind = DependencyKind.Binary
                targetBackend = TargetBackend.JVM_IR
            }
            defaultDirectives {
                JvmEnvironmentConfigurationDirectives.JDK_KIND with TestJdkKind.FULL_JDK
                JvmEnvironmentConfigurationDirectives.JVM_TARGET with JvmTarget.JVM_1_8
                +JvmEnvironmentConfigurationDirectives.WITH_REFLECT
            }
            facadeStep(::ClassicFrontendFacade)
            commonFirWithPluginFrontendConfiguration()
            classicFrontendHandlersStep {
                useHandlers(
                    ::ClassicDiagnosticsHandler,
                    ::DeclarationsDumpHandler,
                )
            }
            psi2IrStep()
            irHandlersStep {
                useHandlers(
                    ::IrPrettyKotlinDumpHandler,
                    ::IrTextDumpHandler,
                    ::IrTreeVerifierHandler,
                )
            }
            facadeStep(::JvmIrBackendFacade)
            jvmArtifactsHandlersStep {
                useHandlers(::JvmBoxRunner)
            }
            useConfigurators(::JvmEnvironmentConfigurator, ::CommonEnvironmentConfigurator, ::PluginAnnotationsProvider)
            useCustomRuntimeClasspathProviders(::MyClasspathProvider)
            useAfterAnalysisCheckers(::BlackBoxCodegenSuppressor)
            useAdditionalService<TemporaryDirectoryManager>(::TemporaryDirectoryManagerImplFixed)
        }

    class MyClasspathProvider(testServices: TestServices) : RuntimeClasspathProvider(testServices) {
        override fun runtimeClassPaths(module: TestModule): List<File> =
            (classpathFromClassloader(javaClass.classLoader) ?: error("no classpath"))
    }
}
