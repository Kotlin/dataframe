package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.Convert
import org.jetbrains.kotlinx.dataframe.api.FormatClause
import org.jetbrains.kotlinx.dataframe.api.FormattedFrame
import org.jetbrains.kotlinx.dataframe.api.Gather
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.GroupClause
import org.jetbrains.kotlinx.dataframe.api.InsertClause
import org.jetbrains.kotlinx.dataframe.api.Merge
import org.jetbrains.kotlinx.dataframe.api.MoveClause
import org.jetbrains.kotlinx.dataframe.api.Pivot
import org.jetbrains.kotlinx.dataframe.api.PivotGroupBy
import org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy
import org.jetbrains.kotlinx.dataframe.api.ReducedPivot
import org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy
import org.jetbrains.kotlinx.dataframe.api.RenameClause
import org.jetbrains.kotlinx.dataframe.api.ReplaceClause
import org.jetbrains.kotlinx.dataframe.api.Split
import org.jetbrains.kotlinx.dataframe.api.SplitWithTransform
import org.jetbrains.kotlinx.dataframe.api.Update
import org.jetbrains.kotlinx.dataframe.api.asDataFrame
import org.jetbrains.kotlinx.dataframe.api.columnsCount
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.dataTypes.IFRAME
import org.jetbrains.kotlinx.dataframe.dataTypes.IMG
import org.jetbrains.kotlinx.dataframe.impl.codeGen.CodeGenerationReadResult
import org.jetbrains.kotlinx.dataframe.impl.codeGen.urlCodeGenReader
import org.jetbrains.kotlinx.dataframe.io.DataFrameHtmlData
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.SupportedCodeGenerationFormat
import org.jetbrains.kotlinx.jupyter.api.FieldHandler
import org.jetbrains.kotlinx.jupyter.api.HTML
import org.jetbrains.kotlinx.jupyter.api.JupyterClientType
import org.jetbrains.kotlinx.jupyter.api.KotlinKernelHost
import org.jetbrains.kotlinx.jupyter.api.Notebook
import org.jetbrains.kotlinx.jupyter.api.VariableName
import org.jetbrains.kotlinx.jupyter.api.declare
import org.jetbrains.kotlinx.jupyter.api.libraries.ColorScheme
import org.jetbrains.kotlinx.jupyter.api.libraries.FieldHandlerFactory
import org.jetbrains.kotlinx.jupyter.api.libraries.JupyterIntegration
import org.jetbrains.kotlinx.jupyter.api.libraries.resources
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf

/** Users will get an error if their Kotlin Jupyter kernel is older than this version. */
private const val MIN_KERNEL_VERSION = "0.11.0.198"

internal val newDataSchemas = mutableListOf<KClass<*>>()

