package org.jetbrains.kotlinx.dataframe.plugin.testing.schemaRender

import org.jetbrains.kotlinx.dataframe.annotations.TypeApproximationImpl
import org.jetbrains.kotlinx.dataframe.plugin.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.SimpleCol
import org.jetbrains.kotlinx.dataframe.plugin.SimpleColumnGroup
import org.jetbrains.kotlinx.dataframe.plugin.SimpleFrameColumn
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import kotlin.reflect.KClass

public fun DataFrameSchema.toPluginDataFrameSchema(): PluginDataFrameSchema {
    return PluginDataFrameSchema(
        columns = columns.map { (name, columnSchema) ->
            when (columnSchema) {
                is ColumnSchema.Value -> {
                    val classifier = columnSchema.type.classifier ?: error("")
                    val klass = classifier as? KClass<*> ?: error("$columnSchema")
                    val fqName = klass.qualifiedName ?: error("")
                    val type = TypeApproximationImpl(fqName, columnSchema.nullable)
                    SimpleCol(name, type)
                    // class Data(val i: Int) // local class
                    // val df = dataFrameOf("col")(Data(1))
                    // val schema = df.schema()
                    // generateInterface(schema)
                }
                is ColumnSchema.Group -> SimpleColumnGroup(name, columnSchema.schema.toPluginDataFrameSchema().columns())
                is ColumnSchema.Frame -> SimpleFrameColumn(name, columnSchema.schema.toPluginDataFrameSchema().columns(), columnSchema.nullable)
                else -> TODO()
            }
        }
    )
}
