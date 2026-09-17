---
name: operation-website-documentation
description: Write or update a Kotlin DataFrame operation page (or pages) for the documentation website — topic in docs/StardustDocs/topics plus runnable Korro samples in the :samples module. Use when asked to document an operation, add/refresh a website page for an API, or migrate old :core samples of a page to :samples.
---

# Operation website documentation

Write or update the documentation-website page(s) for a Kotlin DataFrame operation, with runnable
code samples and generated outputs.

**[DOCUMENTATION_GUIDELINES.md](../../../DOCUMENTATION_GUIDELINES.md) is the specification for this
task. Read it in full first and follow it completely** — this skill only adds the operating procedure
around it. If the two ever disagree, the guidelines win.

## Hard rules

- **Edit by hand only:**
  - topics — `docs/StardustDocs/topics/**/*.md`
  - sample tests — `samples/src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/**`
  - plus the two registration files: `docs/StardustDocs/d.tree` (site map) and
    `samples/build.gradle.kts` (Korro config).
- **Everything else is generated** — inserted code blocks between `<!---FUN x-->` / `<!---END-->`,
  `docs/StardustDocs/resources/*.html`, `docs/StardustDocs/images/*.svg`,
  `docs/StardustDocs/topics/_shadow_resources.md`, `generated-sources/**`. Never hand-write or
  hand-patch them; run the Gradle tasks below instead.
- **Never write a sample snippet inline in the `.md`.** Snippets only come from tests via Korro.
- Never use the legacy "dataframe explainer" / `@TransformDataFrameExpressions` mechanism, and never
  add samples to `:core`.
- **No deprecated API.** If an existing sample or page section uses deprecated API, rewrite it with
  the current replacement — or delete it if the replacement makes it redundant.
- **Do not run or build the website yourself, do not deploy, and do not commit or push anything.**
  Leave all changes in the working tree and let the user review them. Only run the Gradle tasks
  listed in step 5 and the ktlint/validation commands.

## Procedure

### 1. Get a template

Operation pages are never written from scratch. Use a completed similar operation as a template:

- If the user provided a reference page — use it.
- Otherwise find one yourself: pick a page of a closely related operation that already has an
  `IMPORT` directive, `FUN`/`END` marks and `<inline-frame>` outputs, and whose test lives in
  `samples/src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/`. Good references:
  `docs/StardustDocs/topics/valueCounts.md` + `…/samples/api/ValueCountsSamples.kt`,
  `countDistinct.md`, `sliceRows.md`, `filter.md`. Tell the user which template you picked.

Read both the template topic and its template test before writing anything, and mirror their
structure, tone and formatting.

### 2. Gather the source of truth

