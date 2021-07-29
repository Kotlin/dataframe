package org.jetbrains.dataframe.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.repositories
import java.util.*

class ConvenienceSchemaGeneratorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val properties = Properties()
        properties.load(javaClass.getResourceAsStream("plugin.properties"))
        val preprocessorVersion = properties.getProperty("PREPROCESSOR_VERSION")
        target.buildscript.repositories { google() }
        target.repositories { google() }
        target.plugins.apply("com.google.devtools.ksp")
        target.plugins.apply(SchemaGeneratorPlugin::class.java)
        target.configurations.getByName("ksp").dependencies.add(
            target.dependencies.create("org.jetbrains.dataframe:symbol-processor:$preprocessorVersion")
        )
    }
}
