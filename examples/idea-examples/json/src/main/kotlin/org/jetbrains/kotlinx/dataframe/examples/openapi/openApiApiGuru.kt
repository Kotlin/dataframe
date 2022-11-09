@file:ImportDataSchema(
    path = "src/main/resources/ApiGuruOpenApi.yaml",
    // no name needed since we're generating the names using OpenApi,
    // but it's still required when using multiple ImportDataSchemas
    name = "ApiGuruOpenApi",
)

package org.jetbrains.kotlinx.dataframe.examples.openapi

import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.examples.openapi.gradle.APIs as GradleAPIs
import org.jetbrains.kotlinx.dataframe.examples.openapi.gradle.Metrics as GradleMetrics

/**
 * In this file we'll demonstrate how to use OpenApi schemas
 * to generate DataSchemas and how to use them.
 */
fun main() {
    gradle()
    ksp()
}

/**
 * Gradle example of reading JSON files with OpenApi schemas.
 * Ctrl+Click on [GradleAPIs] or [GradleMetrics] to see the generated code.
 *
 * (We use import aliases to avoid clashes with the KSP example)
 */
private fun gradle() {
    val apis = GradleAPIs.readJson("examples/idea-examples/openapi/src/main/resources/ApiGuruSample.json")
    apis.print(columnTypes = true, title = true, borders = true)

    val metrics = GradleMetrics.readJson("examples/idea-examples/openapi/src/main/resources/apiGuruMetrics.json")
    metrics.print(columnTypes = true, title = true, borders = true)
}

/**
 * KSP example of reading JSON files with OpenApi schemas.
 * Ctrl+Click on [APIs] or [Metrics] to see the generated code.
 */
private fun ksp() {
    val apis = APIs.readJson("examples/idea-examples/openapi/src/main/resources/ApiGuruSample.json")
    apis.print(columnTypes = true, title = true, borders = true)

    val metrics = Metrics.readJson("examples/idea-examples/openapi/src/main/resources/apiGuruMetrics.json")
    metrics.print(columnTypes = true, title = true, borders = true)
}
