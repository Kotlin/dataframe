package org.jetbrains.dataframe.gradle

import java.io.File

open class SchemaGeneratorExtension {
    var generateExtensionProperties: Boolean = true
    val schemas: MutableList<Schema> = mutableListOf()

    fun schema(config: Schema.() -> Unit) {
        val schema = Schema().apply(config)
        schemas.add(schema)
    }
}

class Schema(
    var data: Any? = null,
    var src: File? = null,
    var interfaceName: String? = null,
    var packageName: String = ""
)
