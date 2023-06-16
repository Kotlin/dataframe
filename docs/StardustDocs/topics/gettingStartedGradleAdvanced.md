[//]: # (title: Get started with Kotlin DataFrame on Gradle with custom configuration)

## Gradle

The Kotlin DataFrame library is published to Maven Central,
so you can add the following line to your Kotlin DSL
buildscript to depend on it:

### General configuration

<tabs>
<tab title="Kotlin DSL">

```kotlin
plugins {
    id("org.jetbrains.kotlinx.dataframe") version "%dataFrameVersion%"
}

dependencies {
    implementation("org.jetbrains.kotlinx:dataframe:%dataFrameVersion%")
}

// Below only applies to Android projects
android {
    defaultConfig {
        minSdk = 26 // Android O+
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packaging {
        resources {
            pickFirsts += listOf(
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/ASL-2.0.txt",
                "META-INF/LICENSE.md",
                "META-INF/NOTICE.md",
                "META-INF/LGPL-3.0.txt",
            )
            excludes += listOf(
                "META-INF/kotlin-jupyter-libraries/libraries.json",
                "META-INF/{INDEX.LIST,DEPENDENCIES}",
                "{draftv3,draftv4}/schema",
                "arrow-git.properties",
            )
        }
    }
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { 
    kotlinOptions.jvmTarget = "1.8" 
}
```

</tab>

<tab title="Groovy DSL">

```groovy
plugins {
    id "org.jetbrains.kotlinx.dataframe" version "%dataFrameVersion%"
}

dependencies {
    implementation 'org.jetbrains.kotlinx:dataframe:%dataFrameVersion%'
}

// Below only applies to Android projects
android {
    defaultConfig {
        minSdk 26 // Android O+
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packaging {
        resources {
            pickFirsts += [
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/ASL-2.0.txt",
                "META-INF/LICENSE.md",
                "META-INF/NOTICE.md",
                "META-INF/LGPL-3.0.txt",
            ]
            excludes += [
                "META-INF/kotlin-jupyter-libraries/libraries.json",
                "META-INF/{INDEX.LIST,DEPENDENCIES}",
                "{draftv3,draftv4}/schema",
                "arrow-git.properties",
            ]
        }
    }
}
tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile).configureEach { 
    kotlinOptions.jvmTarget = "1.8"
}
```

</tab>

</tabs>

Note that it's better to use the same version for a library and plugin to avoid unpredictable errors.
After plugin configuration, you can try it out, for [example](schemasGradle.md#annotation-processing).

### Custom configuration

If you want to avoid adding unnecessary dependencies, you can choose from the following artifacts:

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    // Artifact containing all APIs and implementations
    implementation("org.jetbrains.kotlinx:dataframe-core:%dataFrameVersion%")
    // Optional formats support
    implementation("org.jetbrains.kotlinx:dataframe-excel:%dataFrameVersion%")
    implementation("org.jetbrains.kotlinx:dataframe-arrow:%dataFrameVersion%")
    implementation("org.jetbrains.kotlinx:dataframe-openapi:%dataFrameVersion%")
}
```

</tab>

<tab title="Groovy DSL">

```groovy
dependencies {
    // Artifact containing all APIs and implementations
    implementation 'org.jetbrains.kotlinx:dataframe-core:%dataFrameVersion%'
    // Optional formats support 
    implementation 'org.jetbrains.kotlinx:dataframe-excel:%dataFrameVersion%'
    implementation 'org.jetbrains.kotlinx:dataframe-arrow:%dataFrameVersion%'
    implementation 'org.jetbrains.kotlinx:dataframe-openapi:%dataFrameVersion%'
}
```

</tab>

</tabs>

#### Linter configuration

We provide a Gradle plugin that generates interfaces with your data.
Use this configuration to prevent linter from complaining about formatting in the generated sources.

<tabs>
<tab title="Kotlin DSL">

```kotlin
// (Only if you use kotlint) Excludes for `kotlint`:
tasks.withType<org.jmailen.gradle.kotlinter.tasks.LintTask> {
    exclude {
        it.name.endsWith(".Generated.kt")
    }
    exclude {
        it.name.endsWith("\$Extensions.kt")
    }
}
```

</tab>

<tab title="Groovy DSL">

```groovy
tasks.withType(org.jmailen.gradle.kotlinter.tasks.LintTask).all {
    exclude {
        it.name.endsWith(".Generated.kt")
    }
    exclude {
        it.name.endsWith("\$Extensions.kt")
    }
}
```

</tab>

</tabs>
