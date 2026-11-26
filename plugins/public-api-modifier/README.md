## :plugins:public-api-modifier

This compiler plugin makes `@AccessApiOverload` annotated functions internal.
It could help to produce two artifacts from the same code: one with the full API, one with a reduced API.

This is an exploratory plugin that is NOT actually enabled.
See https://github.com/Kotlin/dataframe/pull/959 for more information.

You can test it by adding `compilerPluginClasspath(projects.pluginApiModifier)` in
[`:core`](../../core/build.gradle.kts).
