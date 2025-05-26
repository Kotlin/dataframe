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
                "META-INF/thirdparty-LICENSE",
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
    implementation("org.jetbrains.kotlinx:dataframe-json:%dataFrameVersion%")
    implementation("org.jetbrains.kotlinx:dataframe-csv:%dataFrameVersion%")
    implementation("org.jetbrains.kotlinx:dataframe-excel:%dataFrameVersion%")
    implementation("org.jetbrains.kotlinx:dataframe-jdbc:%dataFrameVersion%")
    implementation("org.jetbrains.kotlinx:dataframe-arrow:%dataFrameVersion%")

    // experimental
    implementation("org.jetbrains.kotlinx:dataframe-openapi:%dataFrameVersion%")
    
    // experimental
    // This artifact is only needed to directly call functions that generate @DataSchema code from OpenAPI specifications
    // It's used by Gradle and KSP plugins internally.
    // Your project needs dataframe-openapi to use generated code
    implementation("org.jetbrains.kotlinx:dataframe-openapi-generator:%dataFrameVersion%")
}
```

</tab>

<tab title="Groovy DSL">

```groovy
dependencies {
    // Artifact containing all APIs and implementations
    implementation 'org.jetbrains.kotlinx:dataframe-core:%dataFrameVersion%'
    // Optional formats support 
    implementation 'org.jetbrains.kotlinx:dataframe-json:%dataFrameVersion%'
    implementation 'org.jetbrains.kotlinx:dataframe-csv:%dataFrameVersion%'
    implementation 'org.jetbrains.kotlinx:dataframe-excel:%dataFrameVersion%'
    implementation 'org.jetbrains.kotlinx:dataframe-jdbc:%dataFrameVersion%'
    implementation 'org.jetbrains.kotlinx:dataframe-arrow:%dataFrameVersion%'

    // experimental
    implementation 'org.jetbrains.kotlinx:dataframe-openapi:%dataFrameVersion%'

    // experimental
    // This artifact is only needed to directly call functions that generate @DataSchema code from OpenAPI specifications
    // It's used by Gradle and KSP plugins internally.
    // Your project needs dataframe-openapi to use generated code
    implementation 'org.jetbrains.kotlinx:dataframe-openapi-generator:%dataFrameVersion%'
}
```

</tab>

</tabs>

<note>
`dataframe-json` is included with `dataframe-csv` and `dataframe-excel` by default. This is to interact with
JSON structures inside CSV and Excel files. If you don't need this functionality, you can exclude it like:
```kts
implementation("org.jetbrains.kotlinx:dataframe-csv:%dataFrameVersion%") {
    exclude("org.jetbrains.kotlinx", "dataframe-json")
}
```
</note>

#### Linter configuration

We provide a Gradle plugin that generates interfaces with your data.
If you're using any sort of linter, it might complain about them generated sources.

Use a configuration similar to this to prevent your linter from complaining about the 
formatting of the generated sources.

<tabs>
<tab title="Kotlin DSL">

```kotlin
// Exclusions for `kotlinter`, if you use it:
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
// Exclusions for `kotlinter`, if you use it:
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
<tab title=".editorconfig">

```.editorconfig
[{**/*.Generated.kt,**/*$Extensions.kt}]
ktlint = disabled
```

</tab>

</tabs>
