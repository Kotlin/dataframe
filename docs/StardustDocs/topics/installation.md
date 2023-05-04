[//]: # (title: Installation)

You can use the Kotlin DataFrame library in different environments — as any other JVM library.
The following sections will show how to use the Kotlin DataFrame library in [Jupyter](#jupyter-notebook), [Datalore](#datalore) and in a [Gradle project](#gradle).

## Jupyter Notebook

You can use the Kotlin DataFrame library in Jupyter Notebook and in Jupyter Lab.
To start, install the latest version of [Kotlin kernel](https://github.com/Kotlin/kotlin-jupyter#installation) and start your favorite Jupyter client from
the command line, for example:

```shell
jupyter notebook
```

In the notebook you only have to write single line to start using the Kotlin DataFrame library:

```text
%use dataframe
```

In this case the version which is bundled with the kernel, will be used.
If you want to always use the latest version, add another magic before `%use dataframe`:

```text
%useLatestDescriptors
%use dataframe
```

If you want to use specific version of the Kotlin DataFrame library, you can specify it in brackets:

```text
%use dataframe(%dataFrameVersion%)
```

After loading, all essential types will be already imported, so you can start using the Kotlin DataFrame library. Enjoy!

## Datalore

To start with the Kotlin DataFrame library in Datalore, create a Kotlin notebook first:

![Installation to Datalore](datalore-1.png)

As the Notebook you've created is actually a Jupyter notebook, you can follow the instructions 
in the [previous section](#jupyter-notebook) to turn the Kotlin DataFrame library on. 
The simplest way of doing this is shown on screenshot:

![Datalore notebook](datalore-2.png)

## Gradle

The Kotlin DataFrame library is published to Maven Central, so you can simply add the following line to your Kotlin DSL
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
            excludes += listOf(
                "META-INF/kotlin-jupyter-libraries/libraries.json",
                "META-INF/{AL2.0,LGPL2.1,ASL-2.0.txt,INDEX.LIST,DEPENDENCIES,LICENSE.md,NOTICE.md,LGPL-3.0.txt}",
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
            excludes += [
                "META-INF/kotlin-jupyter-libraries/libraries.json",
                "META-INF/{AL2.0,LGPL2.1,ASL-2.0.txt,INDEX.LIST,DEPENDENCIES,LICENSE.md,NOTICE.md,LGPL-3.0.txt}",
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
After plugin configuration you can try it out with [example](gradle.md#annotation-processing).

### Custom configuration

If you want to avoid adding unnecessary dependency, you can choose from following artifacts:

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

We provide a Gradle plugin that generates interfaces by your data.
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

## Other build systems

If you are using Maven, Ivy or Bazel to configure your build, you can still use the Kotlin DataFrame library in your project.
Just follow the instructions for your build system on [this page](https://search.maven.org/artifact/org.jetbrains.kotlinx/dataframe/0.8.1/jar).
