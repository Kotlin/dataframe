[//]: # (title: Setup Kotlin DataFrame on Android)

<web-summary>
Integrate Kotlin DataFrame into your Android app using the standard JVM dependency and simple Gradle configuration.
</web-summary>

<card-summary>
Set up Kotlin DataFrame in Android — configure it easily using Gradle and start working with structured data.
</card-summary>

<link-summary>
How to use Kotlin DataFrame in your Android project with Gradle setup and compiler plugin support.
</link-summary>

> See an [Android project example](https://github.com/Kotlin/dataframe/tree/master/examples/android-example).

Kotlin DataFrame doesn't provide a dedicated Android artifact yet, 
but you can add the Kotlin DataFrame JVM dependency to your Android project with minimal configuration:

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    // Core Kotlin DataFrame API, CSV and JSON IO.
    // See custom Gradle setup:
    // https://kotlin.github.io/dataframe/setupcustomgradle.html
    implementation("org.jetbrains.kotlinx:dataframe-core:%dataFrameVersion%")
    implementation("org.jetbrains.kotlinx:dataframe-json:%dataFrameVersion%")
    implementation("org.jetbrains.kotlinx:dataframe-csv:%dataFrameVersion%")
    // You can add any additional IO modules you like, except for 'dataframe-arrow'.
    // Apache Arrow is not supported well on Android.
}

android {
    defaultConfig {
        minSdk = 21
    }
    // Requires Java 8 or higher
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { 
    kotlinOptions.jvmTarget = "1.8" 
}
```

</tab>

<tab title="Groovy DSL">

```groovy
dependencies {
    // Core Kotlin DataFrame API, CSV and JSON IO.
    // See custom Gradle setup:
    // https://kotlin.github.io/dataframe/setupcustomgradle.html
    implementation 'org.jetbrains.kotlinx:dataframe-core:%dataFrameVersion%'
    implementation 'org.jetbrains.kotlinx:dataframe-json:%dataFrameVersion%'
    implementation 'org.jetbrains.kotlinx:dataframe-csv:%dataFrameVersion%'
    // You can add any additional IO modules you like, except for 'dataframe-arrow'.
    // Apache Arrow is not supported well on Android.
}

android {
    defaultConfig {
        minSdk 21
    }
    // Requires Java 8 or higher
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile).configureEach { 
    kotlinOptions.jvmTarget = "1.8"
}
```

</tab>
</tabs>

This setup adds the [Kotlin DataFrame core](Modules.md#dataframe-core) 
as well as a subset of the [IO modules](Modules.md#io-modules) 
(excluding [experimental ones](Modules.md#experimental-modules)).
For flexible configuration, see [Custom configuration](SetupCustomGradle.md).

## Kotlin DataFrame Compiler Plugin

[Kotlin DataFrame Compiler Plugin](Compiler-Plugin.md) enables automatic generation 
of [extension properties](extensionPropertiesApi.md) and updates [data schemas](schemas.md) 
on-the-fly in Android projects, making development with Kotlin DataFrame 
faster, more convenient, and fully type- and name-safe.

> Requires Kotlin 2.2.20-Beta1 or higher.  
> { style = "note" }

To enable the plugin in your Gradle project, add it to the `plugins` section:

<tabs>
<tab title="Kotlin DSL">

```kotlin
plugins {
    kotlin("plugin.dataframe") version "%compilerPluginKotlinVersion%"
}
```

</tab>

<tab title="Groovy DSL">

```groovy
plugins {
    id 'org.jetbrains.kotlin.plugin.dataframe' version '%compilerPluginKotlinVersion%'
}
```

</tab>
</tabs>

Due to [this issue](https://youtrack.jetbrains.com/issue/KT-66735), incremental compilation must be disabled for now. 
Add the following line to your `gradle.properties` file:

```properties
kotlin.incremental=false
```

## Next Steps

* Once Kotlin DataFrame is set up in your Android project, continue with the [](quickstart.md) 
to learn the basics of working with DataFrames.
* Explore [detailed guides and real-world examples](Guides-And-Examples.md) 
to see how Kotlin DataFrame helps in different data tasks.
* Check out the 
[Android project example](https://github.com/Kotlin/dataframe/tree/master/examples/android-example) 
and more [IDEA examples on GitHub](https://github.com/Kotlin/dataframe/tree/master/examples/idea-examples).
* Learn more about the [compiler plugin](Compiler-Plugin.md).
