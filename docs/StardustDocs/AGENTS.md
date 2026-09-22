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
  `topics/_shadow_resources.md`. Fix the source (the sample, the KDoc, the topic) and re-run the generating
  task instead; a hand-edit here is overwritten by the next run.
- **"Generated" does not mean "keep it out of the PR".** Two workflows auto-commit generated output —
  `generated-sources-master.yml` (on `master`) and `generated-sources.yml` ("Preview Generated Code", on
  `pull_request`). Both run `processKDocsMain korro syncExampleFolders` and stage `*/generated-sources`,
  `docs/StardustDocs/resources/snippets`, `docs/StardustDocs/topics` and `examples/projects`. So the Korro
  `<!---FUN …-->` output injected into `topics/*.md` **is** bot-maintained — don't hand-maintain it.
  There are exactly two exceptions, and they must ride along in your PR:
  - the `api`/`io`/`guides`/`modify` iframe HTML — its path is never staged;
  - `topics/_shadow_resources.md` — its path *is* staged, but `updateShadowResources` is not in that task
    list, so the bot can never produce a change for it.

  For both, run the generating task yourself and commit the output together with the rest of the change;
  without it the published page renders with a missing resource.

## Adding example code to a topic

**Never hand-write a ` ```kotlin ` block in a topic.** All Kotlin in the docs must be a Korro sample, so that it
is compiled and run on every build — a hand-written block is the way a hallucinated or long-dead API reaches the
website. Only `text`-fenced pseudo-grammar (`operation { columnMapping }: DataFrame`) is written by hand.

To add one:

1. **Find which module owns the topic.** `:core`'s korro block takes `topics/*.md` + `topics/concepts/*.md`
   wholesale; `:samples` has an explicit `include(...)` allow-list in `samples/build.gradle.kts`. If the page
   already has an `<!---IMPORT ...-->` line, the class it names tells you the owner. Don't split one topic
   across both modules — both korro tasks would write the same file.
   A page dropped into one of the globbed folders joins the scan automatically, and korro's
   `behavior { ignoreMissing = true }` (`samples/build.gradle.kts:90`, `TODO(#898)`) turns a
   `<!---FUN name-->` with no matching sample into a printed `Cannot resolve FUN 'name'` on a **green**
   build. Eight such lines already arrive from `master`, so a new one is invisible in CI — read korro's
   own output after adding or moving a page, don't rely on the exit code.
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
   `resources/<subFolder>/<sampleName>/`, so that index is what makes the two meet. Since it is keyed by the
   bare filename, a sample function name has to be unique across **all** sample classes, not just its own —
   two classes using one name write two different files that collapse into a single `<resource>` entry. When
   the same operation is illustrated on more than one page, prefix the function with the page it belongs to.
5. **Revert the collateral.** A local korro run rewrites/deletes `resources/snippets/**` for every sample that
   did *not* run in your invocation, and can touch unrelated topics. `git checkout --` everything except the
   topic you edited and the snippet files for your own new samples.
   Those two are the one exception to "don't hand-edit `resources/**`" above: the snippet a new sample of yours
   produces is committed together with the topic that embeds it, so the page is not broken until the CI bot next
   regenerates everything on `master` (that is how the doc PRs in `git log -- resources/snippets` do it).
   Everything else under `resources/**` stays CI-owned.
   **Deleting a page takes two steps.** Korro stages the whole topic tree under
   `<module>/build/korro/docs/**` (`:samples` keeps a second copy in `build/korro/check/**`) and copies it
   back over `topics/**` at the end of the run. A page you `git rm` is therefore silently restored as an
   untracked file by the next `core:korro` / `samples:korro`, and `--rerun-tasks` does **not** clear the
   staging dir. Delete the staged copies as well, then re-run and confirm the page stayed gone.

## How content is injected

- **Korro** (in `:samples` and `:core`) reads the topic markdown, runs the sample tests, and injects code + output.
  `:core`'s `clearSamplesOutputs`/`copySamplesOutputs` regenerate `resources/snippets` (excluding `**/manual/**` and
  `**/kdocs/**`); `:samples` owns the broader topic allow-list. See `samples/AGENTS.md`.
- **KoDEx** produces `resources/snippets/kdocs/` HTML from `@ExportAsHtml` during KDoc preprocessing.
- `_shadow_resources.md` is regenerated by the `updateShadowResources` task in `:samples`.
- The site itself is built/previewed by WriterSide (IDE plugin or CI builder) from `project.ihp` + `d.tree` +
  `cfg/build-script.xml`; Gradle only produces the injected snippets/iframes.

## Gotchas

- **One topic, one module.** A page importing samples from both `:core` and `:samples` builds, but don't:
  it works against the #898 migration and blocks iframes, since `DataFrameSampleHelper` lives only in
  `:samples`. Adding a sample to a `:core`-owned topic means moving that topic's samples over first.
- **A new iframe needs `:samples:updateShadowResources`** on top of the generated HTML — without the
  `<resource>` entry in `_shadow_resources.md` the page silently shows no table.
- **An iframe `src` is the flat basename, never the on-disk path.** `updateShadowResources` registers
  resources by basename only (422 `<resource src="…">` entries, not one with a slash), so a nested
  source file is still referenced as `./resources/<file>.html`: `sliceRows.md:14` says
  `./resources/sliceRowsDf.html` for a file at `resources/api/sliceRows/sliceRowsDf.html`. A nested
  `src` builds fine and silently renders an empty frame.
- After a generator run `git status` over-reports on Windows (`core.autocrlf=true`, no `.gitattributes`)
  — check the real set with `git diff --ignore-cr-at-eol`.

## Adding a new topic under an already-included folder

Several entries in `:samples`' korro `docs` include list (`samples/build.gradle.kts`) are globs over a whole
folder, so a new topic dropped into one of those folders is picked up with no build-file change; a topic at
any other path needs its own `include(...)` line — check that list before adding one. Still required for a
new page either way: a `<toc-element>` in `d.tree` (otherwise it is not published) and a bullet on the
group's overview page (for example `columnOperations.md`).

A `<title>` collision is fine: `first.md` (rows) and `operations/column/firstOnColumn.md` (column) both
render as "first". Follow that naming — `<operation>OnColumn.md`, published as `<operation>oncolumn.html`.
