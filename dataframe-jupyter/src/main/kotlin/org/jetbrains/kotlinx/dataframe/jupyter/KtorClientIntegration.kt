package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readJsonStr
import org.jetbrains.kotlinx.jupyter.ktor.client.core.NotebookHttpResponse

public fun NotebookHttpResponse.bodyAsDataFrame(): DataFrame<*> {
    return DataFrame.readJsonStr(bodyAsText())
}
