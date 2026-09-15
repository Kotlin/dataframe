# Packaging into a fat JAR

<web-summary>
Package Kotlin DataFrame into a fat or uber JAR without losing ServiceLoader providers from META-INF/services.
</web-summary>

<card-summary>
Configure fat JAR packaging so Excel and other ServiceLoader-based integrations continue to work.
</card-summary>

<link-summary>
Learn how to merge META-INF/services resources when packaging Kotlin DataFrame into a fat JAR.
</link-summary>

Fat JARs, also called uber JARs, unpack application dependencies and combine them into a single archive.
When multiple dependencies contain a resource at the same path, the packaging tool must decide how to handle the
duplicate. Service provider configuration files under `META-INF/services` must be **merged**, not replaced or
discarded.

## Symptom

A typical symptom is that an Excel file can be read with `gradle run`, but reading the same file from a fat JAR
fails. For example, `.xlsx` files may work while `.xls` files fail with an exception like this:

```text
java.io.IOException: Your InputStream was neither an OLE2 stream, nor an OOXML stream or you haven't provide the
poi-ooxml*.jar in the classpath/modulepath - FileMagic: OLE2, having providers:
[org.apache.poi.xssf.usermodel.XSSFWorkbookFactory@19e1023e]
```

In this situation, adding `poi-ooxml` does not help: the dependency is already present. The list after
`having providers` shows that Apache POI discovered the provider for `.xlsx`, but not the provider for `.xls`.

## Am I affected?

| Packaging method | Default behavior |
|------------------|------------------|
| Gradle Shadow without service file merging | Affected |
| Ktor `buildFatJar` before 3.5.2 | Affected |
| Ktor `buildFatJar` 3.5.2 or later | Safe: Ktor merges service files and includes their duplicate entries |
| Maven Shade without `ServicesResourceTransformer` | Affected |
| Custom `Jar` task that unpacks dependencies (for example, with `from(zipTree(...))`) | Affected unless it explicitly merges service files |
| Gradle `installDist` or `distZip` | Safe: dependency JARs remain separate |
| Spring Boot `bootJar` | Safe: dependency JARs remain nested |
| A regular classpath, including `gradle run` | Safe: dependency JARs remain separate |

## Why it happens

Java's [`ServiceLoader`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/ServiceLoader.html)
discovers implementations from configuration files under `META-INF/services`. Both `poi` and `poi-ooxml` contain
service descriptors at the same paths:

- `META-INF/services/org.apache.poi.ss.usermodel.WorkbookProvider`
- `META-INF/services/org.apache.poi.extractor.ExtractorProvider`
- `META-INF/services/org.apache.poi.sl.draw.ImageRenderer`

When the dependencies are flattened into a single JAR, each pair of descriptors must be merged. For Excel reading,
the relevant descriptor is `WorkbookProvider`: `poi` declares `HSSFWorkbookFactory`, while `poi-ooxml` declares
`XSSFWorkbookFactory`. The other descriptor collisions can affect text extraction and image rendering in Apache POI.

The problem occurs when fat JAR packaging keeps only one file at each duplicated path. In this case, `ServiceLoader` 
sees only some of the available implementations even though all corresponding `.class` files are present. 
This is a packaging resource collision, not a missing Kotlin DataFrame or Apache POI dependency.

Reading Excel files uses `WorkbookProvider` and is therefore affected. Writing `.xls` and `.xlsx` files creates the
corresponding workbook implementation directly and does not use this service.

## Gradle Shadow

Enable Shadow's service file transformer and allow all service descriptor duplicates to reach it:

```kotlin
import org.gradle.api.file.DuplicatesStrategy

tasks.shadowJar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE // or FAIL
    filesMatching("META-INF/services/**") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE // or WARN
    }
    mergeServiceFiles()
}
```

The `filesMatching` exception is important because duplicate handling happens before resource transformation. 
Without it, an `EXCLUDE` duplicate strategy can discard service descriptors before `mergeServiceFiles()` processes them.
Using `FAIL` for other duplicate resources is a useful way to detect collisions that require an explicit decision.

See [Merging](https://gradleup.com/shadow/configuration/merging/) in the Shadow documentation.

## Ktor `buildFatJar`

Upgrade the Ktor Gradle plugin to version 3.5.2 or later. Starting with 3.5.2, `buildFatJar` configures Shadow to
include duplicate entries under `META-INF/services` and calls `mergeServiceFiles()`.

For an older Ktor version, configure its underlying `shadowJar` task as shown in the Gradle Shadow section.

## Maven Shade

Add the services resource transformer to the Maven Shade plugin configuration:

```xml
<transformers>
    <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer" />
</transformers>
```

See the Maven Shade documentation for
[`ServicesResourceTransformer`](https://maven.apache.org/plugins/maven-shade-plugin/examples/resource-transformers.html#ServicesResourceTransformer).

## Custom `Jar` tasks using `zipTree`

Gradle's `duplicatesStrategy` can keep, exclude, warn about, or fail on duplicate files, but it does not concatenate
their contents. Use Shadow or implement a dedicated merge step for `META-INF/services/**`. If a single executable
JAR is not required, a distribution is simpler and preserves dependency boundaries.

## Package without flattening dependencies

The Gradle Application plugin can build a directory or ZIP distribution in which dependencies remain separate:

```shell
./gradlew installDist
./gradlew distZip
```

Spring Boot's `bootJar` is also safe because it stores dependencies as nested JARs rather than flattening all their
resources into one directory.

## Verify the result

Inspect the service descriptor in the packaged JAR:

```shell
unzip -p build/libs/app-all.jar META-INF/services/org.apache.poi.ss.usermodel.WorkbookProvider
```

It must contain both providers:

```text
org.apache.poi.hssf.usermodel.HSSFWorkbookFactory
org.apache.poi.xssf.usermodel.XSSFWorkbookFactory
```

Kotlin DataFrame modules also contribute implementations to
`META-INF/services/org.jetbrains.kotlinx.dataframe.io.SupportedFormat`. Merging service files preserves all of them,
which is especially relevant to automatic format detection in Kotlin DataFrame 0.x.

See also [DataFrame modules](Modules.md#dataframe-excel) and [reading and writing Excel files](Excel.md).
