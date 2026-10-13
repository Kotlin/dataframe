# AGENTS.md — samples

Guidance for this module. See the root `AGENTS.md` for repo-wide build/style/KoDEx rules; only module-specific
details are here.

**Writing or updating documentation?** The rules live in [`DOCUMENTATION_GUIDELINES.md`](../DOCUMENTATION_GUIDELINES.md)
(website topics + samples) and [`KDOC_GUIDELINES.md`](../KDOC_GUIDELINES.md) (KDocs) — read the relevant one first.
For an operation page (topic + samples in this module), use the
[`operation-website-documentation`](../.agents/skills/operation-website-documentation/SKILL.md) skill,
which walks through that workflow step by step. If anything here disagrees with the guidelines, the guidelines win.

## What this module is

`:samples` holds the **code samples** (plus DataFrame iframes and Kandy plot images) for the documentation website.
Documentation snippets here are runnable tests: running them produces output that Korro injects into the WriterSide
docs under `docs/StardustDocs/topics/`.

**New documentation samples belong here**, not in `:core`. (Sample migration into this module is in progress —
issue #898; `:core` still has a legacy `samples` source set + `samplesTest`/`korro` tasks that will be removed.)
Never use the legacy "dataframe explainer" / `@TransformDataFrameExpressions` mechanism; when migrating a page,
rewrite its `:core` samples here with Sample Helper outputs.

**Moving a page's samples here means deleting the `:core` originals in the same PR** (usually from
`core/src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/Access.kt`). Nothing reports a `:core` sample
that no page imports any more, so the orphan stays, and the next person who edits it sees no change on the
page. Before the PR, check that every function the page imported from `:core` is either deleted or still used by
another page.

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
  sample code + output between the `<!---FUN x-->` / `<!---END-->` marks of the topic markdown. Korro's
  `docs`/`samples` includes in `build.gradle.kts` define exactly which `docs/StardustDocs/topics/**` files and sample
  packages this module owns. Never hand-write or hand-patch the inserted snippets.
- **A text `Output:` block is either generated or stale.** `String.saveSample()` writes one (with the
  `Output:` header), `CodeString.saveSample()` wraps the value in a ` ```kotlin ` fence and
  `String.saveTextSample()` in a ` ```text ` one; korro then injects the block **inside** the
  `<!---FUN-->`…`<!---END-->` region. A block typed by hand sits *outside* `<!---END-->`, and nothing checks it
  ever again. At the time of writing the topics carry 8 generated blocks against 10 hand-written ones, so the
  hand-written shape is common but is the one that rots — prefer `saveSample`.
- **`groupSamples` tab convention:** function-name suffixes map to doc tabs — `_properties` → "Properties",
  `_strings` → "Strings", `_kotlin` → "Kotlin", `_java` → "Java". Korro wraps the variants into `<tabs>`/`<tab>`
  itself: in the topic write a single `<!---FUN x-->` with the **base** name (no suffix) and no tab markup. The
  generated HTML outputs keep the full suffixed name (`./resources/x_properties.html`).
- **Iframes / plots:** `DataFrameSampleHelper` (`SampleHelper`) saves DataFrames as HTML iframes into
  `docs/StardustDocs/resources/<subFolder>/<pageName>/` and Kandy plots as SVGs into `docs/StardustDocs/images/`;
  the `updateShadowResources` task regenerates `docs/StardustDocs/topics/_shadow_resources.md` (a `<resource>` tag
  per generated file, keyed by file name only). So an `<inline-frame>` `src` is always **flat** —
  `./resources/<testName>.html`, never the on-disk path; a nested `src` builds fine but renders an empty frame.
  These are all generated — don't edit them by hand. If a sample output changes, confirm the change is intentional.

## Build / run

First delete the page's HTML output folder (`rm -rf docs/StardustDocs/resources/<subFolder>/<pageName>`; the tests
recreate it), then run in this order (`korro` **must** run after `test`):

```bash
./gradlew :samples:clean :samples:build :samples:test
./gradlew :samples:korro
./gradlew :samples:updateShadowResources
```

- **Never skip `clean`** before `build`/`test`/`korro`: otherwise stale korro output lines in `build/` (incl. those of
  renamed or deleted samples) survive and get injected into the docs.
- `clean` does **not** remove HTML outputs — they are written straight into the tracked
  `docs/StardustDocs/resources/`. Without deleting the page folder first, HTML of renamed or deleted samples stays
  on disk and `updateShadowResources` registers it again.
- The test task already sets `--add-opens java.base/java.nio=ALL-UNNAMED` (for Arrow).
- After editing a sample file, run `./gradlew :samples:ktlintFormat :samples:ktlintCheck`.

## Writing a sample

- **One test file per documentation page**, e.g. `api/<Operation>Samples.kt` extending
  `DataFrameSampleHelper("<pageName>", "api")`; keep the test order aligned with the topic's sections. Put it in the
  subpackage matching its topic, and make sure both the test and the topic file are covered by the `korro.docs` /
  `korro.samples` includes.
- Mark snippets with `// SampleStart` / `// SampleEnd`; show the input dataframe first, in its own sample.
- If the operation selects columns or uses row values, the basic example is a `x_properties` / `x_strings` pair
  (Extension Properties API / String API). If the operation can't take plain `String` names, write `_strings` with
  `String` column accessors (`col<Int>("age")`, `getValue<Int>("age")`, …) — never the outdated "Invoked String API"
  (`"age"<Int>()`). Use the Extension Properties API in the remaining examples.
- **After** `// SampleEnd`: save outputs (`.saveDfHtmlSample()` for `DataFrame`/`DataColumn`/`GroupBy`, using
  `.toDataFrame()`/`.values()`/`.frames()` if needed; `.saveSample()` / `.saveTextSample()` for `String`/`CodeString`
  — see above) and add highlighting (`.format` / `.formatHeader` / `.defaultHeaderFormatting { }` — rows for
  row-oriented operations, headers for column-oriented ones). Reference each HTML output in the topic as an
  `<inline-frame>` right after its `<!---END-->`.
- Use small, illustrative data (e.g. a frame with `null`s for `null`-related operations). `TestBase`'s `peopleDf`
  etc. is the default; otherwise declare a local `@DataSchema` + `dataFrameOf(...)` (see `ValueCountsSamples.kt`).
- **No deprecated API** — no deprecated operations, overloads or parameters; samples must compile without
  deprecation warnings.
- Write each sample as a runnable test with optional assertions; it must pass and must not be `@Ignore`d (except
  complex integration tests, e.g. reading from DBs).
