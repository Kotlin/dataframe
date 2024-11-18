import com.squareup.kotlinpoet.FileSpec

internal fun generateToStrFile(
    nameCapitalized: String,
    fileTypeTitle: String,
    fileType: String,
    apiType: ApiType,
): FileSpec =
    FileSpec.Companion.builder(
        packageName = "org.jetbrains.kotlinx.dataframe.io",
        fileName = "to${nameCapitalized}Str2",
    ).apply {
        addFileComment()
    }
        .build()
