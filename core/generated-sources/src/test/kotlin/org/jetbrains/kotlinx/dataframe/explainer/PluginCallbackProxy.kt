package org.jetbrains.kotlinx.dataframe.explainer

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowValueFilter
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.Convert
import org.jetbrains.kotlinx.dataframe.api.FormattedFrame
import org.jetbrains.kotlinx.dataframe.api.Gather
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.Merge
import org.jetbrains.kotlinx.dataframe.api.Pivot
import org.jetbrains.kotlinx.dataframe.api.PivotGroupBy
import org.jetbrains.kotlinx.dataframe.api.ReducedPivot
import org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy
import org.jetbrains.kotlinx.dataframe.api.Split
import org.jetbrains.kotlinx.dataframe.api.SplitWithTransform
import org.jetbrains.kotlinx.dataframe.api.Update
import org.jetbrains.kotlinx.dataframe.api.format
import org.jetbrains.kotlinx.dataframe.api.frames
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.values
import org.jetbrains.kotlinx.dataframe.api.where
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.io.DataFrameHtmlData
import org.jetbrains.kotlinx.dataframe.io.sessionId
import org.jetbrains.kotlinx.dataframe.io.tableInSessionId
import org.jetbrains.kotlinx.dataframe.io.toHtml
import java.io.File

annotation class TransformDataFrameExpressions

fun interface PluginCallback {
    fun doAction(
        source: String,
        name: String,
        df: Any,
        id: String,
        receiverId: String?,
        containingClassFqName: String?,
        containingFunName: String?,
        statementIndex: Int,
    )
}

object PluginCallbackProxy : PluginCallback {
    val names = mutableMapOf<String, List<String>>()
    val expressionsByStatement = mutableMapOf<Int, List<Expression>>()

    private var manualOutput: DataFrameHtmlData? = null

    fun overrideHtmlOutput(manualOutput: DataFrameHtmlData) {
        this.manualOutput = manualOutput
    }

    data class Expression(
        val source: String,
        val containingClassFqName: String?,
        val containingFunName: String?,
        val df: Any,
    )

    fun start() {
        expressionsByStatement.clear()
        manualOutput = null
    }

    fun save() {
        // ensure stable table ids across test invocation
        sessionId = 0
        tableInSessionId = 0
        var output: DataFrameHtmlData
        val manualOutput = this.manualOutput
        if (manualOutput == null) {
            output = DataFrameHtmlData.tableDefinitions() + WritersideStyle

            // make copy to avoid concurrent modification exception
            val statements = expressionsByStatement.toMap()
            when (statements.size) {
                0 -> error("function doesn't have any dataframe expression")

                1 -> {
                    output += statementOutput(statements.values.single())
                }

                else -> {
                    statements.forEach { (index, expressions) ->
                        var details: DataFrameHtmlData = statementOutput(expressions)

                        details = details.copy(
                            body =
                                """
                                <details>
                                <summary>${
                                    expressions.joinToString(".") { it.source }.also {
                                        if (it.length > 95) {
                                            TODO(
                                                "expression is too long ${it.length}. better to split sample in multiple snippets",
                                            )
                                        }
                                    }.escapeHtmlForIFrame()
                                }</summary>
                                ${details.body}
                                </details>
                                <br>
                                """.trimIndent(),
                        )
                        output += details
                    }
                }
            }
        } else {
            output = manualOutput
        }

        val input = expressionsByStatement.values.first().first()
        val name = "${input.containingClassFqName}.${input.containingFunName}"
        val destination = File("build/dataframes").also {
            it.mkdirs()
        }
        output.writeHtml(File(destination, "$name.html"))
        val korro = File("build/korroOutputLines").also {
            it.mkdirs()
        }

        val group = name.substringBefore("_")
        File(korro, group).writeText(
            """
            
            <inline-frame src="resources/$group.html" width="100%"/>
            """.trimIndent(),
        )
    }

    private fun List<Expression>.joinToSource(): String = joinToString(".") { it.source }

    private fun statementOutput(expressions: List<Expression>): DataFrameHtmlData {
        var data = DataFrameHtmlData()
        val allow = setOf(
            "toDataFrame",
            "peek(dataFrameOf(col), dataFrameOf(col))",
        )
        if (expressions.isEmpty()) {
            error("No dataframe expressions in sample")
        }
        if (expressions.size == 1) {
            if (allow.any { expressions[0].source.contains(it) }) {
                val expression = expressions[0]
                data += convertToHtml(expression.df)
            } else {
                error("${expressions.joinToSource()} Sample without output or input (i.e. function returns some value)")
            }
        } else {
            for ((i, expression) in expressions.withIndex()) {
                when (i) {
                    0 -> {
                        val table = convertToHtml(expression.df)
                        val description = table.copy(
                            body =
                                """
                                <details>
                                <summary>Input ${convertToDescription(expression.df)}</summary>
                                 ${table.body}
                                </details>
                                """.trimIndent(),
                        )
                        data += description
                    }

                    expressions.lastIndex -> {
                        val table = convertToHtml(expression.df)
                        val description = table.copy(
                            body =
                                """
                                <details>
                                <summary>Output ${convertToDescription(expression.df)}</summary>
                                 ${table.body}
                                </details>
                                """.trimIndent(),
                        )
                        data += description
                    }

                    else -> {
                        val table = convertToHtml(expression.df)
                        val description = table.copy(
                            body =
                                """
                                <details>
                                <summary>Step $i: ${convertToDescription(expression.df)}</summary>
                                 ${table.body}
                                </details>
                                """.trimIndent(),
                        )
                        data += description
                    }
                }
            }
        }
        return data
    }

