package buildconfig

import org.jetbrains.amper.plugins.Classpath
import org.jetbrains.amper.plugins.ExecutionAvoidance
import org.jetbrains.amper.plugins.Input
import org.jetbrains.amper.plugins.TaskAction
import java.io.File
import java.nio.file.Path
import kotlin.io.path.pathString

/**
 * Checks and (optionally) autocorrects lint errors when possible in [moduleDir] using ktlint.
 * This helps in committing autocorrect changes in the same commit as where the original code changes were made.
 */
@TaskAction(executionAvoidance = ExecutionAvoidance.Disabled)
fun ktlint(
    @Input ktlintClasspath: Classpath,
    @Input moduleDir: Path,
    doFormat: Boolean,
) {
    val java = ProcessHandle.current().info().command().orElse("java")
    val exitCode = ProcessBuilder(
        *buildList {
            add(java)
            add("-classpath")
            add(ktlintClasspath.resolvedFiles.joinToString(File.pathSeparator) { it.pathString })
            add("com.pinterest.ktlint.Main")
            if (doFormat) add("--format")
            add("--relative")
        }.toTypedArray(),
    ).directory(moduleDir.toFile())
        .inheritIO()
        .start()
        .waitFor()
    check(exitCode == 0) { "ktlint exited with code $exitCode" }
}
