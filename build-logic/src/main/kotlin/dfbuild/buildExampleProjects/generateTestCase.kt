package dfbuild.buildExampleProjects

import org.intellij.lang.annotations.Language
import java.io.File

typealias Code = String

/**
 * Returns the contents of a test class for the given example project [folder].
 *
 * Each folder needs to have its own test class so the tests can run in parallel.
 *
 * See 'build-logic/src/testBuildingExamples/kotlin/dfbuild/buildExampleProjects/TestBuildingExampleProjects.kt'
 *
 * This implementation of [dfbuild.buildExampleProjects.TestBuildingExampleProjects]
 * should be written into 'build/generated/testBuildingExamples' and included in the
 * 'testBuildingExamples' [SourceSet][org.gradle.api.tasks.SourceSet].
 */
@Language("kt")
fun generateTestCase(
    testClassName: String,
    folder: File,
    isDev: Boolean,
    tags: List<String>,
): Code =
    """
    |package dfbuild.buildExampleProjects
    |
    |import org.junit.jupiter.api.Tag
    |import org.junit.jupiter.api.Test
    |import java.io.File
    |
    |class $testClassName : TestBuildingExampleProjects() {
    |    val folder: File =
    |        File("${folder.path}")
    |    val isDev: Boolean = $isDev
    |
    |    ${tags.joinToString(prefix = "@[", separator = " ", postfix = "]") { "Tag(\"$it\")" }}
    |    @Test
    |    fun `can build project successfully`(): Unit =
    |        buildExampleProject(
    |            folder = folder,
    |            isDev = isDev,
    |        )
    |}
    |
    """.trimMargin()
