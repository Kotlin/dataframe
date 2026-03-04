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

- `dfbuild.buildConfig`: Generates build config compile-time constants,
   like `BuildConfig.VERSION` and `BuildConfig.DEBUG`.
   Is NOT included by default, but can be combined with `dfbuild.kotlinJvm<X>`.

- `dfbuild.buildExampleProjects`: Generates tasks that sync versions and
    build all example projects in the `/examples/projects` and `/examples/projects/dev` directories.
    - Only apply this plugin to the root project!
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
