# Custom Gradle Configuration

<web-summary>
Add Kotlin DataFrame to your Gradle project and configure only the modules you need for data processing and IO.
</web-summary>

<card-summary>
Modular setup for Kotlin DataFrame — include just the dependencies required for your use case.
</card-summary>

<link-summary>
How to configure Kotlin DataFrame in Gradle using only the relevant modules for your project.
</link-summary>


Kotlin DataFrame is composed of multiple [modules](Modules.md),
allowing you to include only the functionality you need.

To use Kotlin DataFrame in a [Gradle project](SetupGradle.md) — including [Android](SetupAndroid.md) —
you can configure your Gradle buildscript (`build.gradle.kts` or `build.gradle`) with selected dependencies:

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    // Core API and runtime
    implementation("org.jetbrains.kotlinx:dataframe-core:%dataFrameVersion%")

    // Optional IO format support
    implementation("org.jetbrains.kotlinx:dataframe-json:%dataFrameVersion%")
    implementation("org.jetbrains.kotlinx:dataframe-csv:%dataFrameVersion%")
    implementation("org.jetbrains.kotlinx:dataframe-excel:%dataFrameVersion%")
    implementation("org.jetbrains.kotlinx:dataframe-jdbc:%dataFrameVersion%")
    implementation("org.jetbrains.kotlinx:dataframe-arrow:%dataFrameVersion%")

    // Experimental features
    implementation("org.jetbrains.kotlinx:dataframe-geo:%dataFrameVersion%")
    implementation("org.jetbrains.kotlinx:dataframe-openapi:%dataFrameVersion%")

    // Only needed if you generate @DataSchema from OpenAPI specs
    implementation("org.jetbrains.kotlinx:dataframe-openapi-generator:%dataFrameVersion%")
}
```

</tab>
<tab title="Groovy DSL">

```groovy
dependencies {
    // Core API and runtime
    implementation 'org.jetbrains.kotlinx:dataframe-core:%dataFrameVersion%'

    // Optional IO format support
    implementation 'org.jetbrains.kotlinx:dataframe-json:%dataFrameVersion%'
    implementation 'org.jetbrains.kotlinx:dataframe-csv:%dataFrameVersion%'
    implementation 'org.jetbrains.kotlinx:dataframe-excel:%dataFrameVersion%'
    implementation 'org.jetbrains.kotlinx:dataframe-jdbc:%dataFrameVersion%'
    implementation 'org.jetbrains.kotlinx:dataframe-arrow:%dataFrameVersion%'

    // Experimental features
    implementation 'org.jetbrains.kotlinx:dataframe-geo:%dataFrameVersion%'
    implementation 'org.jetbrains.kotlinx:dataframe-openapi:%dataFrameVersion%'

    // Only needed if you generate @DataSchema from OpenAPI specs
    implementation 'org.jetbrains.kotlinx:dataframe-openapi-generator:%dataFrameVersion%'
}
```

</tab>
</tabs>
