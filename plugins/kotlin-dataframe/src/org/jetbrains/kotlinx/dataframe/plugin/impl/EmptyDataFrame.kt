package org.jetbrains.kotlinx.dataframe.plugin.impl

import org.jetbrains.kotlin.fir.backend.Fir2IrComponents
import org.jetbrains.kotlin.fir.backend.toIrType
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.types.toKotlinType
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.plugin.extensions.KotlinTypeFacade
import org.jetbrains.kotlinx.dataframe.size
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType

class EmptyDataFrame(
    df: AnyFrame,
) : DataFrame<Any?> by df {
    init {
        require(df.isEmptyDataFrame()) { "Not an 'empty' dataframe" }
    }
}

fun AnyFrame.asEmptyDataFrame(): EmptyDataFrame = EmptyDataFrame(this)

class EmptyColumn(
    col: BaseColumn<*>,
) : BaseColumn<Any?> by col {
    init {
        require(col.isEmptyColumn()) { "Not an 'empty' column" }
    }
}

fun BaseColumn<*>.asEmptyColumn(): EmptyColumn = EmptyColumn(this)

fun AnyFrame.isEmptyDataFrame(): Boolean = rowsCount() == 0

fun BaseColumn<*>.isEmptyColumn(): Boolean =
    when (kind()) {
        Value -> (this as ValueColumn<*>).size() == 0
        Group -> (this as ColumnGroup<*>).size() == 0
        Frame -> (this as FrameColumn<*>).single().columns().all { it.isEmptyColumn() } && size() == 1
    }

@JvmName("emptyColumnToSimpleCol")
fun EmptyColumn.toSimpleCol(kotlinTypeFacade: KotlinTypeFacade): SimpleCol =
    when (kind()) {
        Value ->
            SimpleDataColumn(
                name = name(),
                type =
                    (this as ValueColumn<*>).type().let {
                        kotlinTypeFacade.from(it)
                    },
            )

        Group ->
            SimpleColumnGroup(
                name = name(),
                columns =
                    (this as ColumnGroup<*>).columns().map {
                        it.asEmptyColumn().toSimpleCol(kotlinTypeFacade)
                    },
            )

        Frame ->
            SimpleFrameColumn(
                name = name(),
                columns =
                    (this as FrameColumn<*>).first().columns().map {
                        it.asEmptyColumn().toSimpleCol(kotlinTypeFacade)
                    },
            )
    }

fun EmptyDataFrame.toPluginDataFrameSchema(kotlinTypeFacade: KotlinTypeFacade): PluginDataFrameSchema =
    PluginDataFrameSchema(
        columns().map {
            (it as EmptyColumn).toSimpleCol(kotlinTypeFacade)
        },
    )

fun PluginDataFrameSchema.toEmptyDataFrame(kotlinTypeFacade: KotlinTypeFacade): EmptyDataFrame =
    dataFrameOf(columns().map { it.toEmptyColumn(kotlinTypeFacade) })
        .asEmptyDataFrame()

fun PluginDataFrameSchema.processAsEmptyDataFrame(
    block: AnyFrame.() -> AnyFrame,
    kotlinTypeFacade: KotlinTypeFacade,
): PluginDataFrameSchema =
    toEmptyDataFrame(kotlinTypeFacade)
        .block()
        .asEmptyDataFrame()
        .toPluginDataFrameSchema(kotlinTypeFacade)

@OptIn(ObsoleteDescriptorBasedAPI::class)
fun SimpleCol.toEmptyColumn(kotlinTypeFacade: KotlinTypeFacade): EmptyColumn {
    val c: Fir2IrComponents = TODO() // kotlinTypeFacade.session.sessionProvider.getSession()

    return when (this) {
        is SimpleDataColumn ->
            DataColumn.createValueColumn(
                name = name(),
                values = emptyList<Nothing?>(),
                infer = Infer.None,
                type =
                    type.type
                        .toIrType(c)
                        .toKotlinType()
                        .toKType(),
            )

        is SimpleColumnGroup ->
            DataColumn.createColumnGroup(
                name = name(),
                df = columns().map { it.toPluginColumn() }.toDataFrame(),
            )

        is SimpleFrameColumn ->
            DataColumn.createFrameColumn(
                name = name(),
                groups = listOf(columns().map { it.toPluginColumn() }.toDataFrame()),
            )
    }.asEmptyColumn()
}

fun KotlinType.toKType(): KType {
    val kClass = Class.forName(this.toString()).kotlin
    return kClass.starProjectedType
}

// fun ConeKotlinType.toIrType(): IrType {
//    val classifier = this.lookupTag.toClassifier()
//    return when (classifier) {
//        is ClassId -> ??? // transform to IrType
//        is TypeAliasId -> ??? // transform to IrType
//        else -> KotlinTypeFactory.simpleType(
//            annotations = Annotations.EMPTY,
//            constructor = ???, // provide type constructor
//        arguments = this.typeArguments.map {
//            TypeProjectionImpl(Variance.INVARIANT, it.toIrType()) // create type projection
//        },
//        nullable = this.isMarkedNullable
//            )
//    }
// }
