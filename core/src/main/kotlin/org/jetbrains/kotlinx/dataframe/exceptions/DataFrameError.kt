package org.jetbrains.kotlinx.dataframe.exceptions

/**
 * If a DataFrame function used by the compiler plugin as an implementation detail throws an exception
 * that implements this interface, [message] will be reported as a warning
 */
public interface DataFrameError {
    public val message: String
}
