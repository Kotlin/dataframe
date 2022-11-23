package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.dataframe.impl.codeGen.ReplCodeGenerator
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.Convert
import org.jetbrains.kotlinx.dataframe.api.FormattedFrame
import org.jetbrains.kotlinx.dataframe.api.Gather
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.Merge
import org.jetbrains.kotlinx.dataframe.api.Pivot
import org.jetbrains.kotlinx.dataframe.api.PivotGroupBy
import org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy
import org.jetbrains.kotlinx.dataframe.api.ReducedPivot
import org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy
import org.jetbrains.kotlinx.dataframe.api.Split
import org.jetbrains.kotlinx.dataframe.api.SplitWithTransform
import org.jetbrains.kotlinx.dataframe.api.Update
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.asDataFrame
import org.jetbrains.kotlinx.dataframe.api.columnsCount
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.frames
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.values
import org.jetbrains.kotlinx.dataframe.codeGen.CodeWithConverter
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.dataTypes.IFRAME
import org.jetbrains.kotlinx.dataframe.dataTypes.IMG
import org.jetbrains.kotlinx.dataframe.impl.codeGen.CodeGenerationReadResult
import org.jetbrains.kotlinx.dataframe.impl.codeGen.urlCodeGenReader
import org.jetbrains.kotlinx.dataframe.impl.createStarProjectedType
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.io.SupportedCodeGenerationFormat
import org.jetbrains.kotlinx.dataframe.io.supportedFormats
import org.jetbrains.kotlinx.jupyter.api.HTML
import org.jetbrains.kotlinx.jupyter.api.HtmlData
import org.jetbrains.kotlinx.jupyter.api.KotlinKernelHost
import org.jetbrains.kotlinx.jupyter.api.Notebook
import org.jetbrains.kotlinx.jupyter.api.VariableName
import org.jetbrains.kotlinx.jupyter.api.declare
import org.jetbrains.kotlinx.jupyter.api.libraries.ColorScheme
import org.jetbrains.kotlinx.jupyter.api.libraries.JupyterIntegration
import org.jetbrains.kotlinx.jupyter.api.libraries.resources
import org.jetbrains.kotlinx.jupyter.api.renderHtmlAsIFrameIfNeeded
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubtypeOf

internal val newDataSchemas = mutableListOf<KClass<*>>()

