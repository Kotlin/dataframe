package org.jetbrains.dataframe.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.repositories
import java.util.*

@Suppress("unused")
class ConvenienceSchemaGeneratorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(KspPluginApplier::class.java)
        target.plugins.apply(SchemaGeneratorPlugin::class.java)
    }
}

internal class KspPluginApplier : Plugin<Project> {
    override fun apply(target: Project) {
        val properties = Properties()
        properties.load(javaClass.getResourceAsStream("plugin.properties"))
        val preprocessorVersion = properties.getProperty("PREPROCESSOR_VERSION")
        target.plugins.apply("com.google.devtools.ksp")
        target.configurations.getByName("ksp").dependencies.add(
            target.dependencies.create("org.jetbrains.kotlinx.dataframe:symbol-processor:$preprocessorVersion")
        )
    }
}
