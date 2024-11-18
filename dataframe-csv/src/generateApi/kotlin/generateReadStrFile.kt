import com.squareup.kotlinpoet.FileSpec

internal fun generateReadStrFile(
    nameCapitalized: String,
    fileTypeTitle: String,
    fileType: String,
    apiType: ApiType,
): FileSpec =
    FileSpec.Companion.builder(
        packageName = "org.jetbrains.kotlinx.dataframe.io",
        fileName = "read${nameCapitalized}Str2",
    ).apply {
        addFileComment()
    }
        .build()
