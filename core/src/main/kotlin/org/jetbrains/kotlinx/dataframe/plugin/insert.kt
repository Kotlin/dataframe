package org.jetbrains.kotlinx.dataframe.plugin

import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.api.Col
import org.jetbrains.kotlinx.dataframe.impl.api.ColumnToInsert1
import org.jetbrains.kotlinx.dataframe.impl.api.DataFrameLikeContainer
import org.jetbrains.kotlinx.dataframe.impl.api.MyColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.api.insertImplGenericContainer

internal class DataFrameStructure(private val columns: List<SimpleCol>) : DataFrameLikeContainer<SimpleCol> {
    override fun columns(): List<SimpleCol> {
        return columns
    }
}

internal open class SimpleCol(private val name: String) : Col {
    override fun name(): String {
        return name
    }

    open fun rename(s: String): SimpleCol {
        return SimpleCol(s)
    }
}

internal class SimpleColumnGroup(
    name: String,
    private val columns: List<SimpleCol>
) : MyColumnGroup<SimpleCol>, SimpleCol(name) {

    override fun columns(): List<SimpleCol> {
        return columns
    }

    override fun rename(s: String): SimpleColumnGroup {
        return SimpleColumnGroup(s, columns)
    }
}

@PublishedApi
internal fun DataFrameStructure.insertImpl(path: ColumnPath, column: SimpleCol): DataFrameStructure {
    val columns = listOf(ColumnToInsert1(path, column))

    return insertImplGenericContainer<DataFrameStructure, SimpleCol, MyColumnGroup<SimpleCol>>(
        this,
        columns,
        columns.firstOrNull()?.referenceNode?.getRoot(),
        0,
        factory = { DataFrameStructure(it) },
        empty = DataFrameStructure(emptyList()),
        rename = { rename(it) },
        createColumnGroup = { name, columns ->
            SimpleColumnGroup(name, columns)
        }
    )
}
