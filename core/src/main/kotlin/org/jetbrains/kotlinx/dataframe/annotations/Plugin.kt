package org.jetbrains.kotlinx.dataframe.annotations

import org.jetbrains.kotlinx.dataframe.plugin.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.SimpleCol
import org.jetbrains.kotlinx.dataframe.plugin.schema
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KType

public annotation class Dsl

@Target(AnnotationTarget.VALUE_PARAMETER)
public annotation class Value

@Target(AnnotationTarget.VALUE_PARAMETER)
public annotation class ReturnType

public sealed interface TypeApproximation

public class TypeApproximationImpl(public val fqName: String, public val nullable: Boolean) : TypeApproximation

public fun TypeApproximation(fqName: String, nullable: Boolean): TypeApproximation = TypeApproximationImpl(fqName, nullable)

public object ColumnGroupTypeApproximation : TypeApproximation

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

public annotation class Interpretable(val interpreter: KClass<out Interpreter<*>>)

public class Add : AbstractSchemaModificationInterpreter() {
    public val Arguments.df: PluginDataFrameSchema by arg(THIS)
    public val Arguments.name: String by arg()
    public val Arguments.type: TypeApproximation by arg(name("expression"))

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return PluginDataFrameSchema(listOf(SimpleCol(name, type)))
    }
}

public class Add1 : AbstractSchemaModificationInterpreter() {

    public val Arguments.name: String by arg()
    public val Arguments.expression: TypeApproximation by arg()
    public val Arguments.parent: String by arg()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return PluginDataFrameSchema(listOf(SimpleCol(name, expression)))
    }
}

public class From : AbstractInterpreter<Unit>() {
    public val Arguments.receiver: AddDslApproximation by arg()
    public val Arguments.name: String by arg(THIS)
    public val Arguments.type: TypeApproximation by arg(name("expression"))

    override fun Arguments.interpret() {
        receiver.columns += SimpleCol(name, type)
    }
}

public class AddDslApproximation(public val columns: MutableList<SimpleCol>)

public fun AddDslApproximation(columns: List<Pair<String, PluginColumnSchema>>): AddDslApproximation {
    return AddDslApproximation(columns.mapTo(mutableListOf()) { SimpleCol(it.first, it.second.type) })
}

public class AddWithDsl : AbstractSchemaModificationInterpreter() {
    public val Arguments.df: PluginDataFrameSchema by schema(THIS)
    public val Arguments.body: (Any) -> Unit by arg()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        val addDsl = AddDslApproximation(listOf())
        body(addDsl)
        return PluginDataFrameSchema(addDsl.columns)
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

public data class Property(val name: String, val type: String)

public annotation class SchemaProcessor(val processor: KClass<out SchemaModificationInterpreter>)

public class PluginColumnSchema(public val type: TypeApproximation)

// public sealed interface PluginColumnSchema {
//    public interface Value : PluginColumnSchema
//    public interface Group : PluginColumnSchema
//    public interface Frame : PluginColumnSchema
// }

public interface Compiler
