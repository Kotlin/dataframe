plugins {
    alias(convention.plugins.base)
    alias(libs.plugins.ktlint.gradle)
    alias(libs.plugins.ktfmt)
}

// rules are set up through .editorconfig
ktlint {
    version = libs.versions.ktlint.asProvider()
}

ktfmt {
    kotlinLangStyle()
}
