package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.test.TestJdkKind
import org.jetbrains.kotlin.test.backend.BlackBoxCodegenSuppressor
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives.IGNORE_DEXING
import org.jetbrains.kotlin.test.directives.JvmEnvironmentConfigurationDirectives
import org.jetbrains.kotlin.test.model.DependencyKind
import org.jetbrains.kotlin.test.model.FrontendKinds
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.runners.codegen.AbstractFirLightTreeBlackBoxCodegenTest
import org.jetbrains.kotlin.test.services.EnvironmentBasedStandardLibrariesPathProvider
import org.jetbrains.kotlin.test.services.KotlinStandardLibrariesPathProvider
import org.jetbrains.kotlin.test.services.RuntimeClasspathProvider
import org.jetbrains.kotlin.test.services.TemporaryDirectoryManager
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlinx.dataframe.services.TemporaryDirectoryManagerImplFixed
import java.io.File

open class AbstractExplainerBlackBoxCodegenTest : AbstractFirLightTreeBlackBoxCodegenTest() {

    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)
        with(builder) {
            globalDefaults {
                frontend = FrontendKinds.FIR
                targetPlatform = JvmPlatforms.jvm8
                dependencyKind = DependencyKind.Binary
            }
            defaultDirectives {
                JvmEnvironmentConfigurationDirectives.JDK_KIND with TestJdkKind.FULL_JDK
                JvmEnvironmentConfigurationDirectives.JVM_TARGET with JvmTarget.JVM_1_8
                +JvmEnvironmentConfigurationDirectives.WITH_REFLECT
                +IGNORE_DEXING
            }
            useAdditionalService<KotlinStandardLibrariesPathProvider> {
                EnvironmentBasedStandardLibrariesPathProvider
            }
            commonFirWithPluginFrontendConfiguration()
            useConfigurators(::PluginAnnotationsProvider)
            useCustomRuntimeClasspathProviders(::MyClasspathProvider)
            useAfterAnalysisCheckers(::BlackBoxCodegenSuppressor)
            useAdditionalService<TemporaryDirectoryManager>(::TemporaryDirectoryManagerImplFixed)
        }
    }

    class MyClasspathProvider(testServices: TestServices) : RuntimeClasspathProvider(testServices) {
        override fun runtimeClassPaths(module: TestModule): List<File> =
            (classpathFromClassloader(javaClass.classLoader) ?: error("no classpath"))
    }
}
