package org.jetbrains.kotlinx.dataframe.plugin.impl.api

import org.jetbrains.kotlinx.dataframe.plugin.impl.AbstractSchemaModificationInterpreter
import org.jetbrains.kotlinx.dataframe.plugin.impl.Arguments
import org.jetbrains.kotlinx.dataframe.plugin.impl.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.impl.Present
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleColumnGroup
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleFrameColumn
import org.jetbrains.kotlinx.dataframe.plugin.impl.add
import org.jetbrains.kotlinx.dataframe.plugin.impl.groupBy
import org.jetbrains.kotlinx.dataframe.plugin.impl.ignore
import org.jetbrains.kotlinx.dataframe.plugin.impl.makeNullable
import org.jetbrains.kotlinx.dataframe.plugin.impl.simpleColumnOf
import org.jetbrains.kotlinx.dataframe.plugin.impl.type

/*class GroupBySum0 : AbstractSchemaModificationInterpreter() {
    val Arguments.receiver by groupBy()
    val Arguments.resultName: String by arg(defaultValue = Present("sum"))
    val Arguments.predicate by ignore()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return receiver.keys.add(resultName, session.builtinTypes.intType.type, context = this)
    }
}*/


// TODO: minOf - has method paramter name, but not resultName - inconsitency
abstract class GroupByAggregator2(val defaultName: String) : AbstractSchemaModificationInterpreter() {
    val Arguments.receiver by groupBy()
    val Arguments.resultName: String? by arg(defaultValue = Present(null))
    val Arguments.expression by type()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        val aggregated = makeNullable(simpleColumnOf(resultName ?: defaultName, expression.type))
        return PluginDataFrameSchema(receiver.keys.columns() + aggregated)
    }
}

class GroupBySumOf : GroupByAggregator2(defaultName = "sum")

abstract class GroupByAggregator3(val defaultName: String) : AbstractSchemaModificationInterpreter() {
    val Arguments.receiver by groupBy()
    val Arguments.name: String? by arg(defaultValue = Present(null))
    val Arguments.columns: ColumnsResolver by arg()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        val aggregated = makeNullable(SimpleColumnGroup(name ?: defaultName, receiver.groups.columns()))
        // TODO: type of the column from "columns"
        // TODO: could it be 2 or more columns in "columns"?
        return PluginDataFrameSchema(receiver.keys.columns() + aggregated)
    }
}

class GroupBySum0 : GroupByAggregator3(defaultName = "sum")


