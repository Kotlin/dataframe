package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.NameValueProperty
import org.jetbrains.kotlinx.dataframe.api.cast

/**
 * A [DataSchema] interface can implement this if it represents a map-like data schema (so name: value).
 * Used in OpenAPI to represent objects with 'just' additionalProperties of a certain type.
 */
public interface AdditionalProperty<T> : NameValueProperty<T> {

    /** Key of the property. */
    override val name: String

    /** Value of the property. */
    override val value: T

    public companion object
}

private const val DEPRECATION_MESSAGE = "'key' has been renamed to 'name'."

@Deprecated(DEPRECATION_MESSAGE, ReplaceWith("name"))
public val ColumnsScope<AdditionalProperty<*>>.key: DataColumn<String>
    @JvmName("AdditionalProperty_key")
    get() = get("name").cast()

@Deprecated(DEPRECATION_MESSAGE, ReplaceWith("name"))
public val ColumnsScope<AdditionalProperty<*>?>.key: DataColumn<String?>
    @JvmName("NullableAdditionalProperty_key")
    get() = get("name").cast()

@Deprecated(DEPRECATION_MESSAGE, ReplaceWith("name"))
public val DataRow<AdditionalProperty<*>>.key: String
    @JvmName("AdditionalProperty_key")
    get() = get("name") as String

@Deprecated(DEPRECATION_MESSAGE, ReplaceWith("name"))
public val DataRow<AdditionalProperty<*>?>.key: String?
    @JvmName("NullableAdditionalProperty_key")
    get() = get("name") as String?

@Deprecated(DEPRECATION_MESSAGE, ReplaceWith("name"))
public val AdditionalProperty<*>.key: String
    get() = name
