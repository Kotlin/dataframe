package org.jetbrains.kotlinx.dataframe.explainer

import com.beust.klaxon.JsonObject
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.api.print
import java.io.File
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.Convert
import org.jetbrains.kotlinx.dataframe.api.FormattedFrame
import org.jetbrains.kotlinx.dataframe.api.Gather
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.Merge
import org.jetbrains.kotlinx.dataframe.api.Pivot
import org.jetbrains.kotlinx.dataframe.api.PivotGroupBy
import org.jetbrains.kotlinx.dataframe.api.ReducedPivot
import org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy
import org.jetbrains.kotlinx.dataframe.api.SplitWithTransform
import org.jetbrains.kotlinx.dataframe.api.Update
import org.jetbrains.kotlinx.dataframe.api.frames
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.values
import org.jetbrains.kotlinx.dataframe.io.DataFrameHtmlData
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.toHTML

private fun convertToHTML(dataframeLike: Any): DataFrameHtmlData {
    fun DataFrame<*>.toHTML() = toHTML(DisplayConfiguration(), getFooter = { "" })
    fun FormattedFrame<*>.toHTML1() = toHTML(DisplayConfiguration())

    return when (dataframeLike) {
        is Pivot<*> -> DataFrameHtmlData(body = "<p>${dataframeLike::class}</p>") + dataframeLike.frames().toDataFrame().toHTML()
        is ReducedPivot<*> -> DataFrameHtmlData(body = "<p>${dataframeLike::class}</p>") + dataframeLike.values().toDataFrame().toHTML()
        is PivotGroupBy<*> -> DataFrameHtmlData(body = "<p>${dataframeLike::class}</p>") + dataframeLike.frames().toHTML()
        is ReducedPivotGroupBy<*> -> DataFrameHtmlData(body = "<p>${dataframeLike::class}</p>") + dataframeLike.values().toHTML()
        is SplitWithTransform<*, *, *> -> DataFrameHtmlData(body = "<p>${dataframeLike::class}</p>")
        is Merge<*, *, *> -> DataFrameHtmlData(body = "<p>${dataframeLike::class}</p>") + dataframeLike.into("merged").toHTML()
        is Gather<*, *, *, *> -> DataFrameHtmlData(body = "<p>${dataframeLike::class}</p>") + dataframeLike.into("key", "value").toHTML()
        is Update<*, *> -> DataFrameHtmlData(body = "<p>${dataframeLike::class}</p>")
        is Convert<*, *> -> DataFrameHtmlData(body = "<p>${dataframeLike::class}</p>")
        is FormattedFrame<*> -> dataframeLike.toHTML1()
        is GroupBy<*, *> -> DataFrameHtmlData(body = "<p>${dataframeLike::class}</p>") + dataframeLike.toDataFrame().toHTML()
        is AnyFrame -> dataframeLike.toHTML()
        is AnyCol -> dataframeLike.toDataFrame().toHTML()
        else -> throw IllegalArgumentException("Unsupported type: ${dataframeLike::class}")
    }
}

private fun convertToDescription(dataframeLike: Any): String {
    return when (dataframeLike) {
        is AnyFrame -> dataframeLike.let { "DataFrame: rowsCount = ${it.rowsCount()}, columnsCount = ${it.columnsCount()}" }
        else -> "TODO"
    }
}

annotation class Disable

fun main() {
    File("build/dataframes")
        .walkTopDown()
        .filter {
            it.nameWithoutExtension.startsWith("org.jetbrains")
        }
        // org.ClassName.functionName_properties
        // <dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.addDfs.html"/>
        .groupBy {
            it.nameWithoutExtension.substringBefore("_")
        }
        .mapValues { (name, files) ->
            val target = File("../docs/StardustDocs/snippets")
            val original = files.first()
            original.copyTo(File(target, "$name.html"), overwrite = true)
        }
}

object PluginCallback {
//    val strings = mutableListOf<String>()
//    val names = mutableListOf<String>()
//    val dfs = mutableListOf<String>()

    var i = 0
    val names = mutableMapOf<String, List<String>>()
    val expressionsByStatement = mutableMapOf<Int, List<ExpressionResult>>()

    data class ExpressionResult(val containingClassFqName: String?, val containingFunName: String?, val df: Any)

    fun start() {
        expressionsByStatement.clear()
    }

    fun save() {
        expressionsByStatement.toMap().forEach { (index, expressions) ->
            val input = expressions.first()
            val others = expressions.drop(1)
            val name = "${input.containingClassFqName}.${input.containingFunName}"

//            convertToHTML(input.df)
//                .withTableDefinitions()
//                .writeHTML(File("build/dataframes/${name}.input.html"))
//
//            val data = others.fold(DataFrameHtmlData.tableDefinitions()) { acc, expressionResult ->
//                acc + convertToHTML(expressionResult.df)
//            }

            var htmlData = DataFrameHtmlData.tableDefinitions()
            for ((i, expression) in expressions.withIndex()) {
                when (i) {
                    0 -> {
                        val table = convertToHTML(expression.df)
                        val description = table.copy(
                            body = """
                                <details>
                                <summary>Input: ${convertToDescription(expression.df)}</summary>
                                 ${table.body}
                                </details>
                            """.trimIndent()
                        )
                        htmlData += description
                    }
                    expressions.lastIndex -> {
                        val table = convertToHTML(expression.df)
                        val description = table.copy(
                            body = """
                                <details>
                                <summary>Output: ${convertToDescription(expression.df)}</summary>
                                 ${table.body}
                                </details>
                            """.trimIndent()
                        )
                        htmlData += description
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
                        htmlData += description
                    }
                }
            }

            val destination = File("build/dataframes").also {
                it.mkdirs()
            }
            htmlData.writeHTML(File(destination, "$name.html"))
        }
    }

    var action: (String, String, Any, String, String?, String?, String?, Int) -> Unit =
        @Disable { string, name, df, id, receiverId, containingClassFqName, containingFunName, statementIndex ->
            expressionsByStatement.compute(statementIndex) { _, list ->
                val element = ExpressionResult(containingClassFqName, containingFunName, df)
                list?.plus(element) ?: listOf(element)
            }
            //        strings.add(string)
            //        names.add(name)
            // Can be called with the same name multiple times, need to aggregate samples by function name somehow?
            // save schema
            val path = "$containingClassFqName.$containingFunName.html"
            // names.compute(path) {  }
            //        dfs.add(path)
            if (df is AnyFrame) {
                println(string)
                df.print()
                println(id)
                println(receiverId)
            } else {
                println(df::class)
            }
            File("build/out").let {
                val json = JsonObject(
                    mapOf(
                        "string" to string,
                        "name" to name,
                        "path" to path,
                        "id" to id,
                        "receiverId" to receiverId,
                    )
                ).toJsonString()
                it.appendText(json)
                it.appendText(",\n")
            }
            println(path)
            if (df is AnyFrame) {
                df.print()
            } else {
                println(df::class)
            }
            //        convertToHTML(df).writeHTML(File("build/dataframes/$path"))
        }

    @Disable
    fun doAction(
        string: String,
        name: String,
        df: Any,
        id: String,
        receiverId: String?,
        containingClassFqName: String?,
        containingFunName: String?,
        statementIndex: Int
    ) {
        action(string, name, df, id, receiverId, containingClassFqName, containingFunName, statementIndex)
    }
}
