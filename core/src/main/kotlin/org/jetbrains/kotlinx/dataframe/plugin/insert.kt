package org.jetbrains.kotlinx.dataframe.plugin

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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
    val Arguments.name: String by string()
    val Arguments.infer: Infer by enum()
    val Arguments.expression: TypeApproximation by type()
    val Arguments.receiver: PluginDataFrameSchema by dataFrame(THIS)

    override fun Arguments.interpret(): InsertClauseApproximation {
        return InsertClauseApproximation(receiver, SimpleCol(name, expression))
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

@Serializable
public class PluginDataFrameSchema(
    @Contextual private val columns: List<SimpleCol>
) : DataFrameLikeContainer<SimpleCol> {
    override fun columns(): List<SimpleCol> {
        return columns
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PluginDataFrameSchema

        if (columns != other.columns) return false

        return true
    }

    override fun hashCode(): Int {
        return columns.hashCode()
    }
}

@Serializable
public open class SimpleCol internal constructor(
    public val name: String,
    @SerialName("valuesType") public open val type: TypeApproximation
) : Col {
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SimpleCol

        if (name != other.name) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}

@Serializable
public class SimpleColumnGroup(
    private val name1: String,
    private val columns: List<SimpleCol>
) : MyColumnGroup<SimpleCol>, SimpleCol(name1, ColumnGroupTypeApproximation) {

    override fun columns(): List<SimpleCol> {
        return columns
    }

    override fun rename(s: String): SimpleColumnGroup {
        return SimpleColumnGroup(s, columns)
    }

    override fun changeType(type: TypeApproximation): SimpleCol {
        return TODO()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as SimpleColumnGroup

        if (name != other.name) return false
        if (columns != other.columns) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + columns.hashCode()
        return result
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
