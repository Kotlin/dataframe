package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.jupyter.api.EmbeddedKernelRunMode
import org.jetbrains.kotlinx.jupyter.config.DefaultKernelLoggerFactory
import org.jetbrains.kotlinx.jupyter.config.defaultRepositoriesCoordinates
import org.jetbrains.kotlinx.jupyter.libraries.LibraryResolver
import org.jetbrains.kotlinx.jupyter.libraries.createLibraryHttpUtil
import org.jetbrains.kotlinx.jupyter.repl.ReplForJupyter
import org.jetbrains.kotlinx.jupyter.repl.creating.createRepl
import org.jetbrains.kotlinx.jupyter.repl.embedded.NoOpInMemoryReplResultsHolder
import org.jetbrains.kotlinx.jupyter.testkit.ClasspathLibraryResolver
import org.jetbrains.kotlinx.jupyter.testkit.ReplProvider
import org.jetbrains.kotlinx.jupyter.testkit.ToEmptyLibraryResolver

/**  Mirrors [ReplProvider.forLibrariesTesting] but `extraCompilerArguments` to set opt-in's. */
@Suppress("unused")
fun ReplProvider.Companion.forLibrariesTesting(
    libraries: Collection<String>,
    extraCompilerArguments: List<String> = emptyList(),
): ReplProvider =
    withDefaultClasspathResolution(
        shouldResolveToEmpty = { it in libraries },
        extraCompilerArguments = extraCompilerArguments,
    )

private val httpUtil = createLibraryHttpUtil(DefaultKernelLoggerFactory)

fun withDefaultClasspathResolution(
    shouldResolve: (String?) -> Boolean = { true },
    shouldResolveToEmpty: (String?) -> Boolean = { false },
    extraCompilerArguments: List<String> = emptyList(),
) = ReplProvider { classpath ->
    val resolver =
        run {
            var res: LibraryResolver = ClasspathLibraryResolver(httpUtil.libraryDescriptorsManager, null, shouldResolve)
            res = ToEmptyLibraryResolver(res, shouldResolveToEmpty)
            res
        }

    createRepl(
        httpUtil = httpUtil,
        scriptClasspath = classpath,
        kernelRunMode = EmbeddedKernelRunMode,
        mavenRepositories = defaultRepositoriesCoordinates,
        libraryResolver = resolver,
        inMemoryReplResultsHolder = NoOpInMemoryReplResultsHolder,
        extraCompilerArguments = extraCompilerArguments,
    ).apply {
        initializeWithCurrentClasspath()
    }
}

private fun ReplForJupyter.initializeWithCurrentClasspath() {
    eval { librariesScanner.addLibrariesFromClassLoader(currentClassLoader, this, notebook) }
}
