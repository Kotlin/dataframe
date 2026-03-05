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
