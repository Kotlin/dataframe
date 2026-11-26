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
// so we can keep the list of version type-safe in dfbuild.buildExampleProjects
fun Any.getVersionName(): String {
    val klass = this::class.java
    if (this is Provider<*>) {
        val result = try {
            val valueField = klass.declaredFields
                .single { it.name == "value" }
                .also { it.isAccessible = true }
            valueField.get(this)
        } catch (e: Throwable) {
            throw IllegalStateException(
                "`dfbuild.buildExampleProjects` failed to get a version name from libs.versions.toml because the " +
                    "`Provider<String>` did not have an (accessible) `value: Callable<String>` field. " +
                    "`Any.getVersionName()` might need to be fixed. Class: $this",
                e,
            )
        }
        val lambdaClass = result::class.java
        val name = try {
            val nameField = lambdaClass.declaredFields
                .single { it.type == String::class.java }
                .also { it.isAccessible = true }
            nameField.get(result) as String
        } catch (e: Throwable) {
            throw IllegalStateException(
                "`dfbuild.buildExampleProjects` failed to get a version name from libs.versions.toml because the " +
                    "`value: Callable<String>` did not have an (accessible) `String` typed field containing the " +
                    "name of the version. `Any.getVersionName()` might need to be fixed.\nClass: $this",
                e,
            )
        }
        return name.replace('.', '-')
    } else {
        val provider = try {
            val asProviderFunction = klass.declaredMethods
                .single { it.name == "asProvider" }
            asProviderFunction.invoke(this) as Provider<*>
        } catch (e: Throwable) {
            throw IllegalStateException(
                "`dfbuild.buildExampleProjects` failed to get a version name from libs.versions.toml because a " +
                    "version group was expected, but the encountered value did not have an (accessible) " +
                    "`asProvider()` function. `Any.getVersionName()` might need to be fixed.\nClass: $this",
                e,
            )
        }
        return provider.getVersionName()
    }
}