internal class Integration(private val notebook: Notebook, private val options: MutableMap<String, String?>) :
    JupyterIntegration() {

    val version = options["v"]

    // TODO temporary settings while these experimental modules are being developed

    private val enableExperimentalCsv = options["enableExperimentalCsv"]
    private val enableExperimentalGeo = options["enableExperimentalGeo"]
    private val enableExperimentalOpenApi = options["enableExperimentalOpenApi"]

    private fun KotlinKernelHost.updateImportDataSchemaVariable(
        importDataSchema: ImportDataSchema,
        property: KProperty<*>,
    ): VariableName? {
        val formats = supportedFormats.filterIsInstance<SupportedCodeGenerationFormat>()
        val name = property.name + "DataSchema"
        return when (
            val codeGenResult = CodeGenerator.urlCodeGenReader(importDataSchema.url, name, formats, true)
        ) {
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
                execute(
                    """DISPLAY("Failed to read data schema from ${importDataSchema.url}: ${codeGenResult.reason}")""",
                )
                null
            }
        }
    }

    private fun KotlinKernelHost.updateAnyFrameVariable(
        df: AnyFrame,
        property: KProperty<*>,
        codeGen: ReplCodeGenerator,
    ): VariableName? =
        execute(
            codeWithTypeCastGenerator = codeGen.process(df, property),
            property = property,
            type = DataFrame::class.createStarProjectedType(false),
        )

    private fun KotlinKernelHost.updateAnyRowVariable(
        row: AnyRow,
        property: KProperty<*>,
        codeGen: ReplCodeGenerator,
    ): VariableName? =
        execute(
            codeWithTypeCastGenerator = codeGen.process(row, property),
            property = property,
            type = DataRow::class.createStarProjectedType(false),
        )

    private fun KotlinKernelHost.updateColumnGroupVariable(
        col: ColumnGroup<*>,
        property: KProperty<*>,
        codeGen: ReplCodeGenerator,
    ): VariableName? =
        execute(
            codeWithTypeCastGenerator = codeGen.process(col.asDataFrame(), property),
            property = property,
            type = ColumnGroup::class.createStarProjectedType(false),
        )

    private fun KotlinKernelHost.updateAnyColVariable(
        col: AnyCol,
        property: KProperty<*>,
        codeGen: ReplCodeGenerator,
    ): VariableName? =
        if (col.isColumnGroup()) {
            val codeWithDfCaster = codeGen.process(col.asDataFrame(), property)
            val codeWithColumnGroupCaster = codeWithDfCaster.copy {
                codeWithDfCaster.typeCastGenerator("$it.asColumnGroup()")
            }
            execute(
                codeWithTypeCastGenerator = codeWithColumnGroupCaster,
                property = property,
                type = DataColumn::class.createStarProjectedType(false),
            )
        } else {
            null
        }

    private fun KotlinKernelHost.updateGroupByVariable(
        instance: GroupBy<*, *>,
        property: KProperty<*>,
        codeGen: ReplCodeGenerator,
    ): VariableName? =
        execute(
            codeWithTypeCastGenerator = codeGen.process(instance),
            property = property,
            type = GroupBy::class.createStarProjectedType(false),
        )

    override fun Builder.onLoaded() {
        if (version != null) {
            if (enableExperimentalCsv?.toBoolean() == true) {
                println("CSV module is already enabled by default now.")
            }
            if (enableExperimentalGeo?.toBoolean() == true) {
                println("Enabling experimental Geo module: dataframe-geo")
                repositories("https://repo.osgeo.org/repository/release")
                dependencies("org.jetbrains.kotlinx:dataframe-geo:$version")
            }
            if (enableExperimentalOpenApi?.toBoolean() == true) {
                println("Enabling experimental OpenAPI 3.0.0 module: dataframe-openapi")
                dependencies(
                    "org.jetbrains.kotlinx:dataframe-openapi:$version",
                    "org.jetbrains.kotlinx:dataframe-openapi-generator:$version",
                )
            }
        }

        try {
            setMinimalKernelVersion(MIN_KERNEL_VERSION)
        } catch (_: NoSuchMethodError) {
            // will be thrown when a version < 0.11.0.198
            throw IllegalStateException(
                getKernelUpdateMessage(notebook.kernelVersion, MIN_KERNEL_VERSION, notebook.jupyterClientType),
            )
        }
        val codeGen = ReplCodeGenerator.create()
        val config = JupyterConfiguration(enableExperimentalOpenApi = enableExperimentalOpenApi?.toBoolean() == true)

        if (notebook.jupyterClientType == JupyterClientType.KOTLIN_NOTEBOOK) {
            config.display.isolatedOutputs = true
        }

        onLoaded {
            declare("dataFrameConfig" to config)
        }

        resources {
            if (!config.display.isolatedOutputs) {
                js("DataFrame") {
                    if (config.display.localTesting()) {
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
            render<DisableRowsLimitWrapper>(
                { "DataRow: index = ${it.value.rowsCount()}, columnsCount = ${it.value.columnsCount()}" },
                applyRowsLimit = false,
            )

            render<GroupClause<*, *>>({ "Group" })
            render<MoveClause<*, *>>({ "Move" })
            render<RenameClause<*, *>>({ "Rename" })
            render<ReplaceClause<*, *>>({ "Replace" })
            render<InsertClause<*>>({ "Insert" })
            render<FormatClause<*, *>>({ "Format" })

            render<DataFrameHtmlData> {
                // Our integration declares script and css definition. But in Kotlin Notebook outputs are isolated in IFrames
                // That's why we include them directly in the output
                if (notebook.jupyterClientType == JupyterClientType.KOTLIN_NOTEBOOK) {
                    it.withTableDefinitions().toJupyterHtmlData().toIFrame(notebook.currentColorScheme)
                } else {
                    it.toJupyterHtmlData().toSimpleHtml(notebook.currentColorScheme)
                }
            }

            render<AnyRow>(
                { "DataRow: index = ${it.index()}, columnsCount = ${it.columnsCount()}" },
            )
            render<ColumnGroup<*>>(
                {
                    """ColumnGroup: name = "${it.name()}", rowsCount = ${it.rowsCount()}, columnsCount = ${it.columnsCount()}"""
                },
            )
            render<AnyCol>(
                { """DataColumn: name = "${it.name()}", type = ${renderType(it.type())}, size = ${it.size()}""" },
            )
            render<AnyFrame>(
                { "DataFrame: rowsCount = ${it.rowsCount()}, columnsCount = ${it.columnsCount()}" },
            )
            render<FormattedFrame<*>>(
                { "DataFrame: rowsCount = ${it.df().rowsCount()}, columnsCount = ${it.df().columnsCount()}" },
                modifyConfig = { getDisplayConfiguration(it) },
            )
            render<GroupBy<*, *>>({ "GroupBy" })
            render<ReducedGroupBy<*, *>>({ "ReducedGroupBy" })
            render<Pivot<*>>({ "Pivot" })
            render<ReducedPivot<*>>({ "ReducedPivot" })
            render<PivotGroupBy<*>>({ "PivotGroupBy" })
            render<ReducedPivotGroupBy<*>>({ "ReducedPivotGroupBy" })
            render<SplitWithTransform<*, *, *>>({ "Split" })
            render<Split<*, *>>({ "Split" })
            render<Merge<*, *, *>>({ "Merge" })
            render<Gather<*, *, *, *>>({ "Gather" })
            render<IMG> { HTML(it.toString()) }
            render<IFRAME> { HTML(it.toString()) }
            render<Update<*, *>>({ "Update" })
            render<Convert<*, *>>({ "Convert" })
        }

        import("org.jetbrains.kotlinx.dataframe.api.*")
        import("org.jetbrains.kotlinx.dataframe.*")
        import("org.jetbrains.kotlinx.dataframe.annotations.*")
        import("org.jetbrains.kotlinx.dataframe.io.*")
        import("org.jetbrains.kotlinx.dataframe.columns.*")
        import("org.jetbrains.kotlinx.dataframe.jupyter.ImportDataSchema")
        import("org.jetbrains.kotlinx.dataframe.jupyter.importDataSchema")
        import("org.jetbrains.kotlinx.dataframe.jupyter.KotlinNotebookPluginUtils")
        import("java.net.URL")
        import("java.net.URI")
        import("java.io.File")
        import("kotlinx.datetime.Instant")
        import("kotlinx.datetime.LocalDateTime")
        import("kotlinx.datetime.LocalDate")
        import("org.jetbrains.kotlinx.dataframe.dataTypes.*")
        import("org.jetbrains.kotlinx.dataframe.impl.codeGen.urlCodeGenReader")

        addTypeConverter(object : FieldHandler {

            override val execution = FieldHandlerFactory.createUpdateExecution<Any> { instance, property ->
                // TODO check property type first, then instance, Issue #1245
                when (instance) {
                    is AnyCol -> updateAnyColVariable(instance, property, codeGen)
                    is ColumnGroup<*> -> updateColumnGroupVariable(instance, property, codeGen)
                    is AnyRow -> updateAnyRowVariable(instance, property, codeGen)
                    is AnyFrame -> updateAnyFrameVariable(instance, property, codeGen)
                    is ImportDataSchema -> updateImportDataSchemaVariable(instance, property)
                    is GroupBy<*, *> -> updateGroupByVariable(instance, property, codeGen)
                    else -> error("${instance::class} should not be handled by Dataframe field handler")
                }
            }

            override fun accepts(value: Any?, property: KProperty<*>): Boolean =
                value is AnyCol ||
                    value is ColumnGroup<*> ||
                    value is AnyRow ||
                    value is AnyFrame ||
                    value is ImportDataSchema ||
                    value is GroupBy<*, *>
        })

        fun KotlinKernelHost.addDataSchemas(classes: List<KClass<*>>) {
            val code = classes
                .joinToString("\n") { codeGen.process(it) }
                .trim()

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

// region friend module error suppression

@Suppress("INVISIBLE_REFERENCE")
private interface ReplCodeGenerator : org.jetbrains.kotlinx.dataframe.impl.codeGen.ReplCodeGenerator {

    companion object {
        fun create(): ReplCodeGenerator =
            object :
                ReplCodeGenerator,
                org.jetbrains.kotlinx.dataframe.impl.codeGen.ReplCodeGenerator by
                org.jetbrains.kotlinx.dataframe.impl.codeGen.ReplCodeGeneratorImpl() {}
    }
}

@Suppress("INVISIBLE_REFERENCE")
private val supportedFormats
    get() = org.jetbrains.kotlinx.dataframe.io.supportedFormats

@Suppress("INVISIBLE_REFERENCE")
private fun KClass<*>.createStarProjectedType(nullable: Boolean) =
    org.jetbrains.kotlinx.dataframe.impl.createStarProjectedType(this, nullable)

@Suppress("INVISIBLE_REFERENCE")
private fun renderType(type: KType?) = org.jetbrains.kotlinx.dataframe.impl.renderType(type)

@Suppress("INVISIBLE_REFERENCE")
private fun <T> FormattedFrame<T>.df() = df

@Suppress("INVISIBLE_REFERENCE")
private fun DisplayConfiguration.localTesting() = localTesting

// endregion
