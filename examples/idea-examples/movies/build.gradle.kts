import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.kotlinx.dataframe")
}

repositories {
    mavenCentral()
}

kotlin.sourceSets.getByName("main").kotlin.srcDir("build/generated/ksp/main/kotlin/")

application.mainClass.set("org.jetbrains.kotlinx.dataframe.examples.movies.MoviesWithDataClassKt")

dependencies {
    implementation(project(":core"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

//dataframes {
//    schema {
//        name = "Repository"
//        data = "https://raw.githubusercontent.com/OAI/OpenAPI-Specification/main/examples/v3.0/petstore.yaml"
//    }
//}

