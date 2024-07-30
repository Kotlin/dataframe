package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.dataframe.BuildConfig
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration

public class JupyterConfiguration {
    public val display: DisplayConfiguration = DisplayConfiguration()

    /** Version of the library. */
    public val version: String = BuildConfig.VERSION

    /** DSL accessor. */
    public operator fun invoke(block: JupyterConfiguration.() -> Unit): JupyterConfiguration = apply(block)
}
