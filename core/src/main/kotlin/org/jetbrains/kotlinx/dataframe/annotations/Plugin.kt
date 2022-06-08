package org.jetbrains.kotlinx.dataframe.annotations

import org.jetbrains.kotlinx.dataframe.plugin.schema
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KType

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

public interface Interpreter<T> {
    public val expectedArguments: List<ExpectedArgument>

    public data class ExpectedArgument(public val name: String, public val klass: KType)

    public fun interpret(arguments: Map<String, Any>): T
}

public open class Arguments(private val arguments: Map<String, Any>) {
    public operator fun get(s: String): Any? = arguments[s]
}

public abstract class AbstractInterpreter<T> : Interpreter<T> {
    @PublishedApi
    internal val _expectedArguments: MutableList<Interpreter.ExpectedArgument> = mutableListOf()

    override val expectedArguments: List<Interpreter.ExpectedArgument> = _expectedArguments
    public fun <Value> arg(name: ArgumentName? = null): PropertyDelegateProvider<Any?, ReadOnlyProperty<Arguments, Value>> = PropertyDelegateProvider { thisRef: Any?, property ->
        val name = name?.value ?: property.name
        _expectedArguments.add(Interpreter.ExpectedArgument(name, property.returnType))
        ReadOnlyProperty { args, _ -> args[name] as Value }
    }

    public class ArgumentName private constructor(public val value: String) {
        public companion object {
            public val THIS: ArgumentName = ArgumentName("this")

            public fun of(name: String): ArgumentName = ArgumentName(name)
        }
    }

    public val THIS: ArgumentName = ArgumentName.THIS

    public fun name(name: String): ArgumentName = ArgumentName.of(name)

    final override fun interpret(arguments: Map<String, Any>): T {
        return Arguments(arguments).interpret()
    }

    public abstract fun Arguments.interpret(): T
}

public interface SchemaModificationInterpreter : Interpreter<PluginDataFrameSchema> {

    override fun interpret(arguments: Map<String, Any>): PluginDataFrameSchema
}

public abstract class AbstractSchemaModificationInterpreter : AbstractInterpreter<PluginDataFrameSchema>(), SchemaModificationInterpreter

public class ConvertApproximation(public val schema: PluginDataFrameSchema, public val columns: List<List<String>>)

public class ConvertInterpreter : AbstractInterpreter<ConvertApproximation>() {
    public val Arguments.schema: PluginDataFrameSchema by arg(ArgumentName.THIS)
    public val Arguments.columns: List<String> by arg()

    override fun Arguments.interpret(): ConvertApproximation {
        return ConvertApproximation(schema, columns.map { listOf(it) })
    }
}

public annotation class Interpretable<I : Interpreter<*>>(val interpreter: KClass<I>)

public class Add : AbstractSchemaModificationInterpreter() {
    public val Arguments.df: PluginDataFrameSchema by arg(THIS)
    public val Arguments.name: String by arg()
    public val Arguments.type: TypeApproximation by arg(name("expression"))

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return PluginDataFrameSchema(mapOf(name to PluginColumnSchema(type)))
    }
}

public class Add1 : AbstractSchemaModificationInterpreter() {

    public val Arguments.name: String by arg()
    public val Arguments.expression: TypeApproximation by arg()
    public val Arguments.parent: String by arg()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return PluginDataFrameSchema(mapOf(name to PluginColumnSchema(expression)))
    }
//    override fun interpret(arguments: Map<String, Any>): PluginDataFrameSchema {
//        val name = arguments["name"] as String
//        val type = arguments["expression"] as TypeApproximation
//        val parent = arguments["<parent>"] as String
//
//        return PluginDataFrameSchema(mapOf(name to PluginColumnSchema(type)))
// //        return AnalysisResult.Update(
// //            parent,
// //            updatedProperties = emptyList(),
// //            newProperties = listOf(Property(name, type))
// //        )
//    }
}

public class From : AbstractInterpreter<Unit>() {
    public val Arguments.receiver: AddDslApproximation by arg()
    public val Arguments.name: String by arg(THIS)
    public val Arguments.type: TypeApproximation by arg(name("expression"))

    override fun Arguments.interpret() {
        receiver.columns += (name to PluginColumnSchema(type))
    }
}

public class AddDslApproximation(public val columns: MutableList<Pair<String, PluginColumnSchema>>)

public class AddWithDsl : AbstractSchemaModificationInterpreter() {
    public val Arguments.df: PluginDataFrameSchema by schema(THIS)
    public val Arguments.body: (Any) -> Unit by arg()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        val addDsl = AddDslApproximation(mutableListOf())
        body(addDsl)
        return PluginDataFrameSchema(addDsl.columns.toMap())
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

// public class DslResult(public val properties: List<Property>) {
//    public constructor(property: Property) : this(listOf(property))
//
//    public operator fun plus(other: DslResult): DslResult {
//        return DslResult(properties + other.properties)
//    }
// }

public data class Property(val name: String, val type: String)

public annotation class SchemaProcessor<A : SchemaModificationInterpreter>(val processor: KClass<A>)

@SchemaProcessor<SchemaModificationInterpreter>(SchemaModificationInterpreter::class)
public fun <A : SchemaModificationInterpreter> test(processor: SchemaProcessor<A>) {
    val processor: SchemaModificationInterpreter = processor.processor.java.getDeclaredConstructor().newInstance()

    processor
}

public class PluginDataFrameSchema(public val columns: Map<String, PluginColumnSchema>)

public class PluginColumnSchema(public val type: TypeApproximation)

// public sealed interface PluginColumnSchema {
//    public interface Value : PluginColumnSchema
//    public interface Group : PluginColumnSchema
//    public interface Frame : PluginColumnSchema
// }

public interface Compiler
