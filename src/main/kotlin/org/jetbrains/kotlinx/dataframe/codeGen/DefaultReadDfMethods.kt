package org.jetbrains.kotlinx.dataframe.codeGen

public interface DefaultReadDfMethod {
    public fun toDeclaration(markerName: String, visibility: String): String
}

public class DefaultReadJsonMethod(private val path: String?) : DefaultReadDfMethod {
    public override fun toDeclaration(markerName: String, visibility: String): String {
        return buildString {
            if (path != null) {
                append("""        ${visibility}const val defaultPath: String = "${path.escapeStringLiteral()}"""")
                appendLine()
            }
            val defaultPathClause = if (path != null) " = defaultPath" else ""
            append("        ${visibility}fun readJson(path: String$defaultPathClause): DataFrame<")
            append(markerName)
            append("> = DataFrame.readJson(path).cast()")
        }
    }
}

public class DefaultReadCsvMethod(private val path: String?, private val delimiter: Char) : DefaultReadDfMethod {
    public override fun toDeclaration(markerName: String, visibility: String): String {
        return buildString {
            if (path != null) {
                append("""        ${visibility}const val defaultPath: String = "${path.escapeStringLiteral()}"""")
                appendLine()
            }
            append("        ${visibility}const val defaultDelimiter: Char = '$delimiter'\n")
            val defaultPathClause = if (path != null) " = defaultPath" else ""
            append("        ${visibility}fun readCSV(path: String$defaultPathClause, delimiter: Char = defaultDelimiter): DataFrame<")
            append(markerName)
            append("> {\n            return DataFrame.readCSV(path, delimiter).cast()\n        }")
        }
    }
}

private fun String.escapeStringLiteral(): String = replace("\\", "\\\\")
    .replace("$", "\\\$")
    .replace("\"", "\\\"")
