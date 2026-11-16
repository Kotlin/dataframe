# AGENTS.md — plugins

Guidance for this folder. See the root `AGENTS.md` for repo-wide build/style rules.

## Read this before using anything here

| Module | State |
|---|---|
| `expressions-converter` | Active, in `settings.gradle.kts`. |
| `public-api-modifier` | Active, in `settings.gradle.kts`. |

The deprecated `symbol-processor` and `dataframe-gradle-plugin` modules were removed from the repository
(KSP1 is not compatible with Kotlin 2.3+; their last release is `1.0.0-Beta4`).

Plugins that are still used are being migrated to convention plugins in `build-logic/` — `keywords-generator`
already went that way and is no longer here at all; it lives in `build-logic` as `dfbuild.keywordsGenerator`.
(A stale `plugins/keywords-generator/build/` may still sit in your checkout; it is untracked leftovers.)

## If you came here looking for the compiler plugin

Two things in *this* repo actually affect it, and the plugin itself is neither — it lives in the Kotlin repository
(`github.com/JetBrains/kotlin/tree/master/plugins/kotlin-dataframe`):

- **`dataframe-compiler-plugin-core`** — a shaded subset of `:core` (its `api`, `impl/api`, `columns`, `schema`,
  `codeGen`, `annotations`) that the real plugin, and by extension IntelliJ, bundles to run compile-time
  interpreters. A change to `:core` ships inside the plugin through it. See its `AGENTS.md`.
- **The `@Interpretable` / `@Converter` annotations** on `:core`'s public API — the real plugin's interpreters
  read them. Adding or removing a parameter on an annotated function needs a matching interpreter change in the
  Kotlin repository.
