plugins {
    kotlin("jvm")
}

group = "org.jetbrains.kotlinx"

dependencies {
    implementation(project(":"))
    implementation(kotlin("compiler-embeddable"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
