import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec

internal fun generateReadFile(
    nameCapitalized: String,
    fileTypeTitle: String,
    fileType: String,
    apiType: ApiType,
): FileSpec =
    FileSpec.Companion.builder(
        packageName = "org.jetbrains.kotlinx.dataframe.io",
        fileName = "read${nameCapitalized}2",
    ).apply {
        addFileComment()
        for (inputType in InputType.entries) {
            val dataTitle = inputType.dataTitleStr
            val data = inputType.dataStr

            addFunction(
                FunSpec.Companion.builder(name = "read${nameCapitalized}2").apply {
                    addKdoc(
                        generateReadKdoc(fileTypeTitle, dataTitle, fileType, data),
                    )

                    addCommonFunctionAttributes()
                    parameters += inputParameterOf(inputType)
                    parameters += commonParametersOf(apiType, inputType)

                    // only add it for the input stream overload
                    if (inputType == InputType.INPUT_STREAM) {
                        parameters += getAdjustCsvSpecsParameter()
                    }

                    addCode(
                        CodeBlock.Companion.builder().apply {
                            add("return ")
                            val inputParameterName = inputType.inputParameterName
                            beginControlFlow(
                                when (inputType) {
                                    InputType.PATH -> "java.nio.file.Files.newInputStream($inputParameterName).use"
                                    InputType.FILE -> "java.io.FileInputStream($inputParameterName).use"
                                    InputType.URL -> "catchHttpResponse($inputParameterName)"
                                    InputType.FILE_OR_URL -> "catchHttpResponse(asUrl(fileOrUrl = $inputParameterName))"
                                    InputType.INPUT_STREAM -> "$inputParameterName.let"
                                },
                            )
                            addStatement(
                                """
                                org.jetbrains.kotlinx.dataframe.impl.io.readDelimImpl(
                                    inputStream = it,
                                    delimiter = delimiter,
                                    header = header,
                                    hasFixedWidthColumns = hasFixedWidthColumns,
                                    fixedColumnWidths = fixedColumnWidths,
                                    compression = compression,
                                    colTypes = colTypes,
                                    skipLines = skipLines,
                                    readLines = readLines,
                                    parserOptions = parserOptions,
                                    ignoreEmptyLines = ignoreEmptyLines,
                                    allowMissingColumns = allowMissingColumns,
                                    ignoreExcessColumns = ignoreExcessColumns,
                                    quote = quote,
                                    ignoreSurroundingSpaces = ignoreSurroundingSpaces,
                                    trimInsideQuoted = trimInsideQuoted,
                                    parseParallel = parseParallel,
                                    adjustCsvSpecs = ${
                                    if (inputType == InputType.INPUT_STREAM) {
                                        "adjustCsvSpecs"
                                    } else {
                                        "org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ADJUST_CSV_SPECS"
                                    }
                                },
                                )
                                """.trimIndent(),
                            )
                            endControlFlow()
                        }.build(),
                    )
                }.build(),
            )
        }
    }.build()

