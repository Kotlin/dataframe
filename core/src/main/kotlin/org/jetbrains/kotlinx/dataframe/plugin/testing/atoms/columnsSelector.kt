package org.jetbrains.kotlinx.dataframe.plugin.testing.atoms

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Interpreter
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.plugin.*
import org.jetbrains.kotlinx.dataframe.plugin.testing.test

@Interpretable(ColumnsSelectorIdentity::class)
public fun <T, C> columnsSelector(v: ColumnsSelector<T, C>): ColumnsSelector<T, C> {
    return v
}

public class ColumnsSelectorIdentity : AbstractInterpreter<List<ColumnWithPathApproximation>>() {
    internal val Arguments.v: List<ColumnWithPathApproximation> by arg(lens = Interpreter.Value)
    override fun Arguments.interpret(): List<ColumnWithPathApproximation> {
        return v
    }
}

internal interface Schema0 {
    val intField: Int
    val group: Group0
}

internal interface Group0 {
    val stringField: String
}

internal val ColumnsContainer<Schema0>.intField: DataColumn<Int> get() = TODO()
internal val ColumnsContainer<Schema0>.group: ColumnGroup<Group0> get() = TODO()
internal val ColumnsContainer<Group0>.stringField: DataColumn<String> get() = TODO()

internal fun columnsSelectorTest() {
    test(id = "columnSelector_1", call = columnsSelector<Schema0, _> { intField })
    test(id = "columnSelector_2", call = columnsSelector<Schema0, _> { group.stringField })
}
