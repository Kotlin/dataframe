package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema

/** A [DataSchema] interface / class can implement this if it represents a map-like data schema (so key: value). */
@DataSchema
public interface KeyValueProperty<T> {
    // needs to be explicitly overridden in @DataSchema interface, otherwise extension functions won't generate (TODO)
    public val key: String

    // needs to be explicitly overridden in @DataSchema interface, otherwise type will be read as `T` and extensions won't generate (TODO)
    @ColumnName("value")
    public val `value`: T
}
