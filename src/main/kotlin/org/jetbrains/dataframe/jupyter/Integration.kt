package org.jetbrains.dataframe.jupyter

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.api.columns.MapColumn
import org.jetbrains.dataframe.io.toHTML
import org.jetbrains.kotlinx.jupyter.api.*
import org.jetbrains.kotlinx.jupyter.api.libraries.*


internal class Integration : JupyterIntegration({

    render<AnyFrame> { HTML(it.toHTML()) }
    render<AnyRow> { it.toDataFrame() }
    render<MapColumn<*>> { it.df }
    render<AnyCol> { dataFrameOf(listOf(it)) }
    render<GroupedDataFrame<*, *>> { it.plain() }

    import("org.jetbrains.dataframe.*")
    import("org.jetbrains.dataframe.io.*")

    val codeGen = CodeGenerator.create()

    updateVariable<AnyFrame> { df, property ->
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

    onClassAnnotation<DataSchema> { classes ->
        
        val code = classes.mapNotNull {
            codeGen.generateExtensionProperties(it)
        }.joinToString("\n").trim()

        if(code.isNotEmpty()) {
            execute(code)
        }
    }
})