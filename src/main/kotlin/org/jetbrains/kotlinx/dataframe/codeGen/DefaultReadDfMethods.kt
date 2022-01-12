package org.jetbrains.kotlinx.dataframe.codeGen

public interface DefaultReadDfMethod {
    public fun toDeclaration(markerName: String, visibility: String): String
}

// Used APIs
private const val cast = "cast"
private const val verify = "verify" // cast(true) is obscure, i think it's better to use named argument here
private const val readCSV = "readCSV"
private const val readJson = "readJson"

public class DefaultReadJsonMethod(private val path: String?) : DefaultReadDfMethod {
    public override fun toDeclaration(markerName: String, visibility: String): String {
        return buildString {
            if (path != null) {
                append("""        ${visibility}const val defaultPath: String = "${path.escapeStringLiteral()}"""")
                appendLine()
            }
            val defaultPathClause = if (path != null) " = defaultPath" else ""
            append(
                """
                |        ${visibility}fun $readJson(path: String$defaultPathClause, $verify: Boolean? = null): DataFrame<$markerName> { 
                |            val df = DataFrame.$readJson(path)
                |            return if ($verify != null) df.$cast($verify = $verify) else df.$cast()
                |        }
                """.trimMargin()
            )
        }
    }
}

public class DefaultReadCsvMethod(private val path: String?, private val csvOptions: CsvOptions) : DefaultReadDfMethod {
    public override fun toDeclaration(markerName: String, visibility: String): String {
        val (delimiter) = csvOptions
        return buildString {
            if (path != null) {
                append("""        ${visibility}const val defaultPath: String = "${path.escapeStringLiteral()}"""")
                appendLine()
            }
            append("        ${visibility}const val defaultDelimiter: Char = '$delimiter'\n")
            val defaultPathClause = if (path != null) " = defaultPath" else ""
            append(
                """
                |        ${visibility}fun $readCSV(
                |            path: String$defaultPathClause,
                |            delimiter: Char = defaultDelimiter,
                |            $verify: Boolean? = null
                |        ): DataFrame<$markerName> { 
                |            val df = DataFrame.$readCSV(path, delimiter)
                |            return if ($verify != null) df.$cast($verify = $verify) else df.$cast()
                |        }
                """.trimMargin()
            )
        }
    }
}

public data class CsvOptions(val delimiter: Char)

private fun String.escapeStringLiteral(): String = replace("\\", "\\\\")
    .replace("$", "\\\$")
    .replace("\"", "\\\"")
