package org.jetbrains.kotlinx.dataframe.api

/**
 * Make your enum class inherit [DataSchemaEnum] to
 * make String -> Enum and Enum -> String conversions work
 * using [value] instead of the enum name.
 * (Fallback to enum name if the value cannot be found is implemented)
 */
public interface DataSchemaEnum {
    public val value: String
}
