package org.jetbrains.dataframe.jupyter

import org.jetbrains.dataframe.*
import org.jetbrains.kotlinx.jupyter.api.*
import org.jetbrains.kotlinx.jupyter.api.TypeHandlerExecution
import org.jetbrains.kotlinx.jupyter.api.libraries.*


internal class LibraryProvider: JupyterIntegration({

        render<DataCol> { dataFrameOf(listOf(it)) }
        render<DataFrame<*>> { HTML(it.toHTML()) }
        render<GroupedDataFrame<*,*>> { it.plain() }
        render<DataRow<*>> { it.toDataFrame() }

        import("org.jetbrains.dataframe.*")
        import("org.jetbrains.dataframe.io.*")
})