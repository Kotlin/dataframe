package org.jetbrains.kotlinx.dataframe.exceptions

/**
 * Thrown when a column that an operation requires is missing in the dataframe.
 * Currently thrown by [<code>requireColumn</code>][org.jetbrains.kotlinx.dataframe.api.requireColumn];
 * the shared column resolution machinery used by other operations throws a bare [<code>IllegalStateException</code>][IllegalStateException].
 *
 * [<code>columnName</code>][columnName] carries the full requested column path, with segments joined by `/`.
 *
 * Extends [<code>IllegalStateException</code>][IllegalStateException], the type the column resolution machinery throws for a missing
 * column, so existing `catch (IllegalStateException)` code keeps working.
 *
 * Implements [<code>DataFrameError</code>][DataFrameError], so when the compiler plugin calls the operation, [<code>message</code>][message] is reported
 * as a warning instead of failing the execution.
 */
public class ColumnNotFoundException(public val columnName: String, public override val message: String) :
    IllegalStateException(),
    DataFrameError {
    public constructor(columnName: String, message: String, cause: Throwable) : this(columnName, message) {
        initCause(cause)
    }
}
