import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier.Internal
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier.Package
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier.Protected
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier.Public

plugins {
    alias(conventions.plugins.dfbuild.base)

    alias(libs.plugins.dokka)
}

dokka {
    dokkaSourceSets.configureEach {
        documentedVisibilities(Public, Protected, Internal, Package)
    }
}
