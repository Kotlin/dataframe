package org.jetbrains.dataframe.gradle

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.UnknownConfigurationException
import org.gradle.kotlin.dsl.getByType
import java.util.*

@Suppress("unused")
class ConvenienceSchemaGeneratorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val name = "kotlin.dataframe.add.ksp"
        val property = target.findProperty(name)?.toString()
        var addKsp = true

        if (property != null) {
            if (property.equals("true", ignoreCase = true) || property.equals("false", ignoreCase = true)) {
                addKsp = property.toBoolean()
            } else {
                target.logger.warn("Invalid value '$property' for '$name' property. Defaulting to '$addKsp'. Please use 'true' or 'false'.")
            }
        }

        val properties = Properties()
        properties.load(javaClass.getResourceAsStream("plugin.properties"))
        val preprocessorVersion = properties.getProperty("PREPROCESSOR_VERSION")

        // regardless whether we add KSP or the user adds it, when it's added,
        // configure it to depend on symbol-processor-all
        target.plugins.whenPluginAdded {
            if ("com.google.devtools.ksp" in this.javaClass.packageName) {
                val isMultiplatform =
                    when {
                        target.plugins.hasPlugin("org.jetbrains.kotlin.jvm") -> false
                        target.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") -> true
                        else -> throw Exception("Kotlin plugin must be applied first so we know whether to use multiplatform configurations or not")
                    }
                val mainKspCfg = if (isMultiplatform) "kspJvm" else "ksp"
                val testKspCfg = if (isMultiplatform) "kspJvmTest" else "kspTest"
                try {
                    target.configurations.getByName(mainKspCfg).dependencies.add(
                        target.dependencies.create("org.jetbrains.kotlinx.dataframe:symbol-processor-all:$preprocessorVersion")
                    )
                } catch (e: UnknownConfigurationException) {
                    target.logger.warn("Configuration '$mainKspCfg' not found. Please make sure the KSP plugin is applied.")
                }
                try {
                    target.configurations.getByName(testKspCfg).dependencies.add(
                        target.dependencies.create("org.jetbrains.kotlinx.dataframe:symbol-processor-all:$preprocessorVersion")
                    )
                } catch (e: UnknownConfigurationException) {
                    target.logger.warn("Configuration '$testKspCfg' not found. Please make sure the KSP plugin is applied.")
                }
                target.logger.info("Added DataFrame dependency to the KSP plugin.")
                target.extensions.getByType<KspExtension>().arg("dataframe.resolutionDir", target.projectDir.absolutePath)
            }
        }

        if (addKsp) {
            target.plugins.apply(KspPluginApplier::class.java)
        } else {
            target.logger.warn(
                "Plugin 'org.jetbrains.kotlinx.dataframe' comes bundled with its own version of KSP which is " +
                    "currently disabled as 'kotlin.dataframe.add.ksp' is set to 'false' in a 'properties' file. " +
                    "Either set 'kotlin.dataframe.add.ksp' to 'true' or add the plugin 'com.google.devtools.ksp' " +
                    "manually."
            )
        }
        target.plugins.apply(SchemaGeneratorPlugin::class.java)
    }
}

@Suppress("unused")
class DeprecatingSchemaGeneratorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.logger.warn("DEPRECATION: Replace plugin id(\"org.jetbrains.kotlin.plugin.dataframe\") and kotlin(\"plugin.dataframe\") with id(\"org.jetbrains.kotlinx.dataframe\").")
        target.plugins.apply(ConvenienceSchemaGeneratorPlugin::class.java)
    }
}

/**
 * Applies the KSP plugin in the target project.
 */
internal class KspPluginApplier : Plugin<Project> {
    override fun apply(target: Project) {
        val properties = Properties()
        properties.load(javaClass.getResourceAsStream("plugin.properties"))
        target.plugins.apply("com.google.devtools.ksp")
    }
}
