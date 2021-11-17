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

group = "org.jetbrains.kotlin"

tasks.withType<ProcessResources> {
    filesMatching("**/plugin.properties") {
        filter {
            it.replace("%PREPROCESSOR_VERSION%", "$version")
        }
    }
}

tasks.withType<ProcessResources> {
    filesMatching("**/df.properties") {
        filter {
            it.replace(
                "%DATAFRAME_JAR%",
                project(":").configurations.getByName("instrumentedJars").artifacts.single().file.absolutePath.replace(File.separatorChar, '/')
            )
        }
    }
}

gradlePlugin {
    plugins {
        create("schemaGeneratorPlugin") {
            id = "org.jetbrains.kotlin.plugin.dataframe"
            implementationClass = "org.jetbrains.dataframe.gradle.ConvenienceSchemaGeneratorPlugin"
        }
    }
}

pluginBundle {
    // These settings are set for the whole plugin bundle
    website = "https://github.com/Kotlin/dataframe"
    vcsUrl = "https://github.com/Kotlin/dataframe"

    (plugins) {
        "schemaGeneratorPlugin" {
            // id is captured from java-gradle-plugin configuration
            displayName = "Kotlin Dataframe gradle plugin"
            description = "Gradle plugin providing task for inferring data schemas from your CSV or JSON data"
            tags = listOf("dataframe", "kotlin")
        }
    }

    mavenCoordinates {
        groupId = project.group.toString()
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
    implementation(project(":"))
    implementation(kotlin("gradle-plugin-api"))
    implementation(kotlin("gradle-plugin"))
    implementation("com.beust:klaxon:5.5")
    implementation(libs.ksp.gradle)
    implementation(libs.ksp.api)

    testImplementation("junit:junit:4.12")
    testImplementation("io.kotest:kotest-assertions-core:4.6.0")
    testImplementation("com.android.tools.build:gradle-api:4.1.1")
    testImplementation("com.android.tools.build:gradle:4.1.1")
    testImplementation(gradleApi())
}

sourceSets {
    create("integrationTest") {
        withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
            kotlin.srcDir("src/integrationTest/kotlin")
            resources.srcDir("src/integrationTest/resources")
            compileClasspath += sourceSets["main"].output + sourceSets["test"].output + configurations["testRuntimeClasspath"]
            runtimeClasspath += output + compileClasspath + sourceSets["test"].runtimeClasspath
        }
    }
}


val integrationTestConfiguration by configurations.creating {
    extendsFrom(configurations.testImplementation.get())
}

val integrationTestTask = task<Test>("integrationTest") {
    dependsOn(":plugins:symbol-processor:publishToMavenLocal")
    dependsOn(":publishApiPublicationToMavenLocal")
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    shouldRunAfter("test")
}

tasks.check { dependsOn(integrationTestTask) }
