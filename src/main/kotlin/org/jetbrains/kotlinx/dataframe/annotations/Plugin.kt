package org.jetbrains.kotlinx.dataframe.annotations

import kotlin.reflect.KClass

public annotation class SchemaMutation(val startingSchema: StartingSchema)

public enum class StartingSchema {
    EMPTY, FULL
}

public annotation class Dsl

public annotation class NewColumn

@Target(AnnotationTarget.VALUE_PARAMETER)
public annotation class Name

@Target(AnnotationTarget.VALUE_PARAMETER)
public annotation class ReturnType

public class A

public interface SchemaDD {
    public fun process(parent: String, map: Map<String, Any>): AnalysisResult
}

public class Add : SchemaDD {
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

public class From : SchemaDD {
    override fun process(parent: String, map: Map<String, Any>): AnalysisResult {
        val receiver = map["this"] as String
        val type = map["expression"] as String
        return AnalysisResult.New(listOf(Property(receiver, type)))
    }
}

public class AddWithDsl : SchemaDD {
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

public annotation class SchemaProcessor <A : SchemaDD>(val processor: KClass<A>)

@SchemaProcessor<SchemaDD>(SchemaDD::class)
public fun <A : SchemaDD> test(processor: SchemaProcessor<A>) {
    val processor: SchemaDD = processor.processor.java.getDeclaredConstructor().newInstance()

    processor
}
