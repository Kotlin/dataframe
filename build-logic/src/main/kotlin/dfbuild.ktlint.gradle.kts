plugins {
    alias(conventions.plugins.dfbuild.base)
    alias(libs.plugins.ktlint.gradle)
}

// rules are set up through .editorconfig
ktlint {
    version = libs.versions.ktlint.asProvider()
}
