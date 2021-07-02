plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    maven(url="https://jitpack.io")
}

gradlePlugin {
    plugins {
        create("schemaGeneratorPlugin") {
            id = "org.jetbrains.dataframe.schema-generator"
            implementationClass = "org.jetbrains.dataframe.gradle.SchemaGeneratorPlugin"
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:dataframe:0.7.3-dev-277-0.10.0.53")
}
