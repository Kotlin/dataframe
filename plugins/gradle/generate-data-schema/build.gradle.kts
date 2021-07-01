plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    maven(url="https://jitpack.io")
}

dependencies {
    implementation("org.jetbrains.kotlinx:dataframe:0.7.3-dev-277-0.10.0.53")
}
