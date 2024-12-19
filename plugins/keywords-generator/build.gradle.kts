plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.github.gmazzo.buildconfig") version "5.5.1"
}

val kotlinCompilerVersion: String by project
val kotlinPoetVersion: String by project

repositories {
    mavenCentral()
}

buildConfig {
    packageName = "org.jetbrains.kotlinx.dataframe"
    className = "BuildConfig"
    buildConfigField("kotlinCompilerVersion", kotlinCompilerVersion)
}

dependencies {
    compileOnly(kotlin("compiler-embeddable", kotlinCompilerVersion))
    implementation("com.squareup:kotlinpoet:$kotlinPoetVersion")
}

gradlePlugin {
    plugins {
        create("dependencies") {
            id = "org.jetbrains.dataframe.generator"
            version = "1.0"
            implementationClass = "org.jetbrains.dataframe.keywords.KeywordsGeneratorPlugin"
        }
    }
}
