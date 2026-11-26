package org.jetbrains.kotlinx.dataframe.api

/**
 * Make your enum class inherit [<code>DataSchemaEnum</code>][DataSchemaEnum] to
 * make String -> Enum and Enum -> String conversions work
 * using [<code>value</code>][value] instead of the enum name.
 * (Fallback to enum name if the value cannot be found is implemented)
 */
public interface DataSchemaEnum {
    public val value: String
}
