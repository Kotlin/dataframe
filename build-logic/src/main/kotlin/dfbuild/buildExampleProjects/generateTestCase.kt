package dfbuild.buildExampleProjects

import org.intellij.lang.annotations.Language
import java.io.File

typealias Code = String

@Language("kt")
fun generateTestCase(testClassName: String, folder: File, isDev: Boolean, tags: List<String>): Code =
    """
    package dfbuild.buildExampleProjects
    
    import org.junit.jupiter.api.Tag
    import org.junit.jupiter.api.Test
    import java.io.File

    class $testClassName : TestBuildingExampleProjects() {
        val folder: File = File("${folder.path}")
        val isDev: Boolean = $isDev
        
        ${tags.joinToString(prefix = "@[", separator = " ", postfix = "]") { "Tag(\"$it\")" }}
        @Test
        fun `can build project successfully`(): Unit =
            buildExampleProject(
                folder = folder,
                isDev = isDev,
            )
    }

    """.trimIndent()
