package dfbuild

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import java.io.File

/**
 * Returns the root directory of the whole project
 * by finding the `gradlew` file in the parent directories.
 */
fun Project.findRootDir(): File = projectDir.findRootDir()

fun File.findRootDir(): File {
    var rootDir = this
    while (!rootDir.resolve("gradlew").exists()) {
        rootDir = rootDir.parentFile
            ?: error("Could not find parent of '${rootDir.absolutePath}'")
    }
    return rootDir
}

// tiny reflection-based solution to retrieve the original version name from the accessor,
// so we can keep the list of version type-safe
fun Any.getVersionName(): String {
    val klass = this::class.java
    if (this is Provider<*>) {
        val valueField = klass.declaredFields
            .single { it.name == "value" }
            .also { it.isAccessible = true }
        val result = valueField.get(this)
        val lambdaClass = result::class.java
        val nameField = lambdaClass.declaredFields
            .single { it.type == String::class.java }
            .also { it.isAccessible = true }
        val name = nameField.get(result) as String
        return name.replace('.', '-')
    } else {
        val asProviderFunction = klass.declaredMethods
            .single { it.name == "asProvider" }
        val provider = asProviderFunction.invoke(this) as Provider<*>
        return provider.getVersionName()
    }
}
