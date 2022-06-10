package org.jetbrains.kotlinx.dataframe.plugin

import org.jetbrains.kotlinx.dataframe.annotations.ColumnGroupTypeApproximation
import org.jetbrains.kotlinx.dataframe.annotations.TypeApproximation
import org.jetbrains.kotlinx.dataframe.annotations.TypeApproximationImpl
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.api.Col
import org.jetbrains.kotlinx.dataframe.impl.api.ColumnToInsert1
import org.jetbrains.kotlinx.dataframe.impl.api.DataFrameLikeContainer
import org.jetbrains.kotlinx.dataframe.impl.api.MyColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.api.insertImplGenericContainer

public class PluginDataFrameSchema(private val columns: List<SimpleCol>) : DataFrameLikeContainer<SimpleCol> {
    override fun columns(): List<SimpleCol> {
        return columns
    }
}

public open class SimpleCol internal constructor(private val name: String, public open val type: TypeApproximation) : Col {
    public constructor(name: String, type: TypeApproximationImpl) : this(name, type as TypeApproximation)

    override fun name(): String {
        return name
    }

    public open fun rename(s: String): SimpleCol {
        return SimpleCol(s, type)
    }

    public open fun changeType(type: TypeApproximation): SimpleCol {
        return SimpleCol(name, type)
    }
}

internal class SimpleColumnGroup(
    name: String,
    private val columns: List<SimpleCol>
) : MyColumnGroup<SimpleCol>, SimpleCol(name, ColumnGroupTypeApproximation) {

    override fun columns(): List<SimpleCol> {
        return columns
    }

    override fun rename(s: String): SimpleColumnGroup {
        return SimpleColumnGroup(s, columns)
    }

    override fun changeType(type: TypeApproximation): SimpleCol {
        return TODO()
    }
}

@PublishedApi
internal fun PluginDataFrameSchema.insertImpl(path: ColumnPath, column: SimpleCol): PluginDataFrameSchema {
    val columns = listOf(ColumnToInsert1(path, column))

    return insertImplGenericContainer<PluginDataFrameSchema, SimpleCol, MyColumnGroup<SimpleCol>>(
        this,
        columns,
        columns.firstOrNull()?.referenceNode?.getRoot(),
        0,
        factory = { PluginDataFrameSchema(it) },
        empty = PluginDataFrameSchema(emptyList()),
        rename = { rename(it) },
        createColumnGroup = { name, columns ->
            SimpleColumnGroup(name, columns)
        }
    )
}
