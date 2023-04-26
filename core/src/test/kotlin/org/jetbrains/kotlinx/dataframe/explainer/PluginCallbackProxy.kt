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
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.io.DataFrameHtmlData
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.sessionId
import org.jetbrains.kotlinx.dataframe.io.tableInSessionId
import org.jetbrains.kotlinx.dataframe.io.toHTML
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
        statementIndex: Int
    )
}

object PluginCallbackProxy : PluginCallback {
    val names = mutableMapOf<String, List<String>>()
    val expressionsByStatement = mutableMapOf<Int, List<Expression>>()

    data class Expression(
        val source: String,
        val containingClassFqName: String?,
        val containingFunName: String?,
        val df: Any
    )

    fun start() {
        expressionsByStatement.clear()
    }

    fun save() {
        // ensure stable table ids across test invocation
        sessionId = 0
        tableInSessionId = 0
        var output = DataFrameHtmlData.tableDefinitions() + DataFrameHtmlData(
            // copy writerside stlyles
            style = """
                body {
                    font-family: "JetBrains Mono",SFMono-Regular,Consolas,"Liberation Mono",Menlo,Courier,monospace;
                }       
                
                :root {
                    color: #19191C;
                    background-color: #fff;
                }
                
                :root[theme="dark"] {
                    background-color: #19191C;
                    color: #FFFFFFCC
                }
                
                details details {
                    margin-left: 20px; 
                }
                
                summary {
                    padding: 6px;
                }
            """.trimIndent()
        )

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
                        <summary>${expressions.joinToString(".") { it.source }
                            .also {
                                if (it.length > 95) TODO("expression is too long ${it.length}. better to split sample in multiple snippets")
                            }
                            .escapeHtmlForIFrame()}</summary>
                        ${details.body}
                        </details>
                        <br>
                        """.trimIndent()
                    )

                    output += details
                }
            }
        }
        val input = expressionsByStatement.values.first().first()
        val name = "${input.containingClassFqName}.${input.containingFunName}"
        val destination = File("build/dataframes").also {
            it.mkdirs()
        }
        output.writeHTML(File(destination, "$name.html"))
    }

    private fun statementOutput(
        expressions: List<Expression>,
    ): DataFrameHtmlData {
        var data = DataFrameHtmlData()
        if (expressions.size < 2) error("Sample without output or input (i.e. function returns some value)")
        for ((i, expression) in expressions.withIndex()) {
            when (i) {
                0 -> {
                    val table = convertToHTML(expression.df)
                    val description = table.copy(
                        body = """
                                    <details>
                                    <summary>Input ${convertToDescription(expression.df)}</summary>
                                     ${table.body}
                                    </details>
                        """.trimIndent()
                    )
                    data += description
                }

                expressions.lastIndex -> {
                    val table = convertToHTML(expression.df)
                    val description = table.copy(
                        body = """
                                    <details>
                                    <summary>Output ${convertToDescription(expression.df)}</summary>
                                     ${table.body}
                                    </details>
                        """.trimIndent()
                    )
                    data += description
                }

                else -> {
                    val table = convertToHTML(expression.df)
                    val description = table.copy(
                        body = """
                                    <details>
                                    <summary>Step $i: ${convertToDescription(expression.df)}</summary>
                                     ${table.body}
                                    </details>
                        """.trimIndent()
                    )
                    data += description
                }
            }
        }
        return data
    }

    var action: PluginCallback = PluginCallback { source, name, df, id, receiverId, containingClassFqName, containingFunName, statementIndex ->
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
        statementIndex: Int
    ) {
        action.doAction(source, name, df, id, receiverId, containingClassFqName, containingFunName, statementIndex)
    }
}

private fun convertToHTML(dataframeLike: Any): DataFrameHtmlData {
    fun DataFrame<*>.toHTML() = toHTML(DisplayConfiguration(), getFooter = { "" })
    fun FormattedFrame<*>.toHTML1() = toHTML(DisplayConfiguration())

    return when (dataframeLike) {
        is Pivot<*> -> dataframeLike.frames().toDataFrame().toHTML()
        is ReducedPivot<*> -> dataframeLike.values().toDataFrame().toHTML()
        is PivotGroupBy<*> -> dataframeLike.frames().toHTML()
        is ReducedPivotGroupBy<*> -> dataframeLike.values().toHTML()
        is SplitWithTransform<*, *, *> -> dataframeLike.into().toHTML()
        is Merge<*, *, *> -> dataframeLike.into("merged").toHTML()
        is Gather<*, *, *, *> -> dataframeLike.into("key", "value").toHTML()
        is Update<*, *> -> dataframeLike.df.let {
            var it = it.format(dataframeLike.columns as ColumnsSelectionDsl<Any?>.(it: ColumnsSelectionDsl<Any?>) -> ColumnSet<*>)
            if (dataframeLike.filter != null) {
                it = it.where(dataframeLike.filter as RowValueFilter<Any?, Any?>)
            }
            it.with {
                background(rgb(152, 251, 152))
            }
        }
            .toHTML1()
        is Convert<*, *> -> DataFrameHtmlData(body = "<p>${dataframeLike::class}</p>")
        is FormattedFrame<*> -> dataframeLike.toHTML1()
        is GroupBy<*, *> -> dataframeLike.toDataFrame().toHTML()
        is AnyFrame -> dataframeLike.toHTML()
        is AnyCol -> dataframeLike.toDataFrame().toHTML()
        is DataRow<*> -> dataframeLike.toDataFrame().toHTML()
        is Split<*, *> -> dataframeLike.toDataFrame().toHTML()
        else -> throw IllegalArgumentException("Unsupported type: ${dataframeLike::class}")
    }
}

private fun convertToDescription(dataframeLike: Any): String {
    return when (dataframeLike) {
        is AnyFrame -> dataframeLike.let { "DataFrame: rowsCount = ${it.rowsCount()}, columnsCount = ${it.columnsCount()}" }
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
}

internal fun String.escapeHtmlForIFrame(): String {
    val str = this
    return buildString {
        for (c in str) {
            when {
                c.code > 127 || c == '\'' || c == '\\' -> {
                    append("&#")
                    append(c.code)
                    append(';')
                }
                c == '"' -> append("&quot;")
                c == '<' -> append("&amp;lt;")
                c == '>' -> append("&amp;gt;")
                c == '&' -> append("&amp;")
                else -> {
                    append(c)
                }
            }
        }
    }
}
