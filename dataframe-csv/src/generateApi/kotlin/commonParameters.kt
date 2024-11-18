import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.typeNameOf
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.io.ColType

fun commonParametersOf(apiType: ApiType, inputType: InputType): Iterable<ParameterSpec> =
    listOf(
        ParameterSpec.Companion.builder(name = "delimiter", type = typeNameOf<Char>()).apply {
            defaultValue(
                when (apiType) {
                    ApiType.CSV -> "','"
                    ApiType.TSV -> "'\\t'"
                    ApiType.DELIM -> "','"
                },
            )
            addKdoc(
                "The field delimiter character. Default: ${
                    when (apiType) {
                        ApiType.CSV -> "','"
                        ApiType.TSV -> "'\\t'"
                        ApiType.DELIM -> "','"
                    }
                }.\n\n  Ignored if [hasFixedWidthColumns] is `true`.",
            )
        }.build(),
        ParameterSpec.Companion.builder("header", typeNameOf<List<String>>()).apply {
            defaultValue("emptyList()")
            addKdoc(
                """
                Optional column titles. Default: empty list.
                
                  If non-empty, the data will be read with [header] as the column titles
                  (use [skipLines] if there's a header in the data).
                  If empty (default), the header will be read from the data.
                """.trimIndent(),
            )
        }.build(),
        ParameterSpec.Companion.builder("hasFixedWidthColumns", typeNameOf<Boolean>()).apply {
            defaultValue("false")
            addKdoc(
                """
                Whether the columns have fixed width. Default: `false`.

                  Fixed-width columns can occur, for instance, in multi-space delimited data, where the columns are separated
                  by multiple spaces instead of a single delimiter, so columns are visually aligned.
                  Columns widths are determined by the header in the data (if present), or manually by setting
                  [fixedColumnWidths].
                """.trimIndent(),
            )
        }.build(),
        ParameterSpec.Companion.builder("fixedColumnWidths", typeNameOf<List<Int>>()).apply {
            defaultValue("emptyList()")
            addKdoc(
                """
                The fixed column widths. Default: empty list.

                  Requires [hasFixedWidthColumns]. If empty, the column widths will be determined by the header in the data
                  (if present), else, this manually sets the column widths.
                  The number of widths should match the number of columns.
                """.trimIndent(),
            )
        }.build(),
        ParameterSpec.Companion.builder(
            "compression",
            ClassName.Companion.bestGuess("org.jetbrains.kotlinx.dataframe.io.Compression").plusParameter(STAR),
        ).apply {
            defaultValue(
                if (inputType != InputType.INPUT_STREAM) {
                    "Compression.of(${inputType.inputParameterName})"
                } else {
                    "Compression.None"
                },
            )
            addKdoc(
                """
                The compression type of the input data. Default: [Compression.NONE].

                Can be `GZIP`, `ZIP`, or `NONE`.
                """.trimIndent(),
            )
        }.build(),
        ParameterSpec.Companion.builder("colTypes", typeNameOf<Map<String, ColType>>()).apply {
            defaultValue("emptyMap()")
            addKdoc(
                """
                The column types to use for parsing. Default: empty map.

                If empty, the column types will be inferred from the data.
                """.trimIndent(),
            )
        }.build(),
        ParameterSpec.Companion.builder("skipLines", typeNameOf<Long>()).apply {
            defaultValue("0L")
            addKdoc(
                """
                The number of lines to skip at the beginning of the input. Default: `0`.

                Useful for skipping headers or metadata.
                """.trimIndent(),
            )
        }.build(),
        ParameterSpec.Companion.builder("readLines", typeNameOf<Long?>()).apply {
            defaultValue("null")
            addKdoc(
                """
                The number of lines to read from the input. Default: `null`.

                If `null`, all lines will be read.
                """.trimIndent(),
            )
        }.build(),
        ParameterSpec.Companion.builder("parserOptions", typeNameOf<ParserOptions>()).apply {
            defaultValue("DEFAULT_PARSER_OPTIONS")
            addKdoc(
                """
                The options to use for parsing the input. Default: [DEFAULT_PARSER_OPTIONS].

                Can be used to customize the parsing behavior.
                """.trimIndent(),
            )
        }.build(),
        ParameterSpec.Companion.builder("ignoreEmptyLines", typeNameOf<Boolean>()).apply {
            defaultValue("true")
            addKdoc(
                """
                Whether to ignore empty lines in the input. Default: `true`.

                If `false`, empty lines will be treated as rows with missing values.
                """.trimIndent(),
            )
        }.build(),
        ParameterSpec.Companion.builder("allowMissingColumns", typeNameOf<Boolean>()).apply {
            defaultValue("false")
            addKdoc(
                """
                Whether to allow missing columns in the input. Default: `false`.

                If `true`, missing columns will be filled with `null` values.
                """.trimIndent(),
            )
        }.build(),
        ParameterSpec.Companion.builder("ignoreExcessColumns", typeNameOf<Boolean>()).apply {
            defaultValue("false")
            addKdoc(
                """
                Whether to ignore excess columns in the input. Default: `false`.

                If `true`, excess columns will be ignored.
                """.trimIndent(),
            )
        }.build(),
        ParameterSpec.Companion.builder("quote", typeNameOf<Char>()).apply {
            defaultValue("'\"'")
            addKdoc(
                """
                The character used for quoting fields. Default: `'"'`.

                Fields enclosed in this character will be treated as a single value.
                """.trimIndent(),
            )
        }.build(),
        ParameterSpec.Companion.builder("ignoreSurroundingSpaces", typeNameOf<Boolean>()).apply {
            defaultValue("true")
            addKdoc(
                """
                Whether to ignore surrounding spaces in fields. Default: `true`.

                If `false`, surrounding spaces will be included in the field values.
                """.trimIndent(),
            )
        }.build(),
        ParameterSpec.Companion.builder("trimInsideQuoted", typeNameOf<Boolean>()).apply {
            defaultValue("false")
            addKdoc(
                """
                Whether to trim spaces inside quoted fields. Default: `false`.

                If `true`, spaces inside quoted fields will be removed.
                """.trimIndent(),
            )
        }.build(),
        ParameterSpec.Companion.builder("parseParallel", typeNameOf<Boolean>()).apply {
            defaultValue("false")
            addKdoc(
                """
                Whether to parse the input in parallel. Default: `false`.

                If `true`, the input will be parsed using multiple threads.
                """.trimIndent(),
            )
        }.build(),
    )

fun getAdjustCsvSpecsParameter() =
    ParameterSpec.Companion.builder(
        "adjustCsvSpecs",
        ClassName.bestGuess("org.jetbrains.kotlinx.dataframe.io.AdjustCsvSpecs"),
    ).apply {
        defaultValue("{ it }")
        addKdoc(
            """
            Optional extra [CsvSpecs] configuration. Default: `{ it }`.

              Before instantiating the [CsvSpecs], the [CsvSpecs.Builder] will be passed to this lambda.
              This will allow you to configure/overwrite any CSV / TSV parsing options.
            """.trimIndent(),
        )
    }.build()
