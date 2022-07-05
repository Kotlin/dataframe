package org.jetbrains.kotlinx.dataframe.annotations

import org.jetbrains.kotlinx.dataframe.plugin.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.SimpleCol
import org.jetbrains.kotlinx.dataframe.plugin.dataFrame
import org.jetbrains.kotlinx.dataframe.plugin.string
import org.jetbrains.kotlinx.dataframe.plugin.type
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

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

    public data class ExpectedArgument(public val name: String, public val klass: KType, public val lens: Lens)

    public sealed interface Lens

    public object Value : Lens

    public object ReturnType : Lens

    public object Dsl : Lens

    public object Schema : Lens

    public fun interpret(arguments: Map<String, Success<Any?>>): InterpretationResult<T>

    public sealed interface InterpretationResult<out T>

    public class Success<out T>(public val value: T) : InterpretationResult<T> {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Success<*>

            if (value != other.value) return false

            return true
        }

        override fun hashCode(): Int {
            return value?.hashCode() ?: 0
        }
    }

    public class Error(public val message: String?) : InterpretationResult<Nothing>
}

public open class Arguments(private val arguments: Map<String, Interpreter.Success<Any?>>) {
    public operator fun get(s: String): Any? = (arguments[s] ?: error("")).value
}

public abstract class AbstractInterpreter<T> : Interpreter<T> {
    @PublishedApi
    internal val _expectedArguments: MutableList<Interpreter.ExpectedArgument> = mutableListOf()

    override val expectedArguments: List<Interpreter.ExpectedArgument> = _expectedArguments

    public inline fun <Value, reified CompileTimeValue> argConvert(
        name: ArgumentName? = null,
        lens: Interpreter.Lens = Interpreter.Value,
        crossinline converter: (CompileTimeValue) -> Value
    ): PropertyDelegateProvider<Any?, ReadOnlyProperty<Arguments, Value>> = PropertyDelegateProvider { thisRef: Any?, property ->
        val name = name?.value ?: property.name
        _expectedArguments.add(Interpreter.ExpectedArgument(name, typeOf<CompileTimeValue>(), lens))
        ReadOnlyProperty { args, _ -> converter(args[name] as CompileTimeValue) }
    }

    public fun <Value> arg(
        name: ArgumentName? = null,
        expectedType: KType? = null,
        lens: Interpreter.Lens = Interpreter.Value
    ): PropertyDelegateProvider<Any?, ReadOnlyProperty<Arguments, Value>> = PropertyDelegateProvider { thisRef: Any?, property ->
        val name = name?.value ?: property.name
        _expectedArguments.add(Interpreter.ExpectedArgument(name, expectedType ?: property.returnType, lens))
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

    final override fun interpret(arguments: Map<String, Interpreter.Success<Any?>>): Interpreter.InterpretationResult<T> {
        return try {
            Arguments(arguments).interpret().let { Interpreter.Success(it) }
        } catch (e: Exception) {
            Interpreter.Error(e.message)
        }
    }

    public abstract fun Arguments.interpret(): T
}

public interface SchemaModificationInterpreter : Interpreter<PluginDataFrameSchema> {

    override fun interpret(arguments: Map<String, Interpreter.Success<Any?>>): Interpreter.InterpretationResult<PluginDataFrameSchema>
}

public abstract class AbstractSchemaModificationInterpreter : AbstractInterpreter<PluginDataFrameSchema>(), SchemaModificationInterpreter

public class ConvertApproximation(public val schema: PluginDataFrameSchema, public val columns: List<List<String>>)

public class ConvertInterpreter : AbstractInterpreter<ConvertApproximation>() {
    public val Arguments.schema: PluginDataFrameSchema by dataFrame(ArgumentName.THIS)
    public val Arguments.columns: List<String> by arg(lens = Interpreter.Value)

    override fun Arguments.interpret(): ConvertApproximation {
        return ConvertApproximation(schema, columns.map { listOf(it) })
    }
}

public annotation class Interpretable(val interpreter: KClass<out Interpreter<*>>)

public class Add : AbstractSchemaModificationInterpreter() {
    public val Arguments.df: PluginDataFrameSchema by dataFrame(THIS)
    public val Arguments.name: String by string()
    public val Arguments.type: TypeApproximation by type(name("expression"))

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return PluginDataFrameSchema(listOf(SimpleCol(name, type)))
    }
}

public class Add1 : AbstractSchemaModificationInterpreter() {

    public val Arguments.name: String by string()
    public val Arguments.expression: TypeApproximation by type()
    public val Arguments.parent: String by string()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return PluginDataFrameSchema(listOf(SimpleCol(name, expression)))
    }
}

public class From : AbstractInterpreter<Unit>() {
    public val Arguments.receiver: AddDslApproximation by arg(lens = Interpreter.Value)
    public val Arguments.name: String by string(THIS)
    public val Arguments.type: TypeApproximation by type(name("expression"))

    override fun Arguments.interpret() {
        receiver.columns += SimpleCol(name, type)
    }
}

public class AddDslApproximation(public val columns: MutableList<SimpleCol>)

public fun AddDslApproximation(columns: List<Pair<String, PluginColumnSchema>>): AddDslApproximation {
    return AddDslApproximation(columns.mapTo(mutableListOf()) { SimpleCol(it.first, it.second.type) })
}

public class AddWithDsl : AbstractSchemaModificationInterpreter() {
    public val Arguments.df: PluginDataFrameSchema by dataFrame(THIS)
    public val Arguments.body: (Any) -> Unit by arg(lens = Interpreter.Dsl)

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
