plugins {
    application
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

application.mainClass.set("org.jetbrains.kotlinx.dataframe.examples.titanic.ml.TitanicKt")

dependencies {
    implementation(project(":"))
    implementation("org.jetbrains.kotlinx:kotlin-deeplearning-api:0.3.0")
    implementation("org.jetbrains.kotlinx:kotlin-deeplearning-dataset:0.3.0")
}

