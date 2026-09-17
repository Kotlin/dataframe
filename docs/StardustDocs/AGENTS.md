# AGENTS.md — docs (StardustDocs)

Guidance for the documentation site. See the root `AGENTS.md` for repo-wide rules.

## What this is

The Kotlin DataFrame documentation website, authored as a **JetBrains WriterSide** project (not a Gradle module).
Project marker: `project.ihp`; instance profile / table-of-contents: `d.tree`
(`<instance-profile id="d" name="DataFrame" start-page="Home.topic">`).

## Structure

- `topics/` — the **hand-authored** documentation: `.md` topic files plus `Home.topic` (XML). Subfolders include
  `concepts/`, `guides/`, `schemas/`, `operations/`, `collectionsInterop/`, `dataSources/`, `setup/`, `access/`.
  `topics/_shadow_resources.md` is a **generated** hidden topic (registered `hidden="true"` in `d.tree`).
- `resources/` — **generated / asset content**: `snippets/` (Korro sample HTML + `snippets/kdocs/` = KoDEx
  `@ExportAsHtml` output + `snippets/manual/`), plus `api/`, `guides/`, `io/`, `modify/` (notebook/sample HTML
  iframes) and static data CSVs.
- `images/` — doc images. `cfg/` — WriterSide config (`build-script.xml`, `buildprofiles.xml`, …). Plus `d.tree`,
  `c.list`, `v.list`, `keymap.xml`, `redirection-rules.xml`, `robots.txt`.

## Edit vs don't-edit

- **Edit by hand:** topic markdown under `topics/**/*.md` and `Home.topic`; the TOC `d.tree`; images; `cfg/`
  config; and the explicitly-kept `resources/**/manual/**` snippets.
- **Do NOT hand-edit (generated, auto-regenerated):** everything else under `resources/**` (Korro `snippets/`,
  `snippets/kdocs/`, and the `api`/`io`/`guides`/`modify` iframe HTML) and the generated
  `topics/_shadow_resources.md`. A CI bot regenerates and auto-commits these on `master`.

## Adding example code to a topic

**Never hand-write a ` ```kotlin ` block in a topic.** All Kotlin in the docs must be a Korro sample, so that it
is compiled and run on every build — a hand-written block is the way a hallucinated or long-dead API reaches the
website. Only `text`-fenced pseudo-grammar (`operation { columnMapping }: DataFrame`) is written by hand.

To add one:

1. **Find which module owns the topic.** `:core`'s korro block takes `topics/*.md` + `topics/concepts/*.md`
   wholesale; `:samples` has an explicit `include(...)` allow-list in `samples/build.gradle.kts`. If the page
   already has an `<!---IMPORT ...-->` line, the class it names tells you the owner. Don't split one topic
   across both modules — both korro tasks would write the same file.
2. **Add the sample to that module's sample class**, with the body wrapped in `// SampleStart` / `// SampleEnd`.
   New pages should go to `:samples` (migration #898); an existing `:core` page keeps its samples next to its
   siblings. In `:core` a sample is a `@Test @TransformDataFrameExpressions fun` — see step 4 for when that
   annotation is allowed. In `:samples` it is a plain `@Test fun`: the annotation is not used there at all
   (it appears in nine `core/src/test` files and in none under `samples/`), and rendered output comes from
   `SampleHelper` instead.
3. **Put `<!---FUN funName-->` / `<!---END-->` in the topic** and run korro to fill it in
   (`./gradlew core:korro`, or `samples:korro`). Suffix the function `_properties` / `_strings` to get tabs.
4. **Get the rendered result.** The two modules do this differently.
   - **`:core`** — run with `DATAFRAME_SAVE_OUTPUTS=1`; korro then injects the `<inline-frame>` itself and
     writes the matching `resources/snippets/*.html`.
     **Only annotate a sample with `@TransformDataFrameExpressions` when its last expression is a `DataFrame`
     or a `GroupBy`.** The expressions converter renders nothing else, and it fails in two different ways:
     a sample ending in a `DataColumn` or a `List` *fails* in `samplesTest`, while a sample that ends in one of
     those *after* a renderable step silently falls back to rendering that step — so the page shows an
     `<inline-frame>` of the intermediate `groupBy` under an example whose result is a `List`. Two such samples
     then render byte-identical frames.
     Dropping the annotation does **not** rescue such a sample: `TestBase.save()` runs for every `@Test` under
     `DATAFRAME_SAVE_OUTPUTS` and then errors with `function doesn't have any dataframe expression`, so the test
     fails either way (`convertColumnTo` in `Modify.kt` is one of the pre-existing `samplesTest` failures, not a
     precedent to copy).
     The ways out, best first: **move the page to `:samples`**, where a sample renders its result whatever its
     type and no annotation is involved — that is the direction of #898 anyway, and `groupBy.md`, `pivot.md`,
     `countDistinct.md` and `filter.md` already went that way; end the sample in a `DataFrame`; or, only while
     the page is still `:core`-owned, render the result by hand with `PluginCallbackProxy.overrideHtmlOutput`,
     as `JoinWith.kt` does. The `map` page went the first route in #2066: its samples moved to
     `samples/…/api/MapSamples.kt`, and `saveDfHtmlSample()` there renders a `DataColumn` and a `FrameColumn`
     with no annotation and no manual HTML at all.
   - **`:samples`** — run the samples as tests to produce the HTML, then add the `<inline-frame>` line by hand
     right after `<!---END-->`; korro does not inject it in this module.
   In both modules the generated HTML only reaches the site once it is registered in
   `topics/_shadow_resources.md` — run `./gradlew :samples:updateShadowResources` and commit the new
   `<resource>` lines, otherwise the iframe is on the page but the table is not. The `src` of an
   `<inline-frame>` is a **flat filename**, while `SampleHelper` writes the file to
   `resources/<subFolder>/<sampleName>/`, so that index is what makes the two meet.
5. **Revert the collateral.** A local korro run rewrites/deletes `resources/snippets/**` for every sample that
   did *not* run in your invocation, and can touch unrelated topics. `git checkout --` everything except the
   topic you edited and the snippet files for your own new samples.
   Those two are the one exception to "don't hand-edit `resources/**`" above: the snippet a new sample of yours
   produces is committed together with the topic that embeds it, so the page is not broken until the CI bot next
   regenerates everything on `master` (that is how the doc PRs in `git log -- resources/snippets` do it).
   Everything else under `resources/**` stays CI-owned.

## How content is injected

- **Korro** (in `:samples` and `:core`) reads the topic markdown, runs the sample tests, and injects code + output.
  `:core`'s `clearSamplesOutputs`/`copySamplesOutputs` regenerate `resources/snippets` (excluding `**/manual/**` and
  `**/kdocs/**`); `:samples` owns the broader topic allow-list. See `samples/AGENTS.md`.
- **KoDEx** produces `resources/snippets/kdocs/` HTML from `@ExportAsHtml` during KDoc preprocessing.
- `_shadow_resources.md` is regenerated by the `updateShadowResources` task in `:samples`.
- The site itself is built/previewed by WriterSide (IDE plugin or CI builder) from `project.ihp` + `d.tree` +
  `cfg/build-script.xml`; Gradle only produces the injected snippets/iframes.