internal class Integration(private val notebook: Notebook, private val options: MutableMap<String, String?>) :
    JupyterIntegration() {

    override fun Builder.onLoaded() {
        val codeGen = ReplCodeGenerator.create()
        val config = JupyterConfiguration()

        onLoaded {
            declare("dataFrameConfig" to config)
        }

        resources {
            if (!config.display.isolatedOutputs) {
                js("DataFrame") {
                    if (config.display.localTesting) {
                        classPath("init.js")
                    } else {
                        // Update this commit when new version of init.js is pushed
                        val initJsSha = "3db46ccccaa1291c0627307d64133317f545e6ae"
                        url("https://cdn.jsdelivr.net/gh/Kotlin/dataframe@$initJsSha/core/src/main/resources/init.js")
                    }
                }

                css("DataFrameTable") { classPath("table.css") }
            }
        }

        with(JupyterHtmlRenderer(config.display, this)) {
            render<HtmlData> { notebook.renderHtmlAsIFrameIfNeeded(it) }
            render<AnyRow>(
                { it.toDataFrame() },
                { "DataRow: index = ${it.index()}, columnsCount = ${it.columnsCount()}" })
            render<ColumnGroup<*>>(
                { it.asDataFrame() },
                { """ColumnGroup: name = "${it.name}", rowsCount = ${it.rowsCount()}, columnsCount = ${it.columnsCount()}""" })
            render<AnyCol>(
                { dataFrameOf(it) },
                { """DataColumn: name = "${it.name}", type = ${renderType(it.type())}, size = ${it.size()}""" })
            render<AnyFrame>(
                { it },
                { "DataFrame: rowsCount = ${it.rowsCount()}, columnsCount = ${it.columnsCount()}" })
            render<FormattedFrame<*>>(
                { it.df },
                { "DataFrame: rowsCount = ${it.df.rowsCount()}, columnsCount = ${it.df.columnsCount()}" },
                modifyConfig = { getDisplayConfiguration(it) })
            render<GroupBy<*, *>>({ it.toDataFrame() }, { "GroupBy" })
            render<ReducedGroupBy<*, *>>({ it.into(it.groupBy.groups.name()) }, { "ReducedGroupBy" })
            render<Pivot<*>>({ it.frames().toDataFrame() }, { "Pivot" })
            render<ReducedPivot<*>>({ it.values().toDataFrame() }, { "ReducedPivot" })
            render<PivotGroupBy<*>>({ it.frames() }, { "PivotGroupBy" })
            render<ReducedPivotGroupBy<*>>({ it.values() }, { "ReducedPivotGroupBy" })
            render<SplitWithTransform<*, *, *>>({ it.into() }, { "Split" })
            render<Split<*, *>>({ it.toDataFrame() }, { "Split" })
            render<Merge<*, *, *>>({ it.into("merged") }, { "Merge" })
            render<Gather<*, *, *, *>>({ it.into("key", "value") }, { "Gather" })
            render<IMG> { HTML(it.toString()) }
            render<IFRAME> { HTML(it.toString()) }
            render<Update<*, *>>({ it.df }, { "Update" })
            render<Convert<*, *>>({ it.df }, { "Convert" })
        }

        import("org.jetbrains.kotlinx.dataframe.api.*")
        import("org.jetbrains.kotlinx.dataframe.*")
        import("org.jetbrains.kotlinx.dataframe.annotations.*")
        import("org.jetbrains.kotlinx.dataframe.io.*")
        import("org.jetbrains.kotlinx.dataframe.columns.*")
        import("org.jetbrains.kotlinx.dataframe.jupyter.ImportDataSchema")
        import("org.jetbrains.kotlinx.dataframe.jupyter.importDataSchema")
        import("java.net.URL")
        import("java.io.File")
        import("kotlinx.datetime.Instant")
        import("kotlinx.datetime.LocalDateTime")
        import("kotlinx.datetime.LocalDate")
        import("org.jetbrains.kotlinx.dataframe.dataTypes.*")
        import("org.jetbrains.kotlinx.dataframe.impl.codeGen.urlCodeGenReader")

        fun KotlinKernelHost.execute(codeWithConverter: CodeWithConverter, argument: String): VariableName? {
            val code = codeWithConverter.with(argument)
            return if (code.isNotBlank()) {
                val result = execute(code)
                if (codeWithConverter.hasConverter) {
                    result.name
                } else null
            } else null
        }

        fun KotlinKernelHost.execute(codeWithConverter: CodeWithConverter, property: KProperty<*>): VariableName? {
            val variableName = property.name + if (property.returnType.isMarkedNullable) "!!" else ""
            return execute(codeWithConverter, variableName)
        }

        updateVariable<ImportDataSchema> { importDataSchema, property ->
            val formats = supportedFormats.filterIsInstance<SupportedCodeGenerationFormat>()
            val name = property.name + "DataSchema"
            when (val codeGenResult = CodeGenerator.urlCodeGenReader(importDataSchema.url, name, formats, true)) {
                is CodeGenerationReadResult.Success -> {
                    val readDfMethod = codeGenResult.getReadDfMethod(importDataSchema.url.toExternalForm())
                    val code = readDfMethod.additionalImports.joinToString("\n") +
                        "\n" +
                        codeGenResult.code

                    execute(code)
                    execute("""DISPLAY("Data schema successfully imported as ${property.name}: $name")""")

                    name
                }

                is CodeGenerationReadResult.Error -> {
                    execute("""DISPLAY("Failed to read data schema from ${importDataSchema.url}: ${codeGenResult.reason}")""")
                    null
                }
            }
        }

        updateVariable<AnyFrame> { df, property ->
            execute(codeGen.process(df, property), property)
        }

        updateVariable<AnyRow> { row, property ->
            execute(codeGen.process(row, property), property)
        }

        updateVariable<ColumnGroup<*>> { col, property ->
            execute(codeGen.process(col.asDataFrame(), property), property)
        }

        updateVariable<AnyCol> { col, property ->
            if (col.isColumnGroup()) {
                val codeWithConverter = codeGen.process(col.asColumnGroup().asDataFrame(), property).let { c ->
                    CodeWithConverter(c.declarations) { c.converter("$it.asColumnGroup()") }
                }
                execute(codeWithConverter, property)
            } else null
        }

        fun KotlinKernelHost.addDataSchemas(classes: List<KClass<*>>) {
            val code = classes.joinToString("\n") {
                codeGen.process(it)
            }.trim()

            if (code.isNotEmpty()) {
                execute(code)
            }
        }

        onClassAnnotation<DataSchema> { addDataSchemas(it) }

        beforeCellExecution {
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

        onColorSchemeChange {
            config.display.useDarkColorScheme = (it == ColorScheme.DARK)
        }
    }
}

public fun KotlinKernelHost.useSchemas(schemaClasses: Iterable<KClass<*>>) {
    newDataSchemas.addAll(schemaClasses)
}

public fun KotlinKernelHost.useSchemas(vararg schemaClasses: KClass<*>): Unit = useSchemas(schemaClasses.asIterable())

public inline fun <reified T> KotlinKernelHost.useSchema(): Unit = useSchemas(T::class)
