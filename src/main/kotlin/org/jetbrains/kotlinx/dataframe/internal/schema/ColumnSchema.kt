package org.jetbrains.kotlinx.dataframe.internal.schema

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.hasNulls
import org.jetbrains.kotlinx.dataframe.impl.columns.internal
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.isSupertypeOf

public abstract class ColumnSchema {

    public abstract val kind: ColumnKind

    public abstract val nullable: Boolean

    public class Value(public val type: KType) : ColumnSchema() {
        override val kind: ColumnKind = ColumnKind.Value
        override val nullable: Boolean = type.isMarkedNullable

        public fun compare(other: Value): CompareResult = when {
            type == other.type -> CompareResult.Equals
            type.isSubtypeOf(other.type) -> CompareResult.IsDerived
            type.isSupertypeOf(other.type) -> CompareResult.IsSuper
            else -> CompareResult.None
        }
    }

    public class Map(public val schema: DataFrameSchema) : ColumnSchema() {
        override val kind: ColumnKind = ColumnKind.Group
        override val nullable: Boolean = false

        public fun compare(other: Map): CompareResult = schema.compare(other.schema)
    }

    public class Frame(public val schema: DataFrameSchema, override val nullable: Boolean) : ColumnSchema() {
        public override val kind: ColumnKind = ColumnKind.Frame

        public fun compare(other: Frame): CompareResult =
            schema.compare(other.schema).combine(CompareResult.compareNullability(nullable, other.nullable))
    }

    override fun equals(other: Any?): Boolean {
        val otherType = other as? ColumnSchema ?: return false
        if (otherType.kind != kind) return false
        if (otherType.nullable != nullable) return false
        when (this) {
            is Value -> return type == (otherType as Value).type
            is Map -> return schema == (otherType as Map).schema
            is Frame -> return schema == (otherType as Frame).schema
            else -> throw NotImplementedError()
        }
    }

    public fun compare(other: ColumnSchema): CompareResult {
        if (kind != other.kind) return CompareResult.None
        if (this === other) return CompareResult.Equals
        return when (this) {
            is Value -> compare(other as Value)
            is Map -> compare(other as Map)
            is Frame -> compare(other as Frame)
            else -> throw NotImplementedError()
        }
    }
}

internal fun AnyCol.getColumnType(): ColumnSchema = when (this) {
    is ValueColumn<*> -> org.jetbrains.kotlinx.dataframe.internal.schema.ColumnSchema.Value(type)
    is ColumnGroup<*> -> org.jetbrains.kotlinx.dataframe.internal.schema.ColumnSchema.Map(df.extractSchema())
    is FrameColumn<*> -> org.jetbrains.kotlinx.dataframe.internal.schema.ColumnSchema.Frame(
        internal().schema.value,
        hasNulls
    )
    else -> throw RuntimeException()
}
