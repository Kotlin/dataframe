package org.jetbrains.kotlinx.dataframe.schema

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.LENIENT
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.typeOf

/**
 * Describes a single column of a [DataFrameSchema]: what [kind] of column it is, what [type] it holds,
 * and, for the two nested kinds, the schema of what is inside it.
 *
 * There is one subclass per [column kind][ColumnKind]:
 *
 * - [Value] — a [value column][ValueColumn], described by the common [type] of its values.
 * - [Group] — a [column group][ColumnGroup], described by the [schema][Group.schema] of its nested columns.
 * - [Frame] — a [frame column][FrameColumn], described by the [schema][Frame.schema] of the dataframes it holds.
 */
public sealed class ColumnSchema {

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

    /** The schema of a [value column][ValueColumn]. */
    public class Value(public override val type: KType) : ColumnSchema() {
        override val kind: ColumnKind = ColumnKind.Value
        override val nullable: Boolean = type.isMarkedNullable
        override val contentType: KType? = null

        public fun compare(other: Value, comparisonMode: ComparisonMode = LENIENT): CompareResult =
            when {
                type == other.type -> CompareResult.Matches
                comparisonMode == STRICT -> CompareResult.None
                type.isSubtypeOf(other.type) -> CompareResult.IsDerived
                type.isSupertypeOf(other.type) -> CompareResult.IsSuper
                else -> CompareResult.None
            }
    }

    /**
     * The schema of a [column group][ColumnGroup].
     *
     * @property [schema] The [DataFrameSchema] of the columns nested in the group.
     */
    public class Group(public val schema: DataFrameSchema, override val contentType: KType?) : ColumnSchema() {
        override val kind: ColumnKind = ColumnKind.Group

        /** A column group is never null, instead, make the columns inside nullable. */
        override val nullable: Boolean = false
        override val type: KType get() = typeOf<AnyRow>()

        public fun compare(other: Group, comparisonMode: ComparisonMode = LENIENT): CompareResult =
            schema.compare(
                other = other.schema,
                comparisonMode = comparisonMode,
            )
    }

    /**
     * The schema of a [frame column][FrameColumn].
     *
     * @property [schema] The [DataFrameSchema] of the dataframes held by the column.
     */
    public class Frame(
        public val schema: DataFrameSchema,
        override val nullable: Boolean,
        override val contentType: KType?,
    ) : ColumnSchema() {
        public override val kind: ColumnKind = ColumnKind.Frame
        override val type: KType get() = typeOf<AnyFrame>()

        public fun compare(other: Frame, comparisonMode: ComparisonMode = LENIENT): CompareResult =
            schema.compare(
                other = other.schema,
                comparisonMode = comparisonMode,
            ) + CompareResult.compareNullability(thisIsNullable = nullable, otherIsNullable = other.nullable)
    }

    /** Checks equality by kind, type, or schema */
    override fun equals(other: Any?): Boolean {
        val otherType = other as? ColumnSchema ?: return false
        if (otherType.kind != kind) return false
        if (otherType.nullable != nullable) return false
        return when (this) {
            is Value -> type == (otherType as Value).type
            is Group -> schema == (otherType as Group).schema
            is Frame -> schema == (otherType as Frame).schema
        }
    }

    /**
     * Compares this column schema with the [other] column schema.
     *
     * Column schemas of different [kinds][kind] are never comparable: the result is then
     * [CompareResult.None], whatever the [comparisonMode] is.
     *
     * For [Value] the [types][type] are compared, for [Group] and [Frame] their
     * [DataFrameSchema]s. How strict that comparison is, is decided by the [comparisonMode];
     * see [ComparisonMode] for what each mode means.
     *
     * @param [other] The column schema to compare this one with.
     * @param [comparisonMode] The [mode][ComparisonMode] to compare the column schemas by.
     * @return a [CompareResult] that indicates whether this column schema compared to [other] is
     *   [matching][CompareResult.Matches], [derived][CompareResult.IsDerived],
     *   [superset][CompareResult.IsSuper], or [incomparable][CompareResult.None].
     */
    public fun compare(other: ColumnSchema, comparisonMode: ComparisonMode = LENIENT): CompareResult {
        if (kind != other.kind) return CompareResult.None
        if (this === other) return CompareResult.Matches
        return when (this) {
            is Value -> compare(other as Value, comparisonMode)
            is Group -> compare(other as Group, comparisonMode)
            is Frame -> compare(other as Frame, comparisonMode)
        }
    }

    override fun hashCode(): Int {
        var result = nullable.hashCode()
        result = 31 * result + kind.hashCode()
        result = 31 * result + when (this) {
            is Value -> type.hashCode()
            is Group -> schema.hashCode()
            is Frame -> schema.hashCode()
        }
        return result
    }
}
