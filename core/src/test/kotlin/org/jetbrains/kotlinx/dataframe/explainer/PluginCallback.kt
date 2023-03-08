package org.jetbrains.kotlinx.dataframe.explainer

import com.beust.klaxon.JsonObject
import org.jetbrains.kotlinx.dataframe.AnyFrame
import java.io.File

annotation class Disable

object PluginCallback {
//    val strings = mutableListOf<String>()
//    val names = mutableListOf<String>()
//    val dfs = mutableListOf<String>()

    var i = 0

    var action: (String, String, AnyFrame, String, String?) -> Unit = @Disable { string, name, df, id, parent ->
//        strings.add(string)
//        names.add(name)
        val path = "df${i++}.html"
//        dfs.add(path)
        File("out").let {
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
//        File("dataframes/$path").writeText(df.html())
    }
    @Disable
    fun doAction(string: String, name: String, df: AnyFrame, id: String, parent: String?) {
        action(string, name, df, id, parent)
    }
}
