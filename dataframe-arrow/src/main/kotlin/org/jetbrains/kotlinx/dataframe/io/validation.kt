package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.exceptions.CellConversionException
import org.jetbrains.kotlinx.dataframe.exceptions.TypeConverterNotFoundException

/**
 * Detailed message about any mismatch when saving to Arrow format with user-defined schema that does not match with actual data.
 * Can be sent to callback, written to log or encapsulated to exception
 */
public sealed class ConvertingMismatch(
    /**Name of the column with mismatch*/
    public open val column: String,
    /**Number of first row with mismatch (0-based) if defined*/
    public open val row: Int?,
    /**Original exception if exist*/
    public open val cause: Exception?
) {

    public sealed class WideningMismatch(column: String): ConvertingMismatch(column, null, null) {
        public data class AddedColumn(override val column: String): WideningMismatch(column) {
            override fun toString(): String = "Added column \"$column\" not described in target schema"
        }
        public data class RejectedColumn(override val column: String): WideningMismatch(column) {
            override fun toString(): String = "Column \"$column\" is not described in target schema and was ignored"
        }
    }
    public sealed class NarrowingMismatch(column: String): ConvertingMismatch(column, null, null) {
        public data class NotPresentedColumnIgnored(override val column: String): NarrowingMismatch(column) {
            override fun toString(): String = "Not nullable column \"$column\" is not presented in actual data, saving as is"
        }
        public data class NotPresentedColumnError(override val column: String): NarrowingMismatch(column) {
            override fun toString(): String = "Not nullable column \"$column\" is not presented in actual data, can not save"
        }
    }
    public sealed class TypeConversionNotFound(column: String, cause: TypeConverterNotFoundException): ConvertingMismatch(column, null, cause) {
        public data class ConversionNotFoundIgnored(override val column: String, override val cause: TypeConverterNotFoundException): TypeConversionNotFound(column, cause) {
            override fun toString(): String = "${cause.message} for column \"$column\", saving as is"
        }
        public data class ConversionNotFoundError(override val column: String, val e: TypeConverterNotFoundException): TypeConversionNotFound(column, e) {
            override fun toString(): String = "${e.message} for column \"$column\", can not save"
        }
    }
    public sealed class TypeConversionFail(column: String, row: Int?, public override val cause: CellConversionException): ConvertingMismatch(column, row, cause) {
        public data class ConversionFailIgnored(override val column: String, override val row: Int?, override val cause: CellConversionException): TypeConversionFail(column, row, cause) {
            override fun toString(): String = "${cause.message}, saving as is"
        }
        public data class ConversionFailError(override val column: String, override val row: Int?, override val cause: CellConversionException): TypeConversionFail(column, row, cause) {
            override fun toString(): String = "${cause.message}, can not save"
        }
    }
    public data class SavedAsString(override val column: String, val type: Class<*>): ConvertingMismatch(column, null, null) {
        override fun toString(): String = "Column \"$column\" has type ${type.canonicalName}, will be saved as String\""
    }
    public sealed class NullableMismatch(column: String, row: Int?): ConvertingMismatch(column, row, null) {
        public data class NullValueIgnored(override val column: String, override val row: Int?): NullableMismatch(column, row) {
            override fun toString(): String = "Column \"$column\" contains nulls in row $row but expected not nullable, saving as is"
        }
        public data class NullValueError(override val column: String, override val row: Int?): NullableMismatch(column, row) {
            override fun toString(): String = "Column \"$column\" contains nulls in row $row but expected not nullable, can not save"
        }
    }
}

public class ConvertingException(public val mismatchCase: ConvertingMismatch): IllegalArgumentException(mismatchCase.toString(), mismatchCase.cause)
