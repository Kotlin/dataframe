package org.jetbrains.kotlinx.dataframe.explainer

import java.io.File

fun main() {
    File("build/dataframes")
        .walkTopDown()
        .filter {
            it.nameWithoutExtension.startsWith("org.jetbrains")
        }
        // org.ClassName.functionName_properties
        // <dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.addDfs.html"/>
        .groupBy {
            it.nameWithoutExtension.substringBefore("_")
        }.mapValues { (name, files) ->
            val target = File("../docs/StardustDocs/resources/snippets")
            val original = files
                .firstOrNull { it.nameWithoutExtension.contains("properties") }
                ?: files.first()
            original.copyTo(File(target, "$name.html"), overwrite = true)
        }
}
