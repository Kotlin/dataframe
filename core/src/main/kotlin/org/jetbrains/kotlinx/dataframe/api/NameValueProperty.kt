@file:Suppress("UNCHECKED_CAST", "DEPRECATION")

package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.util.KEY_VALUE_PROPERTY
import org.jetbrains.kotlinx.dataframe.util.KEY_VALUE_PROPERTY_KEY

/** A [DataSchema] interface / class can implement this if it represents a map-like data schema (so name: value). */
@DataSchema
public interface NameValueProperty<T> {
    // needs to be explicitly overridden in @DataSchema interface, otherwise extension functions won't generate (TODO)
    public val name: String

    // needs to be explicitly overridden in @DataSchema interface, otherwise type will be read as `T` and extensions won't generate (TODO)
    @ColumnName("value")
    public val `value`: T
}

// region Deprecated

/** A [DataSchema] interface / class can implement this if it represents a map-like data schema (so key: value). */
@Deprecated(KEY_VALUE_PROPERTY, ReplaceWith("NameValueProperty<T>"))
public interface KeyValueProperty<T> {
    // needs to be explicitly overridden in @DataSchema interface, otherwise extension functions won't generate (TODO)
    public val key: String

    // needs to be explicitly overridden in @DataSchema interface, otherwise type will be read as `T` and extensions won't generate (TODO)
    @ColumnName("value")
    public val `value`: T
}

@Deprecated(KEY_VALUE_PROPERTY_KEY, ReplaceWith("name"))
public val ColumnsScope<KeyValueProperty<*>>.key: DataColumn<String>
    @JvmName("KeyValueProperty_key")
    get() = this["key"].cast()

@Deprecated(KEY_VALUE_PROPERTY_KEY, ReplaceWith("name"))
public val ColumnsScope<KeyValueProperty<*>?>.key: DataColumn<String?>
    @JvmName("NullableKeyValueProperty_key")
    get() = this["key"].cast()

@Deprecated(KEY_VALUE_PROPERTY_KEY, ReplaceWith("name"))
public val DataRow<KeyValueProperty<*>>.key: String
    @JvmName("KeyValueProperty_key")
    get() = this["key"] as String

@Deprecated(KEY_VALUE_PROPERTY_KEY, ReplaceWith("name"))
public val DataRow<KeyValueProperty<*>?>.key: String?
    @JvmName("NullableKeyValueProperty_key")
    get() = this["key"] as String?

/**
 * Accesses the 'key' column of this [KeyValueProperty][KeyValueProperty]
 * [ColumnsContainer][org.jetbrains.kotlinx.dataframe.ColumnsContainer].
 *
 * This is a temporary, future-proof, extension property in preparation of the migration from [KeyValueProperty]
 * to [NameValueProperty].
 */
public val ColumnsScope<KeyValueProperty<*>>.name: DataColumn<String>
    @JvmName("KeyValueProperty_name")
    get() = key

/**
 * Accesses the 'key' column of this [KeyValueProperty][KeyValueProperty]
 * [ColumnsContainer][org.jetbrains.kotlinx.dataframe.ColumnsContainer].
 *
 * This is a temporary, future-proof, extension property in preparation of the migration from [KeyValueProperty]
 * to [NameValueProperty].
 */
public val ColumnsScope<KeyValueProperty<*>?>.name: DataColumn<String?>
    @JvmName("NullableKeyValueProperty_name")
    get() = key

/**
 * Accesses the 'key' value of this [KeyValueProperty][KeyValueProperty]
 * [DataRow][DataRow].
 *
 * This is a temporary, future-proof, extension property in preparation of the migration from [KeyValueProperty]
 * to [NameValueProperty].
 */
public val DataRow<KeyValueProperty<*>>.name: String
    @JvmName("KeyValueProperty_name")
    get() = key

/**
 * Accesses the 'key' value of this [KeyValueProperty][KeyValueProperty]
 * [DataRow][DataRow].
 *
 * This is a temporary, future-proof, extension property in preparation of the migration from [KeyValueProperty]
 * to [NameValueProperty].
 */
public val DataRow<KeyValueProperty<*>?>.name: String?
    @JvmName("NullableKeyValueProperty_name")
    get() = key

public val <T : Any?> ColumnsScope<KeyValueProperty<T>>.value: DataColumn<T>
    @JvmName("KeyValueProperty_value")
    get() = this["value"].cast()

public val <T : Any?> ColumnsScope<KeyValueProperty<T>?>.value: DataColumn<T?>
    @JvmName("NullableKeyValueProperty_value")
    get() = this["value"].cast()

public val <T : Any?> DataRow<KeyValueProperty<T>>.value: T
    @JvmName("KeyValueProperty_value")
    get() = this["value"] as T

public val <T : Any?> DataRow<KeyValueProperty<T>?>.value: T?
    @JvmName("NullableKeyValueProperty_value")
    get() = this["value"] as T?

// endregion
