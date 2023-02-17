[//]: # (title: Installation)

You can use Kotlin DataFrame library in different environments — as any other JVM library.
The following sections will show how to use Kotlin DataFrame library in [Jupyter](#jupyter-notebook), [Datalore](#datalore) and in a [Gradle project](#gradle).

## Jupyter Notebook

You can use Kotlin DataFrame library in Jupyter Notebook and in Jupyter Lab.
To start, install the latest version of [Kotlin kernel](https://github.com/Kotlin/kotlin-jupyter#installation) and start your favorite Jupyter client from
the command line, for example:

```shell
jupyter notebook
```

In the notebook you only have to write single line to start using [`DataFrame`](DataFrame.md):

```text
%use dataframe
```

In this case the version which is bundled with the kernel, will be used.
If you want to always use the latest version, add another magic before `%use dataframe`:

```text
%useLatestDescriptors
%use dataframe
```

If you want to use specific version of Kotlin DataFrame library, you can specify it in brackets:

```text
%use dataframe(0.8.1)
```

After loading, all essential types will be already imported, so you can start using Kotlin DataFrame library. Enjoy!

## Datalore

To start with Kotlin DataFrame library in Datalore, create a Kotlin notebook first:

![Installation to Datalore](datalore-1.png)

As the Notebook you've created is actually a Jupyter notebook, you can follow the instructions 
in the [previous section](#jupyter-notebook) to turn Kotlin DataFrame library on. 
The simplest way of doing this is shown on screenshot:

![Datalore notebook](datalore-2.png)

## Gradle

Kotlin DataFrame library is published to Maven Central, so you can simply add the following line to your Kotlin DSL
buildscript to depend on it:

### All-in-one artifact

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:dataframe:<version>")
}
```

</tab>

<tab title="Groovy DSL">

```kotlin
dependencies {
    implementation 'org.jetbrains.kotlinx:dataframe:<version>'
}
```

</tab>

</tabs>

### Only what you need

If you want to avoid adding unnecessary dependency, you can choose whatever you need:

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    // Artifact containing all APIs and implementations
    implementation("org.jetbrains.kotlinx:dataframe-core:<version>")
    // Optional formats support
    implementation("org.jetbrains.kotlinx:dataframe-excel:<version>")
    implementation("org.jetbrains.kotlinx:dataframe-arrow:<version>")
}
```

</tab>

<tab title="Groovy DSL">

```groovy
dependencies {
    // Artifact containing all APIs and implementations
    implementation 'org.jetbrains.kotlinx:dataframe-core:<version>'
    // Optional formats support 
    implementation 'org.jetbrains.kotlinx:dataframe-excel:<version>'
    implementation 'org.jetbrains.kotlinx:dataframe-arrow:<version>'
}
```

</tab>

</tabs>

### Data schema preprocessor

We provide a Gradle plugin that generates interfaces by your data.
To use it in your project, pick up the latest version from [here](https://plugins.gradle.org/plugin/org.jetbrains.kotlinx.dataframe)
and follow the configuration:

<tabs>
<tab title="Kotlin DSL">

```kotlin
plugins {
    id("org.jetbrains.kotlinx.dataframe") version "<version>"
}

dependencies {
    implementation("org.jetbrains.kotlinx:dataframe:<version>")
}

// Make IDE aware of the generated code:
kotlin.sourceSets.getByName("main").kotlin.srcDir("build/generated/ksp/main/kotlin/")

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
plugins {
    id("org.jetbrains.kotlinx.dataframe") version "<version>"
}

dependencies {
    implementation("org.jetbrains.kotlinx:dataframe:<version>")
}

// Make IDE aware of the generated code:
kotlin.sourceSets.getByName("main").kotlin.srcDir("build/generated/ksp/main/kotlin/")

// (Only if you use kotlint) Excludes for `kotlint`:
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

<tab title="Multiplatform (JVM target Only)">

```kotlin
plugins {
    id("org.jetbrains.kotlinx.dataframe") version "<version>"
}

kotlin {
    jvm()
    sourceSets {
        val jvmMain by getting {
            // Make IDE aware of the generated code:
            kotlin.srcDir("build/generated/ksp/jvmMain/kotlin/")
            dependencies {
                implementation("org.jetbrains.kotlinx:dataframe:<version>")
            }
        }
    }
}

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

</tabs>

<note>

If the code generated by the plugin isn't resolved in IDE, make sure you've configured source sets according to the snippet above. More information in [KSP documentation](https://github.com/google/ksp/blob/main/docs/quickstart.md#make-ide-aware-of-generated-code)

</note>

Note that it's better to use the same version for a library and plugin to avoid unpredictable errors.
After plugin configuration you can try it out with [example](gradle.md#annotation-processing).

## Other build systems

If you are using Maven, Ivy or Bazel to configure your build, you can still use Kotlin DataFrame library in your project.
Just follow the instructions for your build system on [this page](https://search.maven.org/artifact/org.jetbrains.kotlinx/dataframe/0.8.1/jar).
