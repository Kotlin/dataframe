package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.KeyValueProperty

/**
 * A [DataSchema] interface can implement this if it represents a map-like data schema (so key: value).
 * Used in OpenAPI to represent objects with 'just' additionalProperties of a certain type.
 */
public interface AdditionalProperty<T> : KeyValueProperty<T> {

    /** Key of the property. */
    override val key: String

    /** Value of the property. */
    override val value: T

    public companion object
}
