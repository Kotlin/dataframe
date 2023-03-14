package org.jetbrains.kotlinx.dataframe.explainer

import com.beust.klaxon.JsonObject
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.api.print
import java.io.File
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
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.html

private fun convertToHTML(dataframeLike: Any): String =
    when (dataframeLike) {
        is Pivot<*> -> "${dataframeLike::class} \n ${dataframeLike.frames().toDataFrame().html()}"
        is ReducedPivot<*> -> "${dataframeLike::class} \n ${dataframeLike.values().toDataFrame().html()}"
        is PivotGroupBy<*> -> "${dataframeLike::class} \n ${dataframeLike.frames().html()}"
        is ReducedPivotGroupBy<*> -> "${dataframeLike::class} \n ${dataframeLike.values().html()}"
        is SplitWithTransform<*, *, *> -> "<p>${dataframeLike::class}</p>"
        is Merge<*, *, *> -> "${dataframeLike::class} \n ${dataframeLike.into("merged").html()}"
        is Gather<*, *, *, *> -> "${dataframeLike::class} \n ${dataframeLike.into("key", "value").html()}"
        is Update<*, *> -> "<p>${dataframeLike::class}</p>"
        is Convert<*, *> -> "<p>${dataframeLike::class}</p>"
        is FormattedFrame<*> -> dataframeLike.toHTML(DisplayConfiguration()).toString()
        is GroupBy<*, *> -> "${dataframeLike::class} \n ${dataframeLike.toDataFrame().html()}"
        is AnyFrame -> dataframeLike.html()
        else -> throw IllegalArgumentException("Unsupported type: ${dataframeLike::class}")
    }

annotation class Disable

object PluginCallback {
//    val strings = mutableListOf<String>()
//    val names = mutableListOf<String>()
//    val dfs = mutableListOf<String>()

    var i = 0

    var action: (String, String, Any, String, String?) -> Unit = @Disable { string, name, df, id, parent ->
//        strings.add(string)
//        names.add(name)
        val path = "df${i++}.html"
//        dfs.add(path)
        if (df is AnyFrame) {
            println(string)
            df.print()
            println(id)
            println(parent)
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
                    "parent" to parent,
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
        File("build/dataframes/$path").writeText(convertToHTML(df))
    }
    @Disable
    fun doAction(string: String, name: String, df: Any, id: String, parent: String?) {
        action(string, name, df, id, parent)
    }
}
