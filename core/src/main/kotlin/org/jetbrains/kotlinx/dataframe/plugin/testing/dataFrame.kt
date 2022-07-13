package org.jetbrains.kotlinx.dataframe.plugin.testing

import kotlinx.serialization.encodeToString
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.TypeApproximationImpl
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.codeGen.MarkersExtractor
import org.jetbrains.kotlinx.dataframe.plugin.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.SimpleCol
import org.jetbrains.kotlinx.dataframe.plugin.dataFrame
import org.jetbrains.kotlinx.dataframe.plugin.pluginJsonFormat
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import kotlin.reflect.KClass

@Interpretable(DataFrameIdentity::class)
public fun dataFrame(v: DataFrame<*>): DataFrame<*> {
    return v
}

public class DataFrameIdentity : AbstractInterpreter<PluginDataFrameSchema>() {
    public val Arguments.v: PluginDataFrameSchema by dataFrame()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return v
    }
}

internal interface Schema1 {
    val i: Int
}

internal fun injectionScope() {
    val df = dataFrameOf("i")(1, 2, 3).cast<Schema1>()
    test(id = "dataFrame_1", call = dataFrame(df))
}

internal fun main() {
    val df = dataFrameOf("i")(1, 2, 3)
    val schema = df.schema()
    val generator = CodeGenerator.create()
    val declaration =
        generator.generate(schema, name = "Schema1", fields = true, extensionProperties = false, isOpen = true).code.declarations
    val get = MarkersExtractor.get<Schema1>()
    val columns = get.schema.columns.map { (name, columnSchema) ->
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
            is ColumnSchema.Group -> TODO()
            is ColumnSchema.Frame -> TODO()
            else -> TODO()
        }
    }
    val pluginSchema = PluginDataFrameSchema(columns)
    pluginJsonFormat.encodeToString(pluginSchema)
    println(declaration)
}
