# AGENTS.md — examples

Guidance for this folder. See the root `AGENTS.md` for repo-wide rules.

## What this is

End-user demo projects for Kotlin DataFrame. **These are NOT part of the main Gradle build** — `examples` is not in
`settings.gradle.kts` and is not an included build. Each example is a **standalone Gradle or Maven project** with its
own `settings.gradle.kts` / `pom.xml` and its own dependency versions.

## Layout

- `projects/` — example projects targeting the **latest stable** DataFrame release: `android-example`, `exposed`,
  `hibernate`, `json-openapi`, `kotlin-dataframe-plugin-gradle-example`, `kotlin-dataframe-plugin-maven-example`,
  `kotlin-spark`, `movies`, `multik`, `spark-parquet-dataframe`, `titanic`, `youtube`, …
- `projects/dev/` — the same set built against the current **dev/master** sources.
- `notebooks/` — Kotlin/Jupyter `.ipynb` analytics demos (titanic, movies, netflix, github, wine, youtube,
  quickstart, …), some mirrored on Datalore.

## How they are built / run

Orchestrated indirectly by the root convention plugin `dfbuild.buildExampleProjects`
(`build-logic/src/main/kotlin/dfbuild.buildExampleProjects.gradle.kts`), which **scans** the folders and generates
tasks — it does not `includeBuild` them.

- **Version sync:** `syncAllExampleFolders` (and per-folder `sync<Folder>(Dev)`) copy versions from the main
  `gradle/libs.versions.toml` into each example; wired into `assemble`. A newly synced dependency version must be
  added to the `versionsToSync` list in that convention plugin.
- **Build/verify:** `runBuildAllExampleFolders` (and subsets `runBuildDev/Release/Gradle/Maven/Android/…`) generate
  and run JUnit tests via GradleTestKit / maven-invoker. `runBuildAllExampleFolders` is attached to `:test` **only**
  when `-Pkotlin.dataframe.debug=true` (so CI/PR builds exercise the examples).
- **After a release:** `promoteDevExamples` deletes `projects/*` (except `dev`) and copies `projects/dev/*` up.
- **Android** examples require `android.sdk.dir` to be set; **dev-Maven** examples trigger `:publishLocal`.

Manual: `./gradlew runBuildAllExampleFolders -Pkotlin.dataframe.debug=true`. Don't add these projects to
`settings.gradle.kts`; keep each example self-contained.
