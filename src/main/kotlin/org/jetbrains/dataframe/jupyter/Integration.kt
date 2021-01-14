package org.jetbrains.dataframe.jupyter

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.api.columns.GroupedColumn
import org.jetbrains.kotlinx.jupyter.api.*
import org.jetbrains.kotlinx.jupyter.api.libraries.*


internal class Integration : JupyterIntegration({

    render<DataFrame<*>> { HTML(it.toHTML()) }
    render<DataRow<*>> { it.toDataFrame() }
    render<GroupedColumn<*>> { it.df }
    render<DataCol> { dataFrameOf(listOf(it)) }
    render<GroupedDataFrame<*, *>> { it.plain() }

    import("org.jetbrains.dataframe.*")
    import("org.jetbrains.dataframe.io.*")

    val codeGen = CodeGenerator()

    updateVariable<DataFrame<*>> { df, property ->
        codeGen.generate(df, property)?.let {
            val code = it.with(property.name)
            execute(code).name
        }
    }

    updateVariable<DataFrameToListNamedStub> { stub, prop ->
        val code = codeGen.generate(stub).with(prop.name)
        execute(code).name
    }

    updateVariable<DataFrameToListTypedStub> { stub, prop ->
        val code = codeGen.generate(stub).with(prop.name)
        execute(code).name
    }

    onClassAnnotation<DataFrameType> { classes ->
        
        val code = classes.mapNotNull {
            codeGen.generateExtensionProperties(it)
        }.joinToString("\n").trim()

        if(code.isNotEmpty()) {
            execute(code)
        }
    }
})