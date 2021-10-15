package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.dataframe.impl.codeGen.ReplCodeGenerator
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.FormattedFrame
import org.jetbrains.kotlinx.dataframe.api.GroupedDataFrame
import org.jetbrains.kotlinx.dataframe.api.GroupedPivot
import org.jetbrains.kotlinx.dataframe.api.PivotedDataFrame
import org.jetbrains.kotlinx.dataframe.api.toAnyFrame
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.CodeWithConverter
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.dataTypes.IMG
import org.jetbrains.kotlinx.dataframe.impl.createStarProjectedType
import org.jetbrains.kotlinx.dataframe.io.HtmlData
import org.jetbrains.kotlinx.dataframe.io.initHtml
import org.jetbrains.kotlinx.dataframe.io.toHTML
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.size
import org.jetbrains.kotlinx.dataframe.stubs.DataFrameToListNamedStub
import org.jetbrains.kotlinx.dataframe.stubs.DataFrameToListTypedStub
import org.jetbrains.kotlinx.dataframe.toDataFrame
import org.jetbrains.kotlinx.jupyter.api.HTML
import org.jetbrains.kotlinx.jupyter.api.KotlinKernelHost
import org.jetbrains.kotlinx.jupyter.api.VariableName
import org.jetbrains.kotlinx.jupyter.api.annotations.JupyterLibrary
import org.jetbrains.kotlinx.jupyter.api.declare
import org.jetbrains.kotlinx.jupyter.api.libraries.JupyterIntegration
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubtypeOf

internal val newDataSchemas = mutableListOf<KClass<*>>()

@JupyterLibrary
internal class Integration : JupyterIntegration() {

    override fun Builder.onLoaded() {
        val codeGen = ReplCodeGenerator.create()
        val config = JupyterConfiguration()

        onLoaded {
            declare("dataFrameConfig" to config)
            display(initHtml().toJupyter())
        }

        with(JupyterHtmlRenderer(config.display, this)) {
            render<HtmlData> { it.toJupyter() }
            render<AnyFrame>({ it })
            render<FormattedFrame<*>>({ it.df }, modifyConfig = { getDisplayConfiguration(it) })
            render<AnyRow>({ it.toDataFrame() }, { "DataRow [${it.ncol}]" })
            render<ColumnGroup<*>>({ it.df })
            render<AnyCol>({ listOf(it).toAnyFrame() }, { "DataColumn [${it.nrow()}]" })
            render<GroupedDataFrame<*, *>>({ it.toDataFrame() })
            render<PivotedDataFrame<*>> { it.toDataFrame().toHTML(config.display) { "Pivot: ${it.ncol} columns" } }
            render<GroupedPivot<*>> { it.toDataFrame().toHTML(config.display) { "GroupedPivot: ${it.size}" } }

            render<IMG> { HTML("<img src=\"${it.url}\"/>") }
        }

        import("org.jetbrains.kotlinx.dataframe.*")
        import("org.jetbrains.kotlinx.dataframe.annotations.*")
        import("org.jetbrains.kotlinx.dataframe.io.*")
        import("java.net.URL")
        import("org.jetbrains.kotlinx.dataframe.dataTypes.*")

        fun KotlinKernelHost.execute(codeWithConverter: CodeWithConverter, property: KProperty<*>): VariableName? {
            val code = codeWithConverter.with(property.name)
            return if (code.isNotBlank()) {
                val result = execute(code)
                if (codeWithConverter.hasConverter) {
                    result.name
                } else null
            } else null
        }

        updateVariable<AnyFrame> { df, property ->
            execute(codeGen.process(df, property), property)
        }

        updateVariable<AnyRow> { row, property ->
            execute(codeGen.process(row, property), property)
        }

        updateVariable<DataFrameToListNamedStub> { stub, prop ->
            val code = codeGen.process(stub).with(prop.name)
            execute(code).name
        }

        updateVariable<DataFrameToListTypedStub> { stub, prop ->
            val code = codeGen.process(stub).with(prop.name)
            execute(code).name
        }

        fun KotlinKernelHost.addDataSchemas(classes: List<KClass<*>>) {
            val code = classes.map {
                codeGen.process(it)
            }.joinToString("\n").trim()

            if (code.isNotEmpty()) {
                execute(code)
            }
        }

        onClassAnnotation<DataSchema> { addDataSchemas(it) }

        afterCellExecution { snippet, result ->
            if (newDataSchemas.isNotEmpty()) {
                addDataSchemas(newDataSchemas)
                newDataSchemas.clear()
            }
        }

        val internalTypes = listOf(
            ColumnReference::class,
        ).map { it.createStarProjectedType(true) }

        markVariableInternal { property ->
            // TODO: add more conditions to include all generated properties and other internal stuff
            //  that should not be shown to user in Jupyter variables view
            internalTypes.any { property.returnType.isSubtypeOf(it) }
        }
    }
}

public fun KotlinKernelHost.useSchemas(schemaClasses: Iterable<KClass<*>>) {
    newDataSchemas.addAll(schemaClasses)
}

public fun KotlinKernelHost.useSchemas(vararg schemaClasses: KClass<*>): Unit = useSchemas(schemaClasses.asIterable())

public inline fun <reified T> KotlinKernelHost.useSchema(): Unit = useSchemas(T::class)
