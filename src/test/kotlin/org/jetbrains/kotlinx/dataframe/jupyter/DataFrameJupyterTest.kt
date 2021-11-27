package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.jupyter.ReplForJupyterImpl
import org.jetbrains.kotlinx.jupyter.api.libraries.LibraryDefinition
import org.jetbrains.kotlinx.jupyter.api.libraries.LibraryReference
import org.jetbrains.kotlinx.jupyter.api.libraries.Variable
import org.jetbrains.kotlinx.jupyter.api.libraries.libraryDefinition
import org.jetbrains.kotlinx.jupyter.defaultRepositories
import org.jetbrains.kotlinx.jupyter.dependencies.ResolverConfig
import org.jetbrains.kotlinx.jupyter.libraries.ChainedLibraryResolver
import org.jetbrains.kotlinx.jupyter.libraries.EmptyResolutionInfoProvider
import org.jetbrains.kotlinx.jupyter.libraries.LibraryResolver
import org.jetbrains.kotlinx.jupyter.testkit.ClasspathLibraryResolver
import org.jetbrains.kotlinx.jupyter.testkit.JupyterReplTestCase
import org.jetbrains.kotlinx.jupyter.testkit.ReplProvider

typealias LibraryFilter = (String?) -> Boolean

private val specialLibraries = listOf("dataframe")
private val specialLibrariesFilter: LibraryFilter = { it in specialLibraries }
private val emptyLibraryDefinition = libraryDefinition {}

class ToEmptyResolver(parent: LibraryResolver?, private val resolveToEmpty: LibraryFilter) : ChainedLibraryResolver(parent) {
    override fun tryResolve(reference: LibraryReference, arguments: List<Variable>): LibraryDefinition? {
        return if (resolveToEmpty(reference.name)) return emptyLibraryDefinition else null
    }
}

private val testLibraryResolver = run {
    var res: LibraryResolver = ClasspathLibraryResolver(null) { !specialLibrariesFilter(it) }
    res = ToEmptyResolver(res, specialLibrariesFilter)
    res
}

private val testReplProvider = ReplProvider { classpath ->
    ReplForJupyterImpl(
        EmptyResolutionInfoProvider,
        classpath,
        isEmbedded = true,
        resolverConfig = ResolverConfig(defaultRepositories, testLibraryResolver)
    ).apply {
        eval { librariesScanner.addLibrariesFromClassLoader(ToEmptyResolver::class.java.classLoader, this) }
    }
}

abstract class DataFrameJupyterTest : JupyterReplTestCase(testReplProvider)

fun interface CodeReplacer {
    fun replace(code: String): String

    companion object {
        val DEFAULT = CodeReplacer { it }

        fun byMap(replacements: Map<String, String>) = CodeReplacer { code ->
            replacements.entries.fold(code) { acc, (key, replacement) ->
                acc.replace(key, replacement)
            }
        }

        fun byMap(vararg replacements: Pair<String, String>): CodeReplacer = byMap(mapOf(*replacements))
    }
}
