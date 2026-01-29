plugins {
    with(convention.plugins) {
        alias(kotlinJvm8)
    }
    with(libs.plugins) {
        alias(shadow)
        alias(publisher)
        alias(ktlint)
    }
}

group = "org.jetbrains.kotlinx.dataframe"

dependencies {
    compileOnly(libs.kotlin.compiler)

    testImplementation(libs.kotlin.compiler)
    testImplementation(libs.kotlin.compiler.internal.test.framework)

    testRuntimeOnly(projects.core)

    testRuntimeOnly(libs.kotlin.test)
    testRuntimeOnly(libs.kotlin.script.runtime)
    testRuntimeOnly(libs.kotlin.annotations.jvm)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.platform.commons)
    testImplementation(libs.junit.platform.launcher)
    testImplementation(libs.junit.platform.runner)
    testImplementation(libs.junit.platform.suite.api)

    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
    useJUnitPlatform()
    doFirst {
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib", "kotlin-stdlib")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-reflect", "kotlin-reflect")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-test", "kotlin-test")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-script-runtime", "kotlin-script-runtime")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-annotations-jvm", "kotlin-annotations-jvm")
    }
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

tasks.register<JavaExec>("generateTests") {
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass = "org.jetbrains.kotlinx.dataframe.GenerateTestsKt"
}

fun Test.setLibraryProperty(propName: String, jarName: String) {
    val path = project.configurations
        .testRuntimeClasspath.get()
        .files
        .find { """$jarName-\d.*jar""".toRegex().matches(it.name) }
        ?.absolutePath
        ?: return
    systemProperty(propName, path)
}
