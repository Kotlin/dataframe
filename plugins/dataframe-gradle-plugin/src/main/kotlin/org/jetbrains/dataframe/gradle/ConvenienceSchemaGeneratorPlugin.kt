package org.jetbrains.dataframe.gradle

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
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
        if (addKsp) {
            target.plugins.apply(KspPluginApplier::class.java)
        }
        target.afterEvaluate {
            target.extensions.findByType<KspExtension>()?.arg("dataframe.resolutionDir", target.projectDir.absolutePath)
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

internal class KspPluginApplier : Plugin<Project> {
    override fun apply(target: Project) {
        val properties = Properties()
        properties.load(javaClass.getResourceAsStream("plugin.properties"))
        val preprocessorVersion = properties.getProperty("PREPROCESSOR_VERSION")
        target.plugins.apply("com.google.devtools.ksp")
        target.configurations.getByName("ksp").dependencies.add(
            target.dependencies.create("org.jetbrains.kotlinx.dataframe:symbol-processor-all:$preprocessorVersion")
        )
    }
}
