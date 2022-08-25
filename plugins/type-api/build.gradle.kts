plugins {
    kotlin("jvm")/* version "1.7.10"*/
    id("java")
    kotlin("libs.publisher") version "0.0.60-dev-30"
}

group = "org.jetbrains.kotlinx.dataframe"
version = "0.9.0-dev"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
}

dependencies {
    compileOnly(kotlin("compiler-embeddable"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}


kotlinPublications {
    publication {
        publicationName.set("api")
        artifactId.set("type-api")
        description.set("Data processing in Kotlin")
        packageName.set(artifactId)
    }
}

