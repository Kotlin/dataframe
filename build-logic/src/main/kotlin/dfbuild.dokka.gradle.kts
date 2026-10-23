import dfbuild.findRootDir
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier.Internal
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier.Package
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier.Protected
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier.Public
import java.time.Year

plugins {
    alias(conventions.plugins.dfbuild.base)

    alias(libs.plugins.dokka)
}

val repoRootDir = findRootDir()

dokka {
    dokkaSourceSets.configureEach {
        documentedVisibilities(Public, Protected, Internal, Package)
    }
    pluginsConfiguration.html {
        customAssets.from(repoRootDir.resolve("docs/StardustDocs/images/kotlin-dataframe-logo.svg"))
        customStyleSheets.from(repoRootDir.resolve("docs/dokka/styles/logo-styles.css"))
        homepageLink = "https://kotlin.github.io/dataframe/"
        footerMessage = listOf(
            "© 2021–${Year.now().value} JetBrains s.r.o. and contributors. Licensed under Apache 2.0.",
            "<a href=\"https://kotlin.github.io/dataframe/\">Documentation</a>",
            "<a href=\"https://github.com/Kotlin/dataframe\">GitHub</a>",
            "<a href=\"https://kotlinlang.slack.com/archives/C4W52CFEZ\">Slack Community</a>",
            "<a href=\"https://twitter.com/KotlinForData\">X</a>",
            "<a href=\"https://www.youtube.com/@Kotlin\">YouTube</a>",
        ).joinToString(" | ")
    }
}
