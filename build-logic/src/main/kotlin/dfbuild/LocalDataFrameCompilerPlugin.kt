package dfbuild

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.dependencies
import java.io.File

/**
 * The DataFrame compiler plugin only works if it has access to the published
 * DataFrame jars. Simple module dependencies won't work.
 *
 * This function sets up the current project to depend on the
 * [required DataFrame module](requiredDataFrameModules) jars produced by their
 * `instrumentedJars` configurations.
 *
 * It also ensures any `api()` dependencies of the required DataFrame modules are
 * introduced as `implementation()` dependencies of the current project.
 *
 * For example:
 * ```kts
 * dfbuild.localDataFrameModuleDependencies(setOf(
 *     project(":dataframe-arrow"),
 *     project(":dataframe-excel"),
 * ))
 * ```
 *
 * @param modules the DataFrame modules that the current project depends on.
 *   ":core" is always added automatically.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Project.localDataFrameModuleDependencies(modules: Iterable<Project>) {
    val dependentProjects = modules.toSet() + project(":core")

    tasks.withType(
        Class.forName("org.jetbrains.kotlin.gradle.tasks.KotlinCompile") as Class<Task>,
    ) {
        dependentProjects.forEach {
            dependsOn("${it.path}:jar")
        }
    }

    // get the output of the instrumentedJars configuration, aka the jar-files of the compiled modules
    // all modules with jar-task have this artifact in the DataFrame project
    val dependentProjectJarPaths = dependentProjects.map {
        it.configurations
            .getByName("instrumentedJars")
            .artifacts.single()
            .file.absolutePath
            .replace(File.separatorChar, '/')
    }

    dependencies {
        add("runtimeOnly", rootProject.project(":"))
        // Must depend on jars for the compiler plugin to work!
        add("implementation", files(dependentProjectJarPaths))

        // include api() dependencies from dependent projects, as they are not included in the jars
        dependentProjects.forEach {
            it.configurations.getByName("api").dependencies.forEach { dep ->
                if (dep is ExternalModuleDependency) {
                    println("adding implementation(\"${dep.group}:${dep.name}:${dep.version ?: "+"}\")")
                    add("implementation", "${dep.group}:${dep.name}:${dep.version ?: "+"}")
                }
            }
        }
    }
}
