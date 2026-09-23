# AGENTS.md — examples

Guidance for this folder. See the root `AGENTS.md` for repo-wide rules.

## What this is

End-user demo projects for Kotlin DataFrame. **These are NOT part of the main Gradle build** — `examples` is not in
`settings.gradle.kts` and is not an included build. Each example is a **standalone Gradle, Maven, or Kotlin Toolchain
project** with its own `settings.gradle.kts` / `pom.xml` / `project.yaml`+`module.yaml` and its own dependency
versions. The build system is auto-detected per folder
(`build-logic/src/main/kotlin/dfbuild/buildExampleProjects/detectBuildSystem.kt`).

## Layout

- `projects/` — example projects targeting the **latest stable** DataFrame release: `android-example`, `exposed`,
  `hibernate`, `json-openapi`, `kotlin-dataframe-plugin-gradle-example`, `kotlin-dataframe-plugin-maven-example`,
  `kotlin-dataframe-plugin-kotlin-toolchain-example`, `kotlin-spark`, `movies`, `multik`, `spark-parquet-dataframe`,
  `titanic`, `youtube`, …
- `projects/dev/` — the same set built against the current **dev/master** sources.
- `notebooks/` — Kotlin/Jupyter `.ipynb` analytics demos (titanic, movies, netflix, github, wine, youtube,
  quickstart, …), some mirrored on Datalore.

## How they are built / run

Orchestrated indirectly by the root convention plugin `dfbuild.buildExampleProjects`
(`build-logic/src/main/kotlin/dfbuild.buildExampleProjects.gradle.kts`), which **scans** the folders and generates
tasks — it does not `includeBuild` them.

- **Version sync:** `syncAllExampleFolders` (and per-folder `sync<Folder>(Dev)`) copy versions from the main
  `gradle/libs.versions.toml` into each example; wired into `assemble`. A newly synced dependency version must be
  added to the `versionsToSync` list in that convention plugin. Per-build-system sync logic lives in
  `build-logic/src/main/kotlin/dfbuild/buildExampleProjects/` (`gradleTasks.kt`, `mavenTasks.kt`,
  `kotlinToolchainTasks.kt`).
- **Build/verify:** `runBuildAllExampleFolders` (and subsets `runBuildDev/Release/Gradle/Maven/Android/…`) generate
  and run JUnit tests via GradleTestKit / maven-invoker / the project's `./kotlin build` wrapper (subset:
  `runBuildKotlinToolchainExampleFolders`). `runBuildAllExampleFolders` is attached to `:test` **only**
  when `-Pkotlin.dataframe.debug=true` (so CI/PR builds exercise the examples).
- **After a release:** `promoteDevExamples` deletes `projects/*` (except `dev`) and copies `projects/dev/*` up.
- **Android** examples require `android.sdk.dir` to be set; **dev-Maven** and **dev-Kotlin Toolchain** examples
  trigger `:publishLocal` (they resolve DataFrame from `build/maven`).

## Kotlin Toolchain example specifics

`kotlin-dataframe-plugin-kotlin-toolchain-example` is configured by `project.yaml` + `module.yaml` (no Gradle/Maven
files) and ships the `kotlin`/`kotlin.bat` wrapper scripts. Sync rewrites its `libs.versions.toml`, `.editorconfig`,
and the `settings: kotlin: version:` block in `module.yaml` (matched by regex — keep that block's layout intact).
The ktlint (`module.yaml`) and exec-maven-plugin (`project.yaml`) versions are **not** synced yet (KTC-5915) — they
can't reference `libs.versions.toml`, so bump them by hand. ktlint formatting is run manually via
`./kotlin task :kotlin-dataframe-plugin-kotlin-toolchain-example:exec-maven-plugin.exec` (see its `README.md`).
User-facing docs: `docs/StardustDocs/topics/setup/SetupKotlinToolchain.md`.

Manual: `./gradlew runBuildAllExampleFolders -Pkotlin.dataframe.debug=true`. Don't add these projects to
`settings.gradle.kts`; keep each example self-contained.
