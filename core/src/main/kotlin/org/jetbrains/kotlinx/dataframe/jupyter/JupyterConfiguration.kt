package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration

public class JupyterConfiguration {
    public val display: DisplayConfiguration = DisplayConfiguration()

    /** DSL accessor. */
    public operator fun invoke(block: JupyterConfiguration.() -> Unit): JupyterConfiguration = apply(block)
}
