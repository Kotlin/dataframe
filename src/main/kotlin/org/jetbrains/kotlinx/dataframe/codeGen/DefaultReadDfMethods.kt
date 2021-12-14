package org.jetbrains.kotlinx.dataframe.codeGen

public class DefaultReadJsonMethod(private val path: String) {
    public fun toDeclaration(markerName: String, visibility: String): String {
        return """
        |        ${visibility}const val defaultPath: String = "$path"
        |        ${visibility}fun readJson(path: String = defaultPath): DataFrame<$markerName> = DataFrame.readJson(path).cast()
        """.trimMargin()
    }
}
