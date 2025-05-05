package org.jetbrains.kotlinx.dataframe.exceptions

/**
 * If DataFrame function used by compiler plugin as implementation detail throws this exception, [message] will be reported as warning
 */
public interface DataFrameException {
    public val message: String
}
