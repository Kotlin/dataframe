# AGENTS.md — plugins

Guidance for this folder. See the root `AGENTS.md` for repo-wide build/style rules.

## Read this before using anything here

**Most of this folder is dead code.** Three of the six modules are disabled, and none of the three is registered
in `settings.gradle.kts` — so nothing in them is compiled, tested, or published by the normal build. Code in a
disabled module can be arbitrarily out of date and **nothing will fail to tell you**. Do not cite it as evidence
of how anything currently behaves, and do not change it expecting an effect.

| Module | State |
|---|---|
| `kotlin-dataframe` | **Disabled legacy copy** of the Kotlin DataFrame compiler plugin. The live one is developed in the Kotlin repository: `github.com/JetBrains/kotlin/tree/master/plugins/kotlin-dataframe`. See issue #1290. |
| `symbol-processor` | **Disabled** — KSP1 is not compatible with Kotlin 2.3+. |
| `dataframe-gradle-plugin` | **Disabled** for the same reason (it drove `symbol-processor` to generate schemas from a data sample). |
| `expressions-converter` | Active, in `settings.gradle.kts`. |
| `public-api-modifier` | Active, in `settings.gradle.kts`. |
| `keywords-generator` | Separate build with its own Kotlin version. |

Plugins that are still used are being migrated to convention plugins in `build-logic/`.

## If you came here looking for the compiler plugin

Two things in *this* repo actually affect it, and `plugins/kotlin-dataframe` is neither:

- **`dataframe-compiler-plugin-core`** — a shaded subset of `:core` (its `api`, `impl/api`, `columns`, `schema`,
  `codeGen`, `annotations`) that the real plugin, and by extension IntelliJ, bundles to run compile-time
  interpreters. A change to `:core` ships inside the plugin through it. See its `AGENTS.md`.
- **The `@Interpretable` / `@Converter` annotations** on `:core`'s public API — the real plugin's interpreters
  read them. Adding or removing a parameter on an annotated function needs a matching interpreter change in the
  Kotlin repository.
