## :build-logic

This project contains all shared logic for build logic (`build.gradle.kts` files).

The entire DataFrame project is built
using [Composite Builds](https://docs.gradle.org/current/userguide/composite_builds.html)
and [Pre-compiled Script Plugins](https://docs.gradle.org/current/userguide/implementing_gradle_plugins_precompiled.html)
acting as [Convention Plugins](https://docs.gradle.org/current/userguide/implementing_gradle_plugins_convention.html).

### Plugins:
- `dfbuild.base`: common build logic for all projects.

