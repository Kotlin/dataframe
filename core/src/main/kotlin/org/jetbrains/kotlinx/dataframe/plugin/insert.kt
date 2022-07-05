package org.jetbrains.kotlinx.dataframe.plugin

import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.ColumnGroupTypeApproximation
import org.jetbrains.kotlinx.dataframe.annotations.TypeApproximation
import org.jetbrains.kotlinx.dataframe.annotations.TypeApproximationImpl
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.toPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.api.Col
import org.jetbrains.kotlinx.dataframe.impl.api.ColumnToInsert1
import org.jetbrains.kotlinx.dataframe.impl.api.DataFrameLikeContainer
import org.jetbrains.kotlinx.dataframe.impl.api.MyColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.api.insertImplGenericContainer

internal class Insert0 : AbstractInterpreter<InsertClauseApproximation>() {
    val Arguments.column: SimpleCol by dataColumn()
    val Arguments.receiver: PluginDataFrameSchema by dataFrame(THIS)

    override fun Arguments.interpret(): InsertClauseApproximation {
        return InsertClauseApproximation(receiver, column)
    }
}

internal class Insert1 : AbstractInterpreter<InsertClauseApproximation>() {
    val Arguments.column: String by string()
    val Arguments.infer: Infer by enum()
    val Arguments.expression: TypeApproximation by type()
    val Arguments.receiver: PluginDataFrameSchema by dataFrame(THIS)

    override fun Arguments.interpret(): InsertClauseApproximation {
        return InsertClauseApproximation(receiver, SimpleCol(column, expression))
    }
}

internal class Insert2 : AbstractInterpreter<InsertClauseApproximation>() {
    val Arguments.column: ColumnAccessorApproximation by columnAccessor()
    val Arguments.infer: Infer by enum()
    val Arguments.expression: TypeApproximation by type()
    val Arguments.receiver: PluginDataFrameSchema by dataFrame(THIS)

    override fun Arguments.interpret(): InsertClauseApproximation {
        return InsertClauseApproximation(receiver, SimpleCol(column.name, expression))
    }
}

internal class Insert3 : AbstractInterpreter<InsertClauseApproximation>() {
    val Arguments.column: KPropertyApproximation by kproperty()
    val Arguments.infer: Infer by enum()
    val Arguments.expression: TypeApproximation by type()
    val Arguments.receiver: PluginDataFrameSchema by dataFrame(THIS)

    override fun Arguments.interpret(): InsertClauseApproximation {
        return InsertClauseApproximation(receiver, SimpleCol(column.name, expression))
    }
}

internal class Under0 : AbstractInterpreter<PluginDataFrameSchema>() {
    val Arguments.column: ColumnWithPathApproximation by columnWithPath()
    val Arguments.receiver: InsertClauseApproximation by insertClause(THIS)

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return receiver.df.insertImpl(column.path.path.toPath(), receiver.column)
    }
}

internal class Under1 : AbstractInterpreter<PluginDataFrameSchema>() {
    val Arguments.columnPath: ColumnPathApproximation by columnPath()
    val Arguments.receiver: InsertClauseApproximation by insertClause(THIS)

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return receiver.df.insertImpl(columnPath.path.toPath(), receiver.column)
    }
}

internal class Under2 : AbstractInterpreter<PluginDataFrameSchema>() {
    val Arguments.column: ColumnAccessorApproximation by columnAccessor()
    val Arguments.receiver: InsertClauseApproximation by insertClause(THIS)

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return receiver.df.insertImpl(pathOf(column.name), receiver.column)
    }
}

internal class Under3 : AbstractInterpreter<PluginDataFrameSchema>() {
    val Arguments.column: KPropertyApproximation by kproperty()
    val Arguments.receiver: InsertClauseApproximation by insertClause(THIS)

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return receiver.df.insertImpl(pathOf(column.name), receiver.column)
    }
}

internal class Under4 : AbstractInterpreter<PluginDataFrameSchema>() {
    val Arguments.column: String by string()
    val Arguments.receiver: InsertClauseApproximation by insertClause(THIS)

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return receiver.df.insertImpl(pathOf(column), receiver.column)
    }
}

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
