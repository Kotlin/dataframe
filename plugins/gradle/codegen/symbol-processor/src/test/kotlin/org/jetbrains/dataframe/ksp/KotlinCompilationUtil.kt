package org.jetbrains.dataframe.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.config.JvmTarget
import java.io.File
import java.io.OutputStream
import java.net.URLClassLoader

internal object KotlinCompilationUtil {
    fun prepareCompilation(
        sources: List<SourceFile>,
        outputStream: OutputStream,
        classpaths: List<File> = emptyList()
    ): KotlinCompilation {
        val compilation = KotlinCompilation()
        val srcRoot = compilation.workingDir.resolve("ksp/srcInput")
        val javaSrcRoot = srcRoot.resolve("java")
        val kotlinSrcRoot = srcRoot.resolve("kotlin")
        compilation.sources = sources
        // workaround for https://github.com/tschuchortdev/kotlin-compile-testing/issues/105
        compilation.kotlincArguments += "-Xjava-source-roots=${javaSrcRoot.absolutePath}"
        compilation.jvmDefault = "enable"
        compilation.jvmTarget = JvmTarget.JVM_1_8.description
        compilation.inheritClassPath = false
        compilation.verbose = false
        compilation.classpaths = Classpaths.inheritedClasspath + classpaths
        compilation.messageOutputStream = outputStream
        compilation.kotlinStdLibJar = Classpaths.kotlinStdLibJar
        compilation.kotlinStdLibCommonJar = Classpaths.kotlinStdLibCommonJar
        compilation.kotlinStdLibJdkJar = Classpaths.kotlinStdLibJdkJar
        compilation.kotlinReflectJar = Classpaths.kotlinReflectJar
        compilation.kotlinScriptRuntimeJar = Classpaths.kotlinScriptRuntimeJar
        return compilation
    }

    /**
     * Helper object to persist common classpaths resolved by KCT to make sure it does not
     * re-resolve host classpath repeatedly and also runs compilation with a smaller classpath.
     * see: https://github.com/tschuchortdev/kotlin-compile-testing/issues/113
     */
    private object Classpaths {

        val inheritedClasspath: List<File>

        /**
         * These jars are files that Kotlin Compile Testing discovers from classpath. It uses a
         * rather expensive way of discovering these so we cache them here for now.
         *
         * We can remove this cache once we update to a version that includes the fix in KCT:
         * https://github.com/tschuchortdev/kotlin-compile-testing/pull/114
         */
        val kotlinStdLibJar: File?
        val kotlinStdLibCommonJar: File?
        val kotlinStdLibJdkJar: File?
        val kotlinReflectJar: File?
        val kotlinScriptRuntimeJar: File?

        init {
            // create a KotlinCompilation to resolve common jars
            val compilation = KotlinCompilation()
            kotlinStdLibJar = compilation.kotlinStdLibJar
            kotlinStdLibCommonJar = compilation.kotlinStdLibCommonJar
            kotlinStdLibJdkJar = compilation.kotlinStdLibJdkJar
            kotlinReflectJar = compilation.kotlinReflectJar
            kotlinScriptRuntimeJar = compilation.kotlinScriptRuntimeJar

            inheritedClasspath = getClasspathFromClassloader(
                KotlinCompilationUtil::class.java.classLoader
            )
        }
    }

    // ported from https://github.com/google/compile-testing/blob/master/src/main/java/com
    // /google/testing/compile/Compiler.java#L231
    private fun getClasspathFromClassloader(referenceClassLoader: ClassLoader): List<File> {
        val platformClassLoader: ClassLoader = ClassLoader.getPlatformClassLoader()
        var currentClassloader = referenceClassLoader
        val systemClassLoader = ClassLoader.getSystemClassLoader()

        // Concatenate search paths from all classloaders in the hierarchy
        // 'till the system classloader.
        val classpaths: MutableSet<String> = LinkedHashSet()
        while (true) {
            if (currentClassloader === systemClassLoader) {
                classpaths.addAll(getSystemClasspaths())
                break
            }
            if (currentClassloader === platformClassLoader) {
                break
            }
            check(currentClassloader is URLClassLoader) {
                """Classpath for compilation could not be extracted
                since $currentClassloader is not an instance of URLClassloader
                """.trimIndent()
            }
            // We only know how to extract classpaths from URLClassloaders.
            currentClassloader.urLs.forEach { url ->
                check(url.protocol == "file") {
                    """Given classloader consists of classpaths which are unsupported for
                    compilation.
                    """.trimIndent()
                }
                classpaths.add(url.path)
            }
            currentClassloader = currentClassloader.parent
        }
        return classpaths.map { File(it) }.filter { it.exists() }
    }
}

/**
 * Returns the list of File's in the system classpath
 *
 * @see getSystemClasspaths
 */
fun getSystemClasspathFiles(): Set<File> {
    return getSystemClasspaths().map { File(it) }.toSet()
}

/**
 * Returns the file paths from the system class loader
 *
 * @see getSystemClasspathFiles
 */
fun getSystemClasspaths(): Set<String> {
    val pathSeparator = System.getProperty("path.separator")!!
    return System.getProperty("java.class.path")!!.split(pathSeparator).toSet()
}
