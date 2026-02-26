package dfbuild.buildExampleProjects

import dfbuild.toCamelCaseByDelimiters
import org.apache.maven.shared.invoker.DefaultInvocationRequest
import org.apache.maven.shared.invoker.DefaultInvoker
import org.gradle.tooling.BuildException
import org.gradle.tooling.GradleConnector
import org.junit.AssumptionViolatedException
import java.io.File

/**
 * Implementations of this class are auto-generated from 'examples/projects'
 * into 'build/generated/testBuildingExamples'.
 *
 * This class, as well as the generated tests, are automatically registered as
 * the 'testBuildingExamples' [SourceSet][org.gradle.api.tasks.SourceSet] by the
 * `dfbuild.buildExampleProjects` convention plugin.
 */
@Suppress("unused")
abstract class TestBuildingExampleProjects {
    /**
     * Allows receiving certain Gradle properties.
     * These are passed down to the tests from the `dfbuild.buildExampleProjects` convention plugin.
     */
    protected fun getGradleProperty(name: String): String? = System.getProperty("gradle.properties.$name")

    protected fun buildExampleProject(folder: File, isDev: Boolean) {
        val name = folder.name.toCamelCaseByDelimiters().replaceFirstChar { it.uppercase() } +
            (if (isDev) "Dev" else "")

        val buildSystem = folder.detectBuildSystem()
            ?: error(
                "Could not detect build system in example project folder '$folder'. We only support ${BuildSystem.entries.toList()}.",
            )

        val isAndroid = "android" in name.lowercase()
        when (buildSystem) {
            BuildSystem.GRADLE ->
                buildGradleProject(name = name, folder = folder, isAndroid = isAndroid)

            BuildSystem.MAVEN ->
                buildMavenProject(name = name, folder = folder)
        }
    }

    protected fun buildGradleProject(name: String, folder: File, isAndroid: Boolean) {
        // Needs the android.sdk.dir property to be set or -Pandroid.sdk.dir=... added as Gradle argument
        // when and android-named example is run
        val androidSdkDir = getGradleProperty("android.sdk.dir")
        if (isAndroid && androidSdkDir == null) {
            throw AssumptionViolatedException(
                "Skipping `build$name` because the `android.sdk.dir` property is not to run the Android example '$folder'.",
            )
        }

        GradleConnector.newConnector()
            .forProjectDirectory(folder)
            .connect()
            .use {
                it.newBuild()
                    .forTasks("clean", "build")
                    .withArguments(
                        buildList {
                            if (isAndroid) {
                                this += "-Dsdk.dir=$androidSdkDir"
                                this += "-Dandroid.home=$androidSdkDir"
                            }
                        },
                    )
                    .setStandardInput(System.`in`)
                    .setStandardOutput(System.out)
                    .setStandardError(System.err)
                    .run()
            }
    }

    protected fun buildMavenProject(name: String, folder: File) {
        DefaultInvoker()
            .execute(
                DefaultInvocationRequest().apply {
                    mavenExecutable = folder.resolve("mvnw").also { it.setExecutable(true) }
                    pomFile = folder.resolve("pom.xml")
                    goals = listOf("clean", "compile")
                    setLocalRepositoryDirectory(File(getGradleProperty("maven.repo.local")!!))
                },
            ).let { result ->
                if (result.exitCode != 0) {
                    throw BuildException(
                        "`build$name` failed. Could not build Maven project in '$folder'.",
                        result.executionException,
                    )
                }
            }
    }
}
