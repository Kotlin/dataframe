@file:ImportDataSchema(
    // Using just a sample since the full file will cause OOM errors
    path = "src/main/resources/ApiGuruSample.json",
    name = "APIsNoKeyValue",
)
@file:ImportDataSchema(
    // Now we can use the full file!
    path = "https://api.apis.guru/v2/list.json",
    name = "APIsKeyValue",
    jsonOptions = JsonOptions(
        // paths in the json that should be converted to KeyValue columns
        keyValuePaths = ["""$""", """$[*]["versions"]"""]
    )
)

package org.jetbrains.kotlinx.dataframe.examples.openapi

import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
import org.jetbrains.kotlinx.dataframe.annotations.JsonOptions
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.examples.openapi.gradle.noOpenApi.MetricsKeyValue
import org.jetbrains.kotlinx.dataframe.examples.openapi.gradle.noOpenApi.MetricsNoKeyValue

/**
 * In this file we'll demonstrate how to use the jsonOption `keyValuePaths`
 * both using the Gradle- and KSP plugin and what it does.
 */
fun main() {
    gradleNoKeyValue()
    gradleKeyValue()

    kspNoKeyValue()
    kspKeyValue()
}

/**
 * Gradle example of reading a JSON file with no key-value pairs.
 * Ctrl+Click on [MetricsNoKeyValue] to see the generated code.
 */
private fun gradleNoKeyValue() {
    val df = MetricsNoKeyValue.readJson("examples/idea-examples/openapi/src/main/resources/apiGuruMetrics.json")
    df.print(columnTypes = true, title = true, borders = true)
}

/**
 * Gradle example of reading a JSON file with key-value pairs.
 * Ctrl+Click on [MetricsKeyValue] to see the generated code.
 */
private fun gradleKeyValue() {
    val df = MetricsKeyValue.readJson("examples/idea-examples/openapi/src/main/resources/apiGuruMetrics.json")
    df.print(columnTypes = true, title = true, borders = true)
}

/**
 * KSP example of reading a JSON file with no key-value pairs.
 * Ctrl+Click on [APIsNoKeyValue] to see the generated code.
 *
 * Note the many generated interfaces. You can imagine larger files crashing the code generator.
 */
private fun kspNoKeyValue() {
    val df = APIsNoKeyValue.readJson("examples/idea-examples/openapi/src/main/resources/ApiGuruSample.json")
    df.print(columnTypes = true, title = true, borders = true)
}

/**
 * KSP example of reading a JSON file with key-value pairs.
 * Ctrl+Click on [APIsKeyValue] to see the generated code.
 */
private fun kspKeyValue() {
    val df = APIsKeyValue.readJson("examples/idea-examples/openapi/src/main/resources/ApiGuruSample.json")
        .value.first()

    df.print(columnTypes = true, title = true, borders = true)
}