    var action: PluginCallback =
        PluginCallback { source, name, df, id, receiverId, containingClassFqName, containingFunName, statementIndex ->
            expressionsByStatement.compute(statementIndex) { _, list ->
                val element = Expression(source, containingClassFqName, containingFunName, df)
                list?.plus(element) ?: listOf(element)
            }
        }

    override fun doAction(
        source: String,
        name: String,
        df: Any,
        id: String,
        receiverId: String?,
        containingClassFqName: String?,
        containingFunName: String?,
        statementIndex: Int,
    ) {
        action.doAction(source, name, df, id, receiverId, containingClassFqName, containingFunName, statementIndex)
    }
}

private fun convertToHtml(dataframeLike: Any): DataFrameHtmlData {
    fun DataFrame<*>.toHtml() = this@toHtml.toHtml(SamplesDisplayConfiguration, getFooter = WritersideFooter)

    fun FormattedFrame<*>.toHtml1() = toHtml(SamplesDisplayConfiguration)

    return when (dataframeLike) {
        is Pivot<*> -> dataframeLike.frames().toDataFrame().toHtml()

        is ReducedPivot<*> -> dataframeLike.values().toDataFrame().toHtml()

        is PivotGroupBy<*> -> dataframeLike.frames().toHtml()

        is ReducedPivotGroupBy<*> -> dataframeLike.values().toHtml()

        is SplitWithTransform<*, *, *> -> dataframeLike.into().toHtml()

        is Merge<*, *, *> -> dataframeLike.into("merged").toHtml()

        is Gather<*, *, *, *> -> dataframeLike.into("key", "value").toHtml()

        is Update<*, *> ->
            dataframeLike.df.let {
                var it = it.format(
                    dataframeLike.columns as ColumnsSelectionDsl<Any?>.(
                        it: ColumnsSelectionDsl<Any?>,
                    ) -> ColumnsResolver<*>,
                )
                if (dataframeLike.filter != null) {
                    it = it.where(dataframeLike.filter as RowValueFilter<Any?, Any?>)
                }
                it.with {
                    background(rgb(152, 251, 152))
                }
            }.toHtml1()

        is Convert<*, *> -> DataFrameHtmlData(body = "<p>${dataframeLike::class}</p>")

        is FormattedFrame<*> -> dataframeLike.toHtml1()

        is GroupBy<*, *> -> dataframeLike.toDataFrame().toHtml()

        is AnyFrame -> dataframeLike.toHtml()

        is AnyCol -> dataframeLike.toDataFrame().toHtml()

        is DataRow<*> -> dataframeLike.toDataFrame().toHtml()

        is Split<*, *> -> dataframeLike.toDataFrame().toHtml()

        else -> throw IllegalArgumentException("Unsupported type: ${dataframeLike::class}")
    }
}

private fun convertToDescription(dataframeLike: Any): String =
    when (dataframeLike) {
        is AnyFrame -> dataframeLike.let {
            "DataFrame: rowsCount = ${it.rowsCount()}, columnsCount = ${it.columnsCount()}"
        }

        is Pivot<*> -> "Pivot"

        is ReducedPivot<*> -> "ReducedPivot"

        is PivotGroupBy<*> -> "PivotGroupBy"

        is ReducedPivotGroupBy<*> -> "ReducedPivotGroupBy"

        is SplitWithTransform<*, *, *> -> "SplitWithTransform"

        is Split<*, *> -> "Split"

        is Merge<*, *, *> -> "Merge"

        is Gather<*, *, *, *> -> "Gather"

        is Update<*, *> -> "Update"

        is Convert<*, *> -> "Convert"

        is FormattedFrame<*> -> "FormattedFrame"

        is GroupBy<*, *> -> "GroupBy"

        is DataRow<*> -> "DataRow"

        else -> throw IllegalArgumentException("Unsupported type: ${dataframeLike::class}")
    }.escapeHtmlForIFrame()

internal fun String.escapeHtmlForIFrame(): String =
    buildString {
        for (c in this@escapeHtmlForIFrame) {
            when (c) {
                '<' -> append("&lt;")

                '>' -> append("&gt;")

                '&' -> append("&amp;")

                '"' -> append("&quot;")

                '\'' -> append("&#39;")

                '\\' -> append("&#92;")

                else -> {
                    if (c.code > 127) {
                        append("&#${c.code};")
                    } else {
                        append(c)
                    }
                }
            }
        }
    }
