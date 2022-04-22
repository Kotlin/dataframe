package org.jetbrains.kotlinx.dataframe.annotations

import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass


public annotation class Dsl

@Target(AnnotationTarget.VALUE_PARAMETER)
public annotation class Name

@Target(AnnotationTarget.VALUE_PARAMETER)
public annotation class Value

@Target(AnnotationTarget.VALUE_PARAMETER)
public annotation class ReturnType

public class TypeApproximation(public val fqName: String, public val nullable: Boolean)

@Target(AnnotationTarget.CLASS)
public annotation class HasSchema(val schemaArg: Int)

@Target(AnnotationTarget.VALUE_PARAMETER)
public annotation class Schema

public interface SchemaModificationInterpreter {
    public fun process(parent: String, map: Map<String, Any>): AnalysisResult
}

public interface Interpreter {

    public fun interpret(arguments: Map<String, Any>): Any

}

public class ConvertApproximation(public val schema: DataFrameSchema, public val columns: List<List<String>>)

public class DataFrameApproximation(public val schema: DataFrameSchema)

public class ConvertInterpreter : Interpreter {
    override fun interpret(arguments: Map<String, Any>): Any {
        val df = arguments["this"] as DataFrameApproximation
        val columns = arguments["columns"] as List<String>
        return ConvertApproximation(df.schema, columns.map { listOf(it) })
    }
}

public annotation class Interpretable <I : Interpreter>(val interpreter: KClass<I>)

public class Add : SchemaModificationInterpreter {
    override fun process(parent: String, map: Map<String, Any>): AnalysisResult {
        val name = map["name"] as String
        val type = map["expression"] as String
        return AnalysisResult.Update(
            parent,
            updatedProperties = emptyList(),
            newProperties = listOf(Property(name, type))
        )
    }
}

public class From : SchemaModificationInterpreter {
    override fun process(parent: String, map: Map<String, Any>): AnalysisResult {
        val receiver = map["this"] as String
        val type = map["expression"] as String
        return AnalysisResult.New(listOf(Property(receiver, type)))
    }
}

public class AddWithDsl : SchemaModificationInterpreter {
    override fun process(parent: String, map: Map<String, Any>): AnalysisResult {
        val body = map["body"] as AnalysisResult.New
        return AnalysisResult.Update(parent, newProperties = body.properties, updatedProperties = emptyList())
    }
}

public sealed interface AnalysisResult {
    public class New(public val properties: List<Property>) : AnalysisResult {
        public operator fun plus(other: New): New {
           return New(properties + other.properties)
        }
    }

    public class Update(
        public val parent: String,
        public val updatedProperties: List<Property>,
        public val newProperties: List<Property>
    ) : AnalysisResult

}

//public class DslResult(public val properties: List<Property>) {
//    public constructor(property: Property) : this(listOf(property))
//
//    public operator fun plus(other: DslResult): DslResult {
//        return DslResult(properties + other.properties)
//    }
//}

public data class Property(val name: String, val type: String)

public annotation class SchemaProcessor <A : SchemaModificationInterpreter>(val processor: KClass<A>)

@SchemaProcessor<SchemaModificationInterpreter>(SchemaModificationInterpreter::class)
public fun <A : SchemaModificationInterpreter> test(processor: SchemaProcessor<A>) {
    val processor: SchemaModificationInterpreter = processor.processor.java.getDeclaredConstructor().newInstance()

    processor
}
