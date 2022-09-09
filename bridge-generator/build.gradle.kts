plugins {
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.dataframe")
    kotlin("libs.publisher") version "0.0.60-dev-30"
}

group = "org.jetbrains.kotlinx.dataframe"
version = "0.9.0-dev"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
}

dependencies {
    implementation("org.jetbrains.kotlinx:dataframe")
    //implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
//    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("io.kotest:kotest-assertions-core:4.6.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

kotlin.sourceSets.getByName("main").kotlin.srcDir("build/generated/ksp/main/kotlin/")
kotlin.sourceSets.getByName("test").kotlin.srcDir("build/generated/ksp/test/kotlin/")

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}


kotlinPublications {
    fairDokkaJars.set(false)
    publication {
        publicationName.set("api")
        artifactId.set("bridge-generator")
        description.set("Data processing in Kotlin")
        packageName.set(artifactId)
    }
}
