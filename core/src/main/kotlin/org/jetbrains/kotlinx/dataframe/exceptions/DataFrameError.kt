package org.jetbrains.kotlinx.dataframe.exceptions

/**
 * If DataFrame function used by compiler plugin as implementation detail throws exception
 * that implements this interface, [message] will be reported as warning
 */
public interface DataFrameError {
    public val message: String
}
