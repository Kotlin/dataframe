plugins {
    with(convention.plugins) {
        alias(kotlinJvm8)
    }
    with(libs.plugins) {
        alias(ktlint)
    }
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
