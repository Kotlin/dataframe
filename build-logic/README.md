## :build-logic

This project contains all shared logic for build logic (`build.gradle.kts` files).

The entire DataFrame project is built
using [Composite Builds](https://docs.gradle.org/current/userguide/composite_builds.html)
and [Pre-compiled Script Plugins](https://docs.gradle.org/current/userguide/implementing_gradle_plugins_precompiled.html)
acting as [Convention Plugins](https://docs.gradle.org/current/userguide/implementing_gradle_plugins_convention.html).

### Plugins:
- `dfbuild.base`: common build logic for all projects.

- `dfbuild.kotlinJvmCommon`: common build logic for all Kotlin JVM projects.
    - Includes `dfbuild.base` and `dfbuild.ktlint`.
    - Sets `explicitApi()`, opt-ins, and the toolchain version.
    - Sets up the `instrumentedJars` configuration and task. 
    - This should not be used directly, as a JVM target version needs to be picked.
      Use `dfbuild.kotlinJvm8` (preferred) or `dfbuild.kotlinJvm11` instead.

- `dfbuild.kotlinJvm8`: See `dfbuild.kotlinJvmCommon`.

- `dfbuild.kotlinJvm11`: See `dfbuild.kotlinJvmCommon`.

- `dfbuild.ktlint`: Sets up our linter plugin. Included by default in `dfbuild.kotlinJvmCommon`.

- `dfbuild.kodex`: Sets up [KoDEx](https://github.com/Jolanrensen/KoDEx) KDoc preprocessing.
    Requires `dfbuild.kotlinJvm8` or `dfbuild.kotlinJvm11`.
    - This plugin modifies the `Jar` tasks such that its sources contain KoDEx-preprocessed sources.
    - Available tasks: `processKDocsMain` and `kodex` (alias, does the same thing).
    - All tasks and modifications will be skipped if the Gradle property `skipKodex` exists.
    - The plugin can be configured via the `kodexConvention` extension for your needs.
    - Modules the project depends on are included in the context of KoDEx.
    - See also: https://github.com/Kotlin/dataframe/blob/master/KODEX_KDOC_PREPROCESSING.md

- `dfbuild.buildConfig`: Generates build config compile-time constants,
   like `BuildConfig.VERSION` and `BuildConfig.DEBUG`.
   Is NOT included by default, but can be combined with `dfbuild.kotlinJvm<X>`.

- `dfbuild.buildExampleProjects`: Generates tasks that sync versions and
    build all example projects in the `/examples/projects` and `/examples/projects/dev` directories.
    - NOTE: Only apply this plugin to the root project!
    - NOTE: Add every dependency version that needs to be synced to the `libs.versions.toml` file of the example in
      the `versionsToSync` list!

    - Projects inside `/examples/projects` are built using the latest release version of DataFrame.
      They are meant to be downloadable as separate projects by users.
    - Projects inside `/examples/projects/dev` are built using sources of DataFrame.
      They should be kept up to date as sources change.
    - Available tasks:
        - `sync<NameOfFolder(Dev)>`: Syncs and overwrites build settings and versions.
          Copies and overwrites versions from the main `libs.versions.toml`, `gradle.properties`, etc.
        - `syncAllExampleFolders`: Syncs all example projects. Automatically called on `assemble`.
        - `promoteDevExamples`: Promotes the `/examples/projects/dev` example projects to `/examples/project`.
          To be called after a release. Automatically calls `syncAllExampleFolders` afterward.
        - `generate<NameOfFolder(Dev)>Test`: Generates a Junit test class for the given folder in
          `/build/generated/testBuildingExamples/src/test/kotlin`.
        - `generateAllExampleFoldersTests`: Cleans and generates Junit test classes for all example folders.
        - `runBuild<NameOfFolder(Dev)>`: Executes the generated Junit test class for the given folder and
          runs the build. This automatically syncs and generates the test classes beforehand.
            - `publishLocal` is called automatically when dealing with a dev Maven test.
            - Android tests (folders with "android" in their name) require the `android.sdk.dir` gradle property to be set.
            Else, the test will be skipped.
        - `runBuild<Tag>ExampleFolders`: Runs a subset of builds. `Tag` can be `Dev`, `Gradle`, `Android`, etc.
        - `runBuildAllExampleFolders`: Runs all example project builds.
           Automatically called on `test` when the Gradle property `kotlin.dataframe.debug=true`.
    - Creates the `testBuildingExamples` source set which contains the 
      [TestBuildingExampleProjects base class](src/testBuildingExamples/kotlin/dfbuild/buildExampleProjects/TestBuildingExampleProjects.kt),
      and the generated tests in `/build/generated/testBuildingExamples`,
      plus the right dependencies to run them. `:build-logic` is one of the dependencies as well,
      allowing the tests to call helper functions from it.

- `dfbuild.keywordsGenerator`: Generates enums with restricted Kotlin keywords for `:core`.
    Requires `dfbuild.kotlinJvm8` or `dfbuild.kotlinJvm11`.
    - The sole purpose of this plugin is to provide [:core](../core) with the `generateKeywordsSrc` task.
      This task generates three enum classes: `HardKeywords`, `ModifierKeywords`, and `SoftKeywords`.
      These enums together contain all restricted Kotlin keywords to be taken into account when generating our own
      code in Notebooks or any of our [plugins](../plugins). Words like "package", "fun", "suspend", etc...
      As the Kotlin language can change over time, this task ensures that any changes to the language
      will be reflected in our code generation.

- `dfbuild.caupain`: Allows tracking updates for dependencies in `/gradle/libs.versions.toml`.
    This plugin is meant to be applied only to the main project.
    - See https://github.com/deezer/caupain/tree/main for more information.
    - Run `checkDependencyUpdates` to check for outdated dependencies.
      This produces a report in the terminal, and as HTML and Markdown files in `/build/reports`.
    - There's also `replaceOutdatedDependencies` to replace outdated dependencies directly in the
      `/gradle/libs.versions.toml` file, but be careful.
    - Comment `#ignoreUpdates` in `/gradle/libs.versions.toml` to ignore updates for specific dependencies.
    - `/.github/workflows/check-dependency-updates.yml` is a Github Action which runs `checkDependencyUpdates`. 
  
