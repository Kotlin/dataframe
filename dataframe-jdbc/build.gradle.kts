plugins {
    with(conventions.plugins.dfbuild) {
        alias(kotlinJvm8)
        alias(buildConfig)
        alias(kodex)
    }
    with(libs.plugins) {
        alias(publisher)
        alias(binary.compatibility.validator)
    }
}

group = "org.jetbrains.kotlinx"

dependencies {
    api(projects.core)
    compileOnly(libs.duckdb.jdbc)
    compileOnly(libs.sqlite)
    compileOnly(libs.postgresql)
    implementation(libs.kotlinLogging)
    testImplementation(libs.mariadb)
    testImplementation(libs.sqlite)
    testImplementation(libs.postgresql)
    testImplementation(libs.mysql)
    testImplementation(libs.h2db)
    testImplementation(libs.mssql)
    testImplementation(libs.junit)
    testImplementation(libs.sl4jsimple)
    testImplementation(libs.jts.core)
    testImplementation(libs.duckdb.jdbc)
    testImplementation(projects.dataframeJson)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
    testImplementation(libs.hikaricp)
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.mysql)
    testImplementation(libs.testcontainers.mariadb)
}

kotlinPublications {
    publication {
        publicationName = "dataframeJDBC"
        artifactId = project.name
        description = "JDBC support for Kotlin DataFrame"
        packageName = artifactId
    }
}

tasks.processKDocsMain {
    dependsOn(tasks.generateBuildConfigClasses)
}

private val testcontainersPackage = "org.jetbrains.kotlinx.dataframe.io.testcontainers.*"

tasks.test {
    filter {
        excludeTestsMatching(testcontainersPackage)
    }
}

tasks.register<Test>("testcontainersTest") {
    description = "Runs tests that require Docker via Testcontainers."
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    filter {
        includeTestsMatching(testcontainersPackage)
    }
}
