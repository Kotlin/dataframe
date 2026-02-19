## :build-settings-logic

This project contains all shared logic for build settings (`settings.gradle.kts` files).

The entire DataFrame project is built
using [Composite Builds](https://docs.gradle.org/current/userguide/composite_builds.html)
and [Pre-compiled Script Plugins](https://docs.gradle.org/current/userguide/implementing_gradle_plugins_precompiled.html)
acting as [Convention Plugins](https://docs.gradle.org/current/userguide/implementing_gradle_plugins_convention.html).

### Plugins:

- `dfsettings.base`: common settings for all projects; includes setting repositories and Foojay.
- `dfsettings.version-catalog`: makes projects that apply it use the top-level DataFrame version catalog.
- `dfsettings.convention-catalog`: makes projects that apply it gain a `convention.plugins` catalog,
  allowing them to apply convention plugins safely.
- `dfsettings.catalogs`: combinations of the two above.
- `dfsettings.catalogs-inside-convention-plugins`: like `dfsettings.catalogs`, but it uses
  [`dev.panuszewski.typesafe-conventions`](https://github.com/radoslaw-panuszewski/typesafe-conventions-gradle-plugin)
  to make them work from inside build-logic convention plugins.
