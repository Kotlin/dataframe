package org.jetbrains.kotlinx.dataframe.io

/**
 * Defines quoting behavior.
 */
public enum class QuoteMode {

    /**
     * Quotes all fields.
     */
    ALL,

    /**
     * Quotes all non-null fields.
     */
    ALL_NON_NULL,

    /**
     * Quotes fields that contain special characters such as a field delimiter, quote character, or any of the
     * characters in the line separator string.
     */
    MINIMAL,

    /**
     * Quotes all non-numeric fields.
     */
    NON_NUMERIC,

    /**
     * Never quotes fields. When the delimiter occurs in data, the printer prefixes it with the escape character. If the
     * escape character is not set, format validation throws an exception.
     */
    NONE,
}