- Read the operation's implementation and KDoc in `core/src/main/kotlin/.../api/<operation>.kt`.
  For the **fully expanded** KDoc, open the single corresponding file under `core/generated-sources/`
  (open deliberately — don't grep/crawl generated trees). Reuse that KDoc text for the page where it
  is good; the page must contain **at least** as much information as the KDoc, never less.
- Note which overloads/parameters are `@Deprecated` — those must not be documented or used; if an
  existing page or sample relies on them, plan the rewrite to the replacement API (or removal).
- Enumerate every non-deprecated overload, every parameter (including enum parameter values), and the notable
  edge cases (empty frame, `null`s, `NaN`/`NA`, nested/column groups) — the guidelines require all of
  them to be documented.
- Check whether the page already exists, and whether it still pulls samples from `:core`
  (`core/src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/*.kt`, which is what
  `core/build.gradle.kts` korro config covers). If so, migration is part of the job — see step 7.

### 3. Write the topic

Create/update `docs/StardustDocs/topics/<operation>.md` and register it in
`docs/StardustDocs/d.tree` if new (place it next to related operations).

Follow the structure from the guidelines: title → Writerside summaries → what it does, then what it
returns → signature(s) / operation grammar → important notes → *See also* links → `### Parameters` →
`### Examples` → ``### `funName` on `DataColumn` `` (if such an overload exists). Multi-step
operations get one subsection per step, each with the same structure.

If the page has no Writerside summaries yet, add all three right after the title (and the Korro
`IMPORT`), using the standard operation template with the same text in each — see
*Writerside summaries* in the guidelines. Keep existing meaningful custom summaries as they are.

Header and signature rules (see *Structure* in the guidelines for the details):

- Parameters and examples are **headers** (`### Parameters`, `### Examples`), never bold paragraphs.
- A separate [`DataColumn`](docs/StardustDocs/topics/concepts/DataColumn.md) overload goes at the end
  of the operation section under ``### `funName` on `DataColumn` ``, with its own description,
  signature, parameters, and examples.
- Signatures are pseudocode **with types**, in a `kotlin` code block, e.g.
  `sortWith(comparator: Comparator<DataRow>)` / `sortWith { row1: DataRow, row2: DataRow -> Int }`.
- Types go in the **parameter descriptions too**, not only in the signature — as pseudocode (same
  style as the signature) or in words; for lambdas say what they receive and what they must return.
- For columns arguments, state in the description how columns can be selected: by string names
  and/or with the [Columns Selection DSL](docs/StardustDocs/topics/ColumnSelectors.md) (link it).
  Check the actual overloads: some operations are DSL-only; in some the string and DSL overloads
  work differently (e.g. `rename`) and must be described separately; some use a specialized DSL
  (`SortDsl`, `PivotDsl`, …) — name it, link its section, and say what it adds on top of the
  Columns Selection DSL.
- If the page describes several operations: each gets its own `##` section; don't add a list of the
  page's operations anywhere; and write *See also* only once — in the first operation section, in its
  usual place (after the description/notes, before `### Parameters`) — linking **only** to operations
  not described on this page.
- The input dataframe goes inside `### Examples`, immediately before the first example of the page
  ("The following dataframe will be used in the examples below:"), not earlier on the page.

Link on first mention (`[`DataFrame`](DataFrame.md)`, `[column selectors](ColumnSelectors.md)`, …),
follow `docs/StardustDocs/topics/concepts/spellingConventions.md`, and keep the language plain.

### 4. Write the samples

One test file per documentation page, at
`samples/src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/<Operation>Samples.kt`,
extending `DataFrameSampleHelper("<pageName>", "api")`, keeping the order of tests aligned with the
order of sections in the topic.

- Mark snippets with `// SampleStart` / `// SampleEnd`.
- Show the input dataframe first, in its own sample.
- Show both column-access APIs where applicable by naming paired tests `x_properties` /
  `x_strings` — Korro turns them into tabs automatically. Prefer the Extension Property API
  (compiler plugin) elsewhere.
- Save outputs with Sample Helper **after** `// SampleEnd`: `.saveDfHtmlSample()` for
  `DataFrame`/`DataColumn`/`GroupBy` (use `.toDataFrame()`/`.values()`/`.frames()` if needed),
  `.saveSample()` for `String`/`CodeString`.
- Put `.format` / `.formatHeader` / `.defaultHeaderFormatting { }` highlighting after
  `// SampleEnd` too. Highlight rows for row-oriented operations, headers for column-oriented ones.
- Use illustrative data — e.g. a frame that actually contains `null`s for `null`-related
  operations. The shared dataset from `TestBase` (`peopleDf` etc.) is the default, but if it does
  not demonstrate the operation well, declare your own `@DataSchema` + `dataFrameOf(...)` in the
  test class instead (see `ValueCountsSamples.kt`). Keep it small and meaningful.
- **Never use deprecated API** in samples or in the page text — no deprecated operations,
  overloads, or parameters, and nothing scheduled for removal. Document the current replacement
  instead. Check for `@Deprecated` on everything you call.
- Tests must pass and must not be `@Ignore`d (except complex integration tests, for example with reading from DBs), and must compile without deprecation warnings.

Wire it up in the topic: `<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.<Operation>Samples-->`
at the top, then for each sample

```markdown
<!---FUN testName-->

<!---END-->
<inline-frame src="./resources/testName.html" width="100%" height="500px"></inline-frame>
```

Then add both the topic path and the test path to the Korro config in `samples/build.gradle.kts`
(`korro { docs { from(...) }; samples { from(...) } }`) — a glob that already covers the path is
enough.

When the test file is written, run ktlint over it and fix what it reports:

```bash
./gradlew :samples:ktlintFormat
./gradlew :samples:ktlintCheck
```

Note the deliberate `chain-method-continuation = disabled` style from `AGENTS.md`: group chained
operations on one line so the chain reads like a sentence.

### 5. Generate and verify

Run, in this order (`korro` **must** run after `test`):

```bash
./gradlew :samples:clean :samples:build :samples:test
./gradlew :samples:korro
./gradlew :samples:updateShadowResources
```

Then verify, and report honestly if anything failed:

- the test run passed;
- code snippets were inserted between every `FUN`/`END` pair in the topic (no empty pairs);
- an HTML file exists in `docs/StardustDocs/resources/` for every `<inline-frame src>` you
  reference (note tab-generated names get the `_properties` / `_strings` suffix);
- `_shadow_resources.md` was updated.

Do **not** build, run, or deploy the website. Just point the user to
`docs/README.md#running-the-documentation-website-locally` so they can review the rendered page.

### 6. Validate links and anchors

Once the topic text is final, check every link you wrote or touched:

- Markdown links to other topics (`[…](someTopic.md)`, `[…](concepts/x.md)`) — the target file must
  exist under `docs/StardustDocs/topics/` (and subfolders), with the path relative to the current topic.
- Anchors (`someTopic.md#section-anchor`, `#local-anchor`) — the heading must exist in the target
  file; Writerside anchors are the lowercased heading with spaces replaced by `-` and punctuation
  dropped. Also accept explicit `{id="..."}` anchors.
- `<inline-frame src="./resources/x.html">` — the file must exist in `docs/StardustDocs/resources/` 
  (and subfolders).
- `<img src="...">` — the file must exist in `docs/StardustDocs/images/`.
- `topic="x.md"` entries you added to `d.tree` must point at existing files, and the new topic must
  be reachable from the tree.
- Links in the surrounding prose that you rewrote while editing (e.g. renamed sections) still
  resolve.

Fix every broken one, and report any you could not resolve.

### 7. Migrating old `:core` samples of the page

If the page still uses samples generated from `:core` tests:

- Remove the old Korro marks/`IMPORT` from the topic and any old-style output embeds.
- Rewrite those samples as new tests in `:samples` (step 4) with Sample Helper outputs — do not
  carry over `@TransformDataFrameExpressions` or the explainer HTML.
- Delete the now-unused sample functions from the `:core` test file if nothing else references
  them; leave `:core` korro config alone.
- Keep the page's coverage at least as complete as before the migration.

## Reporting

Summarize: which topic file(s) and test file(s) you changed, the template you followed, which Gradle
tasks you ran and their result, the link/anchor validation result, and anything you deliberately
left out (e.g. an overload you could not sensibly exemplify) so the user can decide.

Leave the changes uncommitted — the user commits and deploys.
