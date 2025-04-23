package org.jetbrains.kotlinx.dataframe.plugin.impl.api

import org.jetbrains.kotlin.fir.types.isSubtypeOf
import org.jetbrains.kotlinx.dataframe.plugin.impl.AbstractSchemaModificationInterpreter
import org.jetbrains.kotlinx.dataframe.plugin.impl.Arguments
import org.jetbrains.kotlinx.dataframe.plugin.impl.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleDataColumn
import org.jetbrains.kotlinx.dataframe.plugin.impl.dataFrame
import org.jetbrains.kotlinx.dataframe.plugin.impl.simpleColumnOf

/** Adds to the schema only numerical columns. */
abstract class Aggregator0 : AbstractSchemaModificationInterpreter() {
    private val Arguments.receiver: PluginDataFrameSchema by dataFrame()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        val resolvedColumns = receiver.columns()
            .filterIsInstance<SimpleDataColumn>()
            .filter { it.type.type.isSubtypeOf(session.builtinTypes.numberType.type, session) }

        /*val skipNaN = true
        val sum = Aggregators.sum(skipNaN)*/

       /* val newColumns = resolvedColumns
            .map { col ->
                simpleColumnOf(col.name, session.builtinTypes.doubleType.type)
            }
            .toList()*/

        return PluginDataFrameSchema(receiver.columns() + resolvedColumns)
    }
}

/** Implementation for `sum`. */
class Sum0 : Aggregator0()
