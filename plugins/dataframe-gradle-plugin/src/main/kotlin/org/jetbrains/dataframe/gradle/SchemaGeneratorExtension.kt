@file:Suppress("unused")

package org.jetbrains.dataframe.gradle

import groovy.lang.Closure
import org.gradle.api.Project
import java.io.File
import java.io.Serializable
import java.net.URL

open class SchemaGeneratorExtension {
    lateinit var project: Project
    internal val schemas: MutableList<Schema> = mutableListOf()
    var packageName: String? = null
    var sourceSet: String? = null
    var visibility: DataSchemaVisibility? = null
    internal var defaultPath: Boolean? = null

    fun schema(config: Schema.() -> Unit) {
        val schema = Schema(project).apply(config)
        schemas.add(schema)
    }

    fun schema(config: Closure<*>) {
        val schema = Schema(project)
        project.configure(schema, config)
        schemas.add(schema)
    }

    fun withoutDefaultPath() {
        defaultPath = false
    }
}

class Schema(
    private val project: Project,
    var data: Any? = null,
    var src: File? = null,
    var name: String? = null,
    var packageName: String? = null,
    var sourceSet: String? = null,
    var visibility: DataSchemaVisibility? = null,
    internal var defaultPath: Boolean? = null,
    val csvOptions: CsvOptions = CsvOptions()
) {
    fun setData(file: File) {
        data = file
    }

    fun setData(path: String) {
        data = path
    }

    fun setData(url: URL) {
        data = url
    }

    fun csvOptions(config: CsvOptions.() -> Unit) {
        csvOptions.apply(config)
    }
    fun csvOptions(config: Closure<*>) {
        project.configure(csvOptions, config)
    }

    fun withoutDefaultPath() {
        defaultPath = false
    }

    fun withDefaultPath() {
        defaultPath = true
    }
}

// Without Serializable GradleRunner tests fail
data class CsvOptions(
    var delimiter: Char = ','
) : Serializable
