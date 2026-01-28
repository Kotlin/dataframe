plugins {
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(publisher)
//        alias(kover)
        alias(ktlint)
        alias(binary.compatibility.validator)
    }
}

group = "org.jetbrains.kotlinx"

dependencies {
    api(projects.core)
    compileOnly(libs.duckdb.jdbc)
    compileOnly(libs.sqlite)
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
}

kotlinPublications {
    publication {
        publicationName = "dataframeJDBC"
        artifactId = project.name
        description = "JDBC support for Kotlin DataFrame"
        packageName = artifactId
    }
}

kotlin {
    explicitApi()
}
