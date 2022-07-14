package org.jetbrains.kotlinx.dataframe.annotations

import org.jetbrains.kotlinx.dataframe.plugin.PluginDataFrameSchema
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public interface Interpreter<T> {
    public val expectedArguments: List<ExpectedArgument>

    public data class ExpectedArgument(
        public val name: String,
        public val klass: KType,
        public val lens: Lens,
        val defaultValue: DefaultValue<*>
    )

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

public sealed interface DefaultValue<out T>

public class Present<T>(public val value: T) : DefaultValue<T>
public object Absent : DefaultValue<Nothing>

public open class Arguments(private val arguments: Map<String, Interpreter.Success<Any?>>) {
    public operator fun get(s: String): Any? = (arguments[s] ?: error("")).value
    public operator fun contains(key: String): Boolean {
        return arguments.contains(key)
    }
}

public abstract class AbstractInterpreter<T> : Interpreter<T> {
    @PublishedApi
    internal val _expectedArguments: MutableList<Interpreter.ExpectedArgument> = mutableListOf()

    override val expectedArguments: List<Interpreter.ExpectedArgument> = _expectedArguments

    public inline fun <Value, reified CompileTimeValue> argConvert(
        defaultValue: DefaultValue<Value> = Absent,
        name: ArgumentName? = null,
        lens: Interpreter.Lens = Interpreter.Value,
        crossinline converter: (CompileTimeValue) -> Value
    ): PropertyDelegateProvider<Any?, ReadOnlyProperty<Arguments, Value>> = PropertyDelegateProvider { thisRef: Any?, property ->
        val name = name?.value ?: property.name
        _expectedArguments.add(Interpreter.ExpectedArgument(name, typeOf<CompileTimeValue>(), lens, defaultValue))
        ReadOnlyProperty { args, _ ->
            if (name !in args && defaultValue is Present) {
                defaultValue.value
            } else {
                converter(args[name] as CompileTimeValue)
            }
        }
    }

    public fun <Value> arg(
        name: ArgumentName? = null,
        expectedType: KType? = null,
        defaultValue: DefaultValue<Value> = Absent,
        lens: Interpreter.Lens = Interpreter.Value
    ): PropertyDelegateProvider<Any?, ReadOnlyProperty<Arguments, Value>> = PropertyDelegateProvider { thisRef: Any?, property ->
        val name = name?.value ?: property.name
        _expectedArguments.add(Interpreter.ExpectedArgument(name, expectedType ?: property.returnType, lens, defaultValue))
        ReadOnlyProperty { args, _ ->
            if (name !in args && defaultValue is Present) {
                defaultValue.value
            } else {
                args[name] as Value
            }
        }
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

public abstract class AbstractSchemaModificationInterpreter :
    AbstractInterpreter<PluginDataFrameSchema>(),
    SchemaModificationInterpreter
