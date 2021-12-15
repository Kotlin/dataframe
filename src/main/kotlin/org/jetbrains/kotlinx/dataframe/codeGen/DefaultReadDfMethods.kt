package org.jetbrains.kotlinx.dataframe.codeGen

public interface DefaultReadDfMethod {
    public fun toDeclaration(markerName: String, visibility: String): String
}

public class DefaultReadJsonMethod(private val path: String) : DefaultReadDfMethod {
    public override fun toDeclaration(markerName: String, visibility: String): String {
        return """
        |        ${visibility}const val defaultPath: String = "$path"
        |        ${visibility}fun readJson(path: String = defaultPath): DataFrame<$markerName> = DataFrame.readJson(path).cast()
        """.trimMargin()
    }
}

public class DefaultReadCsvMethod(private val path: String, private val delimiter: Char) : DefaultReadDfMethod {
    public override fun toDeclaration(markerName: String, visibility: String): String {
        return """
        |        ${visibility}const val defaultPath: String = "$path"
        |        ${visibility}const val defaultDelimiter: Char = '$delimiter'
        |        ${visibility}fun readCsv(path: String = defaultPath, delimiter: Char = defaultDelimiter): DataFrame<$markerName> {
        |            return DataFrame.readCSV(path, delimiter).cast()
        |        }
        """.trimMargin()
    }
}
