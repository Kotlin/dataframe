plugins {
    application
    kotlin("jvm")

    id("org.jetbrains.kotlinx.dataframe")

    // only mandatory if `kotlin.dataframe.add.ksp=false` in gradle.properties
    id("com.google.devtools.ksp")
}

repositories {
    mavenCentral()
    mavenLocal() // in case of local dataframe development
}

application.mainClass.set("org.jetbrains.kotlinx.dataframe.examples.titanic.ml.TitanicKt")

dependencies {
    // implementation("org.jetbrains.kotlinx:dataframe:X.Y.Z")
    implementation(project(":"))
    implementation("org.jetbrains.kotlinx:kotlin-deeplearning-api:0.5.2")
    implementation("org.jetbrains.kotlinx:kotlin-deeplearning-impl:0.5.2")
    implementation("org.jetbrains.kotlinx:kotlin-deeplearning-tensorflow:0.5.2")
    implementation("org.jetbrains.kotlinx:kotlin-deeplearning-dataset:0.5.2")
}

dataframes {
    schema {
        data = "src/main/resources/titanic.csv"
        name = "org.jetbrains.kotlinx.dataframe.examples.titanic.ml.Passenger"
        csvOptions {
            delimiter = ';'
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
}
