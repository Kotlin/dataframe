# AGENTS.md — samples

Guidance for this module. See the root `AGENTS.md` for repo-wide build/style/KoDEx rules; only module-specific
details are here.

## What this module is

`:samples` holds the **code samples** (plus DataFrame iframes and Kandy plot images) for the documentation website.
Documentation snippets here are runnable tests: running them produces output that Korro injects into the WriterSide
docs under `docs/StardustDocs/topics/`.

**New documentation samples belong here**, not in `:core`. (Sample migration into this module is in progress —
issue #898; `:core` still has a legacy `samples` source set + `samplesTest`/`korro` tasks that will be removed.)

A test that asserts what a page prints belongs here too, not in `:core`: the dependency runs
`:samples` → `:core` (`build.gradle.kts` takes `:core` through its jars; `core/build.gradle.kts` has no
dependency on `:samples`), so a `:core` test cannot reach `TestBase.peopleDf` and has to copy the fixture
— and a copy stops tracking the original the day someone adds a row to it.

## How it works

- Sample sources live under `src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/**`
  (subpackages `api/`, `io/`, `guides/`, `concepts/`, `schemas/`, …). Test resources (CSV/JSON/XLSX/Parquet/Feather)
  are in `src/test/resources/`.
- The module applies the DataFrame **compiler plugin** (`libs.plugins.dataframe.compiler.plugin`), so samples get
  compile-time type-safe accessors. It depends on the built jars of the I/O modules (`runtimeOnly(projects.dataframe)`)
  — "must depend on jars for the compiler plugin to work".
- **Running the tests emits korro output lines** (into `build/korroOutputLines/`). Then the `korro` task injects the
  sample code + output into the topic markdown; use `korroClean` + `korro` to save/update. Korro's `docs`/`samples`
  includes in `build.gradle.kts` define exactly which `docs/StardustDocs/topics/**` files and sample packages this
  module owns.
- **A text `Output:` block is either generated or stale.** `String.saveSample()` writes one (with the
  `Output:` header), `CodeString.saveSample()` wraps the value in a ` ```kotlin ` fence and
  `String.saveTextSample()` in a ` ```text ` one; korro then injects the block **inside** the
  `<!---FUN-->`…`<!---END-->` region. A block typed by hand sits *outside* `<!---END-->`, and nothing checks it
  ever again. At the time of writing the topics carry 8 generated blocks against 10 hand-written ones, so the
  hand-written shape is common but is the one that rots — prefer `saveSample`.
- **`groupSamples` tab convention:** function-name suffixes map to doc tabs — `_properties` → "Properties",
  `_accessors` → "Accessors", `_strings` → "Strings", `_kotlin` → "Kotlin", `_java` → "Java" (wrapped in
  `<tabs>`/`<tab>`).
- **Iframes / plots:** `SampleHelper` saves DataFrames as HTML iframes and Kandy plots as SVGs; the
  `updateShadowResources` task regenerates `docs/StardustDocs/topics/_shadow_resources.md` (a `<resource>` tag per
  generated file). If a sample output changes, confirm the change is intentional.

## Build / run

- `./gradlew :samples:test` runs samples (needs `--add-opens java.base/java.nio=ALL-UNNAMED`, already set on the
  test task for Arrow). Then `./gradlew :samples:korroClean :samples:korro` to regenerate docs snippets — or run the
  Korro tasks for the whole project.
- Writing a new sample: put it in the correct subpackage matching its topic, write it as a runnable test with
  assertions, use the `_properties`/`_accessors`/… suffixes for grouped variants, and add the target topic file to
  the `korro.docs` include list; for iframe/plot samples, register outputs via `updateShadowResources`.
