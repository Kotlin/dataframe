package org.jetbrains.dataframe.gradle

import groovy.lang.Closure
import org.gradle.util.ConfigureUtil
import java.io.File
import java.net.URL

open class SchemaGeneratorExtension {
    internal val schemas: MutableList<Schema> = mutableListOf()
    var packageName: String? = null
    var sourceSet: String? = null

    fun schema(config: Schema.() -> Unit) {
        val schema = Schema().apply(config)
        schemas.add(schema)
    }

    fun schema(config: Closure<*>) {
        val schema = Schema()
        ConfigureUtil.configure(config, schema)
        schemas.add(schema)
    }
}

class Schema(
    var data: Any? = null,
    var src: File? = null,
    var name: String? = null,
    var packageName: String? = null,
    var sourceSet: String? = null
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
