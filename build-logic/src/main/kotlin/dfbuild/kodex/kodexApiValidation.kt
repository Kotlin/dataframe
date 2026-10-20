package dfbuild.kodex

import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named

/**
 * Makes the `apiBuild` task of the binary-compatibility-validator (used by `apiCheck` and `apiDump`)
 * read the public API from the KoDEx-processed [jar][kodexJarTaskName] instead of the `main` classes.
 *
 * If [skipApiValidation] is `true`, the task will be skipped.
 *
 * Should only be called when the binary-compatibility-validator plugin is applied to this project.
 *
 * NOTE: The binary-compatibility-validator classes (like `KotlinApiBuildTask`) are not accessible from `build-logic`,
 * as modules apply the plugin themselves with a version. Putting it on the `build-logic` classpath would break that.
 * That's why `inputJar` is accessed by name.
 */
internal fun Project.useKodexJarForApiValidation(kodexJarTaskName: String, skipApiValidation: Boolean) {
    tasks.named { it == "apiBuild" }.configureEach {
        onlyIf { !skipApiValidation }
        // looked up lazily, as the KoDEx jar task is only registered in `afterEvaluate {}`
        val kodexJar = provider { tasks.named<Jar>(kodexJarTaskName) }.flatMap { it }

        // `KotlinApiBuildTask.inputJar` takes precedence over `inputClassesDirs`
        val inputJar = property("inputJar") as RegularFileProperty
        inputJar.set(kodexJar.flatMap { it.archiveFile })
        dependsOn(kodexJarTaskName)
    }
}
