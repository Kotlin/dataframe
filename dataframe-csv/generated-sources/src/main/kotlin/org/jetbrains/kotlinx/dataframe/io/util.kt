package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.api.ParserOptions

/**
 * Default strings that are considered null:
 *
 * [["", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil"]][org.jetbrains.kotlinx.dataframe.io.DEFAULT_NULL_STRINGS]
 */
public val DEFAULT_NULL_STRINGS: Set<String> =
    setOf("", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil")

/**
 * Default parsing options for reading delimited data like CSV and TSV.
 *
 * By default, we use [nullStrings][ParserOptions.nullStrings]`  =  `
 * [["", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil"]][org.jetbrains.kotlinx.dataframe.io.DEFAULT_NULL_STRINGS] and
 * [useFastDoubleParser][ParserOptions.useFastDoubleParser]` = true`.
 */
public val DEFAULT_PARSER_OPTIONS: ParserOptions =
    ParserOptions(nullStrings = DEFAULT_NULL_STRINGS, useFastDoubleParser = true)
