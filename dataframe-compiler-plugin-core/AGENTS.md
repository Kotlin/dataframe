# AGENTS.md — dataframe-compiler-plugin-core

Guidance for this module. See the root `AGENTS.md` for repo-wide build/style/KoDEx rules; only module-specific
details are here.

## What this module is (read this first)

`dataframe-compiler-plugin-core` (artifact `dataframe-compiler-plugin-core`) is **NOT a Kotlin compiler plugin.**
It contains no `CompilerPluginRegistrar`, no FIR/IR extensions, no `@ExperimentalCompilerApi` entry points.

It is a **shaded subset of `:core`** — the runtime API and interpreter logic that the *real* Kotlin DataFrame
compiler plugin bundles and calls into at compile time to evaluate DataFrame operations (compile-time interpreters
of operations). It is bundled together with the compiler plugin in Kotlin, and by extension in IntelliJ.

The actual compiler plugin lives in the **Kotlin repository**, not here:
`github.com/JetBrains/kotlin/tree/master/plugins/kotlin-dataframe`. (This repo's `plugins/kotlin-dataframe` is a
disabled legacy copy — see the root `AGENTS.md`.)

## Structure

- **No `main` sources of its own.** The module is a repackaging (`ShadowJar`) of `:core`:
  `implementation(projects.core)` plus a shadow config that strips what the interpreters don't need
  (`jupyter/**`, `io/**`, `documentation/**`, `impl/io/**`, `kotlin-reflect`/`kotlin-stdlib`, kotlinpoet,
  serialization, …). The surviving packages are the `:core` `api`, `impl/api`, `columns`, `schema`, `codeGen`,
  and `annotations` code.
- The only source file is the test `src/test/kotlin/org/jetbrains/kotlinx/dataframe/PluginApiUsages.kt`, which
  verifies that — even with those dependencies excluded — the required runtime API (`convert`, `with`, `asColumn`,
  `map`, `dataFrameOf`, …) still resolves and runs without exceptions.
- Publishes under `publicationName = "shadowed"`.

## Working here

- The guiding principle (from the module README): **aim to include only necessary code.** If you widen what the
  compiler plugin needs from `:core`, update the shadow `exclude(...)` list accordingly and keep the surface minimal.
- Don't add feature code here — put library logic in `:core`. This module only *repackages* `:core`.
- If `PluginApiUsages.kt` starts failing, an exclude is likely stripping API the plugin depends on.
