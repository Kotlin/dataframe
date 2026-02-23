package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.dataframe.core.BuildConfig
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration

public class JupyterConfiguration(
    /** If true, experimental OpenAPI 3.0.0 types support via importDataSchema() is enabled. Can be set via `%use dataframe(..., enableExperimentalOpenApi=true)` */
    public val enableExperimentalOpenApi: Boolean = false,
) {
    public val display: DisplayConfiguration = DisplayConfiguration()

    /** Version of the library. */
    public val version: String = BuildConfig.VERSION

    /** DSL accessor. */
    public operator fun invoke(block: JupyterConfiguration.() -> Unit): JupyterConfiguration = apply(block)
}
