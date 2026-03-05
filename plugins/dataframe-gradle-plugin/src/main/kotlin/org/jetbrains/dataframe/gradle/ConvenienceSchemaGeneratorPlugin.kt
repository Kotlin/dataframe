package org.jetbrains.dataframe.gradle

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import java.util.Properties

@Suppress("unused")
public class ConvenienceSchemaGeneratorPlugin : Plugin<Project> {
    public companion object {
        /**
         * (boolean, default `true`) whether to add KSP plugin
         */
        public const val PROP_ADD_KSP: String = "kotlin.dataframe.add.ksp"

        /**
         * (string, default `null`) comma-delimited list of configurations to add KSP processing to.
         * Defaults to guessing configurations based on which kotlin plugin is applied (jvm or multiplatform)
         */
        public const val PROP_KSP_CONFIGS: String = "kotlin.dataframe.ksp.configs"
    }

    override fun apply(target: Project) {
        val property = target.findProperty(PROP_ADD_KSP)?.toString()
        var addKsp = true

        if (property != null) {
            if (property.equals("true", ignoreCase = true) || property.equals("false", ignoreCase = true)) {
                addKsp = property.toBoolean()
            } else {
                target.logger.warn(
                    "Invalid value '$property' for '$PROP_ADD_KSP' property. Defaulting to '$addKsp'. Please use 'true' or 'false'.",
                )
            }
        }

        val properties = Properties()
        properties.load(javaClass.getResourceAsStream("plugin.properties"))
        val preprocessorVersion = properties.getProperty("PREPROCESSOR_VERSION")

        // regardless whether we add KSP or the user adds it, when it's added,
        // configure it to depend on symbol-processor-all
        target.plugins.whenPluginAdded {
            if (this::class.qualifiedName?.contains("com.google.devtools.ksp") == true) {
                val isMultiplatform by lazy {
                    when {
                        target.plugins.hasPlugin("org.jetbrains.kotlin.jvm") -> false

                        target.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") -> true

                        else -> {
                            target.logger.warn(
                                "Kotlin plugin must be applied first so we know whether to use multiplatform configurations or not",
                            )
                            false
                        }
                    }
                }
                val overriddenConfigs = target.findProperty(PROP_KSP_CONFIGS)
                    ?.let { (it as String) }
                    ?.split(",")
                    ?.map { it.trim() }
                val configs = when {
                    overriddenConfigs != null -> overriddenConfigs
                    isMultiplatform -> listOf("kspJvm", "kspJvmTest")
                    else -> listOf("ksp", "kspTest")
                }

                val cfgsToAdd = configs.toMutableSet()

                configs.forEach { cfg ->
                    target.configurations.findByName(cfg)?.apply {
                        cfgsToAdd.remove(cfg)
                        dependencies.add(
                            target.dependencies.create(
                                "org.jetbrains.kotlinx.dataframe:symbol-processor-all:$preprocessorVersion",
                            ),
                        )
                    }
                }
                target.gradle.projectsEvaluated {
                    cfgsToAdd.forEach { cfg ->
                        target.logger.warn(
                            "Configuration '$cfg' was never found. Please make sure the KSP plugin is applied.",
                        )
                    }
                }

                target.logger.info("Added DataFrame dependency to the KSP plugin.")
                target.extensions.getByType<KspExtension>().arg(
                    "dataframe.resolutionDir",
                    target.projectDir.absolutePath,
                )
            }
        }

        if (addKsp) {
            target.plugins.apply(KspPluginApplier::class.java)
        } else {
            target.logger.warn(
                "Plugin 'org.jetbrains.kotlinx.dataframe' comes bundled with its own version of KSP which is " +
                    "currently disabled as 'kotlin.dataframe.add.ksp' is set to 'false' in a 'properties' file. " +
                    "Either set 'kotlin.dataframe.add.ksp' to 'true' or add the plugin 'com.google.devtools.ksp' " +
                    "manually.",
            )
        }
        target.plugins.apply(SchemaGeneratorPlugin::class.java)
    }
}

@Suppress("unused")
public class DeprecatingSchemaGeneratorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.logger.warn(
            "DEPRECATION: Replace plugin id(\"org.jetbrains.kotlin.plugin.dataframe\") and kotlin(\"plugin.dataframe\") with id(\"org.jetbrains.kotlinx.dataframe\").",
        )
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
