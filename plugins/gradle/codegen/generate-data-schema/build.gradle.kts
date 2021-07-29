plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.15.0"
}

repositories {
    mavenCentral()
    maven(url="https://jitpack.io")
    google()
}

group = "org.jetbrains.kotlinx"
version = "0.1-dev"

tasks.withType<ProcessResources> {
    filesMatching("**/plugin.properties") {
        filter {
            it.replace("%PREPROCESSOR_VERSION%", "1.0-SNAPSHOT")
        }
    }
}

gradlePlugin {
    plugins {
        create("schemaGeneratorPlugin") {
            id = "org.jetbrains.dataframe.schema-generator"
            implementationClass = "org.jetbrains.dataframe.gradle.ConvenienceSchemaGeneratorPlugin"
        }
        create("baseSchemaGeneratorPlugin") {
            id = "org.jetbrains.dataframe.schema-generator-base"
            implementationClass = "org.jetbrains.dataframe.gradle.SchemaGeneratorPlugin"
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<JavaCompile>().all {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

dependencies {
    implementation("org.jetbrains.kotlinx:dataframe:0.7.3-dev-277-0.10.0.53")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
    implementation("com.beust:klaxon:5.5")
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:1.5.21-1.0.0-beta05")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.5.21-1.0.0-beta05")

    testImplementation("junit:junit:4.12")
    testImplementation("io.kotest:kotest-assertions-core:4.6.0")
    testImplementation("com.android.tools.build:gradle-api:4.1.1")
    testImplementation("com.android.tools.build:gradle:4.1.1")
}
