package org.jetbrains.dataframe.jupyter

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.api.columns.GroupedColumn
import org.jetbrains.kotlinx.jupyter.api.*
import org.jetbrains.kotlinx.jupyter.api.TypeHandlerExecution
import org.jetbrains.kotlinx.jupyter.api.libraries.*


internal class LibraryProvider: JupyterIntegration({

        render<DataFrame<*>> { HTML(it.toHTML()) }
        render<DataRow<*>> { it.toDataFrame() }
        render<GroupedColumn<*>> { it.df }
        render<DataCol> { dataFrameOf(listOf(it)) }
        render<GroupedDataFrame<*,*>> { it.plain() }

        import("org.jetbrains.dataframe.*")
        import("org.jetbrains.dataframe.io.*")
})