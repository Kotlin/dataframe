import com.squareup.kotlinpoet.FileSpec

internal fun generateWriteFile(
    nameCapitalized: String,
    fileTypeTitle: String,
    fileType: String,
    apiType: ApiType,
): FileSpec =
    FileSpec.Companion.builder(
        packageName = "org.jetbrains.kotlinx.dataframe.io",
        fileName = "write${nameCapitalized}2",
    ).apply {
        addFileComment()
    }
        .build()
