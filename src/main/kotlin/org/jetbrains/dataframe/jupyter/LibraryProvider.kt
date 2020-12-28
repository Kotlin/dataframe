package org.jetbrains.dataframe.jupyter

import org.jetbrains.dataframe.*
import org.jetbrains.kotlinx.jupyter.api.*
import org.jetbrains.kotlinx.jupyter.api.TypeHandlerExecution
import org.jetbrains.kotlinx.jupyter.api.libraries.*
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

internal class DelegatedTypeHandlerExecution<T>(val callback: (T)->Any?) : TypeHandlerExecution {
    override fun execute(host: KotlinKernelHost, value: Any?, resultFieldName: String?): KotlinKernelHost.Result {
        val v = value as? T
        if(v != null)
            return KotlinKernelHost.Result(callback(v), resultFieldName)
        return KotlinKernelHost.Result(null, resultFieldName)
    }

    override fun replaceVariables(mapping: Map<String, String>): TypeHandlerExecution = this
}

internal class DelegatedExecution(val callback: ()->Unit) : Execution {
    override fun execute(host: KotlinKernelHost): Any? {
        callback()
        return Unit
    }
}

internal class DelegatedTypeHandler<T: Any>(val clazz: KClass<T>, render: (T) -> Any) : RendererTypeHandler {

    override val execution = DelegatedTypeHandlerExecution(render)

    override fun acceptsType(type: KClass<*>): Boolean {
        return type.isSubclassOf(clazz)
    }

    override fun replaceVariables(mapping: Map<String, String>) = this

    companion object
    {
        inline fun <reified T: Any> create(noinline render: (T)->Any) = DelegatedTypeHandler<T>(T::class, render)
    }
}

internal abstract class JupyterIntegration : LibraryDefinitionProducer {

    private val renderers = mutableListOf<RendererTypeHandler>()

    private val initCallbacks = mutableListOf<Execution>()

    private val converters = mutableListOf<GenerativeTypeHandler>()

    protected inline fun <reified T: Any> renderer(noinline renderer: (T) -> Any){
        renderers.add(DelegatedTypeHandler.create(renderer))
    }

    protected fun onLoaded(callback: () -> Unit){
        initCallbacks.add(DelegatedExecution(callback))
    }

    override fun getDefinitions(notebook: Notebook<*>?): List<LibraryDefinition> {
        return listOf(LibraryDefinitionImpl(
                init = initCallbacks,
                renderers = renderers,
                converters = converters
        ))
    }
}

internal class LibraryProvider: JupyterIntegration() {

    init {
        renderer<DataCol> { dataFrameOf(listOf(it)) }
        renderer<DataFrame<*>> { HTML(it.toHTML()) }
        renderer<GroupedDataFrame<*,*>> { it.plain() }
        renderer<DataRow<*>> { it.toDataFrame() }
    }
}