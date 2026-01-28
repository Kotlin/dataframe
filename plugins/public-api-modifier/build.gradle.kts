plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint)
}

group = "org.jetbrains.kotlinx.dataframe"

dependencies {
    compileOnly(libs.kotlin.compiler)
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src"))
        resources.setSrcDirs(listOf("resources"))
    }
    test {
        java.setSrcDirs(listOf("tests", "tests-gen"))
        resources.setSrcDirs(listOf("testResources"))
    }
}
