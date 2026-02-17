package dfbuild.buildExampleProjects

import dfbuild.findRootDir
import dfbuild.toCamelCaseByDelimiters
import org.apache.maven.shared.invoker.DefaultInvocationRequest
import org.apache.maven.shared.invoker.DefaultInvoker
import org.gradle.tooling.BuildException
import org.gradle.tooling.GradleConnector
import org.junit.AssumptionViolatedException
import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.TestFactory
import java.io.File

@Suppress("FunctionName")
class TestBuildingExampleProjects {

    /**
     * Allows receiving certain Gradle properties.
     * These are passed down to the tests from the `dfbuild.buildExampleProjects` convention plugin.
     */
    private fun getGradleProperty(name: String): String? = System.getProperty("gradle.properties.$name")

    @Tag("gradle")
    @TestFactory
    fun `test all example Gradle projects`(): List<DynamicNode> =
        getExampleProjectTestsWhere { it.detectBuildSystem() == BuildSystem.GRADLE }

    @Tag("maven")
    @TestFactory
    fun `test all example Maven projects`(): List<DynamicNode> =
        getExampleProjectTestsWhere { it.detectBuildSystem() == BuildSystem.MAVEN }

    private fun getExampleProjectTestsWhere(predicate: (folder: File) -> Boolean): List<DynamicNode> =
        buildList {
            val rootFolder = File("").absoluteFile.findRootDir()

            val releaseFolders = rootFolder.resolve("examples/projects")
                .listFiles()
                ?.filter { it.isDirectory && it.name != "dev" && predicate(it) }
                .orEmpty()
            if (releaseFolders.isNotEmpty()) {
                this += DynamicContainer.dynamicContainer(
                    "release",
                    releaseFolders.map {
                        setupBuildExampleProjectTest(folder = it, isDev = false)
                    },
                )
            }

            val devFolders = rootFolder.resolve("examples/projects/dev")
                .listFiles()
                ?.filter { it.isDirectory && predicate(it) }
                .orEmpty()
            if (devFolders.isNotEmpty()) {
                this += DynamicContainer.dynamicContainer(
                    "dev",
                    devFolders.map {
                        setupBuildExampleProjectTest(folder = it, isDev = true)
                    },
                )
            }
        }

    private fun setupBuildExampleProjectTest(folder: File, isDev: Boolean): DynamicTest {
        val name = folder.name.toCamelCaseByDelimiters().replaceFirstChar { it.uppercase() } +
            (if (isDev) "Dev" else "")

        val buildSystem = folder.detectBuildSystem()
            ?: error(
                "Could not detect build system in example project folder '$folder'. We only support ${BuildSystem.entries.toList()}.",
            )

        return DynamicTest.dynamicTest(name) {
            when (buildSystem) {
                BuildSystem.GRADLE ->
                    buildGradleProject(name = name, folder = folder)

                BuildSystem.MAVEN ->
                    buildMavenProject(name = name, folder = folder)
            }
        }
    }

    /**
     * Registers task to build the example project.
     */
    private fun buildGradleProject(name: String, folder: File) {
        // Needs the android.sdk.dir property to be set or -Pandroid.sdk.dir=... added as Gradle argument
        // when and android-named example is run
        val isAndroid = "android" in name.lowercase()
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

    private fun buildMavenProject(name: String, folder: File) {
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
