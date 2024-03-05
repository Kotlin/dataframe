package org.jetbrains.kotlinx.dataframe.schema

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.typeOf

public abstract class ColumnSchema {

    /** Either [Value] or [Group] or [Frame]. */
    public abstract val kind: ColumnKind

    /** Whether the column is nullable. Is always `false` for [Group]. */
    public abstract val nullable: Boolean

    /**
     * The type of the column.
     * For [Value] this is the common base type associated with all the values in the column.
     * For [Group] this is [AnyRow].
     * For [Frame] this is [AnyFrame].
     */
    public abstract val type: KType

    /**
     * The type associated with the contents of the column.
     * For [Value] this is irrelevant and thus `null`. Use [type] instead.
     * For [Group] this is the common base type associated with all the [DataRow]s in the column.
     * For [Frame] this is the common base type associated with all the [DataFrame]s in the column.
     */
    public abstract val contentType: KType?

    public class Value(public override val type: KType) : ColumnSchema() {
        override val kind: ColumnKind = ColumnKind.Value
        override val nullable: Boolean = type.isMarkedNullable
        override val contentType: KType? = null

        public fun compare(other: Value): CompareResult = when {
            type == other.type -> CompareResult.Equals
            type.isSubtypeOf(other.type) -> CompareResult.IsDerived
            type.isSupertypeOf(other.type) -> CompareResult.IsSuper
            else -> CompareResult.None
        }
    }

    public class Group(public val schema: DataFrameSchema, override val contentType: KType?) : ColumnSchema() {
        override val kind: ColumnKind = ColumnKind.Group

        /** A column group is never null, instead, make the columns inside nullable. */
        override val nullable: Boolean = false
        override val type: KType get() = typeOf<AnyRow>()

        public fun compare(other: Group): CompareResult = schema.compare(other.schema)
    }

    public class Frame(
        public val schema: DataFrameSchema,
        override val nullable: Boolean,
        override val contentType: KType?,
    ) : ColumnSchema() {
        public override val kind: ColumnKind = ColumnKind.Frame
        override val type: KType get() = typeOf<AnyFrame>()

        public fun compare(other: Frame): CompareResult =
            schema.compare(other.schema).combine(CompareResult.compareNullability(nullable, other.nullable))
    }

    /** Checks equality just on kind, type, or schema. */
    override fun equals(other: Any?): Boolean {
        val otherType = other as? ColumnSchema ?: return false
        if (otherType.kind != kind) return false
        if (otherType.nullable != nullable) return false
        return when (this) {
            is Value -> type == (otherType as Value).type
            is Group -> schema == (otherType as Group).schema
            is Frame -> schema == (otherType as Frame).schema
            else -> throw NotImplementedError()
        }
    }

    public fun compare(other: ColumnSchema): CompareResult {
        if (kind != other.kind) return CompareResult.None
        if (this === other) return CompareResult.Equals
        return when (this) {
            is Value -> compare(other as Value)
            is Group -> compare(other as Group)
            is Frame -> compare(other as Frame)
            else -> throw NotImplementedError()
        }
    }
}
