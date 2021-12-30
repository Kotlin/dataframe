package org.jetbrains.kotlinx.dataframe.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FILE)
@Repeatable
public annotation class ImportDataSchema(
    val name: String,
    val url: String,
    val visibility: DataSchemaVisibility = DataSchemaVisibility.IMPLICIT_PUBLIC,
    val normalizationDelimiters: CharArray = ['\t', ' ', '_'],
    val withDefaultPath: Boolean = true,
    val csvOptions: CsvOptions = CsvOptions(',')
)

public enum class DataSchemaVisibility {
    INTERNAL, IMPLICIT_PUBLIC, EXPLICIT_PUBLIC
}

public annotation class CsvOptions(
    val delimiter: Char
)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FILE)
@Repeatable
public annotation class ImportDataSchemaByAbsolutePath(
    val name: String,
    val absolutePath: String,
    val visibility: DataSchemaVisibility = DataSchemaVisibility.IMPLICIT_PUBLIC,
    val normalizationDelimiters: CharArray = ['\t', ' ', '_'],
    val withDefaultPath: Boolean = true,
    val csvOptions: CsvOptions = CsvOptions(',')
)
