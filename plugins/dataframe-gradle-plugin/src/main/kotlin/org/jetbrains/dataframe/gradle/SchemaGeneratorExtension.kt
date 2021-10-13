@file:Suppress("unused")

package org.jetbrains.dataframe.gradle

import groovy.lang.Closure
import org.gradle.api.Project
import java.io.File
import java.net.URL

open class SchemaGeneratorExtension {
    lateinit var project: Project
    internal val schemas: MutableList<Schema> = mutableListOf()
    var packageName: String? = null
    var sourceSet: String? = null
    var visibility: DataSchemaVisibility? = null

    fun schema(config: Schema.() -> Unit) {
        val schema = Schema().apply(config)
        schemas.add(schema)
    }

    fun schema(config: Closure<*>) {
        val schema = Schema()
        project.configure(schema, config)
        schemas.add(schema)
    }
}

class Schema(
    var data: Any? = null,
    var src: File? = null,
    var name: String? = null,
    var packageName: String? = null,
    var sourceSet: String? = null,
    var visibility: DataSchemaVisibility? = null
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
}
