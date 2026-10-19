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
  `docs/StardustDocs/resources/**/*.html`, `docs/StardustDocs/images/*.svg`,
  `docs/StardustDocs/topics/_shadow_resources.md`, `generated-sources/**`. Never hand-write or
  hand-patch them; run the Gradle tasks below instead. The only manual step allowed is deleting
  the page's own output folder before regenerating it (step 5).
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

Operation pages are never written from scratch. Use a reference page as a template:

- If the user provided a reference page — use it.
- Otherwise use one of the two reference pages, which fully follow the guidelines:
  - `docs/StardustDocs/topics/filter.md` + `samples/…/samples/api/FilterSamples.kt` — a single
    operation with a `DataColumn` overload;
  - `docs/StardustDocs/topics/concat.md` + `samples/…/samples/api/ConcatSamples.kt` — an operation
    with several receivers: the `DataFrame` one first, without a header, then `DataRow`, `DataColumn`,
    `GroupBy`, `Iterable`, each in its own `##` section.

  Tell the user which template you picked.

Read both the template topic and its template test before writing anything, and mirror their
structure, tone and formatting. Any other existing page may be outdated: you can look at it for
the mechanics (`IMPORT`/`FUN`/`END`, the `:samples` test class, Sample Helper calls), but always take
the structure (summaries, headers, signatures, parameters) from the guidelines and the two reference pages.

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
- A separate [`DataColumn`](../../../docs/StardustDocs/topics/concepts/DataColumn.md) overload goes at the end
  of the operation section under ``### `funName` on `DataColumn` ``, with its own description,
  signature, parameters, and examples.
- Signatures are pseudocode **with types**, in a `text` code block, e.g.
  `df.sortWith(comparator: Comparator<DataRow>)` / `df.sortWith { row1: DataRow, row2: DataRow -> Int }`.
- **Always write the receiver**, named after its type: `DataFrame` → `df`, `DataColumn` → `col`,
  `DataRow` → `row`, `ColumnGroup` → `colGroup`, `FrameColumn` → `frameCol`, `GroupBy` → `groupBy`,
  `Pivot` → `pivot`. Name arguments and lambda parameters of these types with the same prefixes
  (`otherDf: DataFrame`, `vararg dfs: DataFrame`, `rows: Iterable<DataRow>`, `vararg cols: DataColumn`).
- **Omit type parameters** (`<T>`, `<C>`, …) in signatures and descriptions unless they carry meaning:
  they take part in the function logic (e.g. a `reified` type parameter like in `colsOf<T>()`), or they
  connect the input and result types (e.g. `collectionColumn.concat(): List<T>` for a
  `DataColumn<Collection<T>>`). Otherwise write `Iterable<DataRow>`, not `Iterable<DataRow<T>>`.
- If a receiver or an argument needs a more specific type than its name tells, declare it on a separate
  line of the signature block after the calls (`collectionColumn: DataColumn<Collection<T>>`), not in a
  comment. Such a receiver may get a descriptive name (`collectionColumn`) instead of the plain prefix.
- Types go in the **parameter descriptions too**, not only in the signature — as pseudocode (same
  style as the signature) or in words; for lambdas say what they receive and what they must return.
- For columns arguments, state in the description how columns can be selected: by string names
  and/or with the [Columns Selection DSL](../../../docs/StardustDocs/topics/ColumnSelectors.md) (link it).
  Check the actual overloads: some operations are DSL-only; in some the string and DSL overloads
  work differently (e.g. `rename`) and must be described separately; some use a specialized DSL
  (`SortDsl`, `PivotDsl`, …) — name it, link its section, and say what it adds on top of the
  Columns Selection DSL.
- If the operation is defined for several receivers (`DataFrame`, `DataRow`, `DataColumn`, `GroupBy`,
  `Iterable`, …): describe the `DataFrame` operation first, **without a header**, right after the
  summaries; every other receiver gets its own ``## `funName` on `Receiver` `` section, the `DataColumn`
  one included (see `concat.md`).
- If the page describes several operations: each gets its own `##` section; don't add a list of the
  page's operations anywhere; and write *See also* only once — in the first operation section, in its
  usual place (after the description/notes, before `### Parameters`) — linking **only** to operations
  not described on this page.
- The input dataframe goes inside `### Examples`, immediately before the first example of the page
  ("The following dataframe will be used in the examples below:"), not earlier on the page.

Link on first mention (`[`DataFrame`](DataFrame.md)`, `[column selectors](ColumnSelectors.md)`, …),
follow `docs/StardustDocs/topics/concepts/spellingConventions.md`, and keep the language plain.

Links that are easy to forget — check them explicitly:

- The operation takes a **row expression** or **row condition** (`RowExpression`, `RowValueExpression`,
  `RowFilter`, `RowValueFilter` — a lambda with `DataRow` as the receiver)? Link it:
  `[row expression](DataRow.md#row-expressions)` / `[row condition](DataRow.md#row-conditions)`.
  Also add a note on what the lambda receives and how the row values are accessed, like in `filter.md`:

  ```markdown
  The row condition receives each row both as `this` and as `it`, so the values of the row can be accessed directly
  using [Extension Properties](extensionPropertiesApi.md) or
  [`String` column accessors](StringApi.md#string-column-accessors),
  including the values of nested columns of a [`ColumnGroup`](DataColumn.md#columngroup).
  ```

  Adapt it to the lambda type:
  `RowValueExpression` / `RowValueFilter` get the row as `this` and the current value as `it`;
  `RowColumnExpression` gets `row` and `col` as parameters.
- Mentioning `ColumnSet` or other column resolvers? Link
  `[`ColumnSet`](ColumnSelectors.md#column-resolvers)`.

Describe only the overloads the method actually has.

### 4. Write the samples

One test file per documentation page, at
`samples/src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/<Operation>Samples.kt`,
extending `DataFrameSampleHelper("<pageName>", "api")`, keeping the order of tests aligned with the
order of sections in the topic.

- Mark snippets with `// SampleStart` / `// SampleEnd`.
- Show the input dataframe first, in its own sample.
- **If the operation selects columns or uses row values, the basic usage example always has two
  versions** — the
  [Extension Properties API](../../../docs/StardustDocs/topics/extensionPropertiesApi.md) and the
  [String API](../../../docs/StardustDocs/topics/concepts/StringApi.md) (read both topics if you are
  unsure what each looks like) — written as paired tests `x_properties` / `x_strings`.
  **Korro wraps such a pair into `<tabs>`/`<tab>` itself** (`groupSamples` in
  `samples/build.gradle.kts` maps the `_properties`/`_strings`/`_kotlin`/`_java`
  suffixes onto tab titles), so never write the tab markup by hand in the topic — just put a single
  `<!---FUN x-->` / `<!---END-->` pair with the **base** name, without the suffix.
  If the operation cannot take plain column names (`String`s), write the `_strings` version with
  [`String` column accessors](../../../docs/StardustDocs/topics/concepts/StringApi.md#string-column-accessors)
  instead of dropping it: `col<Int>("age")` / `colGroup("name")` / `frameCol(...)` in the Columns
  Selection DSL, `getValue<Int>("age")` / `getColumnGroup("name")` in row expressions. Do not use
  the "Invoked String API" (`"age"<Int>()`, `"info"["age"]`) — `StringApi.md` marks it as outdated
  and not recommended. Use the Extension Properties API in the remaining examples.
- Save outputs with Sample Helper **after** `// SampleEnd`: `.saveDfHtmlSample()` for
  `DataFrame`/`DataColumn`/`GroupBy` (use `.toDataFrame()`/`.values()`/`.frames()` if needed),
  `.savePlotSVGSample()` for Kandy plots, `.saveSample()` / `.saveTextSample()` for
  `String`/`CodeString`. Korro inserts all of them into the topic itself (see below).
- Put `.format` / `.formatHeader` / `.defaultHeaderFormatting { }` highlighting after
  `// SampleEnd` too. Highlight rows for row-oriented operations, headers for column-oriented ones.
- Use illustrative data — e.g. a frame that actually contains `null`s for `null`-related
  operations. The shared dataset from `TestBase` (`peopleDf` etc.) is the default, but if it does
  not demonstrate the operation well, declare your own `@DataSchema` + `dataFrameOf(...)` in the
  test class instead (see `ConcatSamples.kt`). Keep it small and meaningful.
- **Never use deprecated API** in samples or in the page text — no deprecated operations,
  overloads, or parameters, and nothing scheduled for removal. Document the current replacement
  instead. Check for `@Deprecated` on everything you call.
- Tests must pass and must not be `@Ignore`d (except complex integration tests, for example with reading from DBs), and must compile without deprecation warnings.

Wire it up in the topic: `<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.<Operation>Samples-->`
at the top, then for each sample

```markdown
<!---FUN testName-->

<!---END-->
```

Korro fills the block with the sample code **and its outputs**: everything the test saved with
Sample Helper — the `<inline-frame>` for `.saveDfHtmlSample()`, the `<img>` for
`.savePlotSVGSample()`, the text of `.saveSample()` / `.saveTextSample()` — in the order it was
saved. **Never write an `<inline-frame>` / `<img>` for a sample by hand** — it would duplicate the
generated one. The only hand-written `<inline-frame>` is for an output shown without its code (e.g. a
highlighted copy of the input): a test that only saves an output, with no `// SampleStart` and no
`FUN` mark in the topic.

The generated `src` is the **flat** file name, `./resources/<testName>.html` — even though
`DataFrameSampleHelper("<pageName>", "api")` writes the file to
`docs/StardustDocs/resources/api/<pageName>/<testName>.html`. `_shadow_resources.md` registers
resources by file name only, so a nested `src` builds fine but renders an empty frame. Never
"fix" a `src` to the on-disk path.

For a tabbed pair, the `FUN` directive takes the **base** name without the suffix. Sample Helper
cuts the `_properties` / `_strings` suffix off the output file names too, so the output of
`valueCountsSelector_properties` is `./resources/valueCountsSelector.html`. Save the output in one
variant only (usually `_properties`) — both variants write the same file:

```markdown
<!---FUN valueCountsSelector-->

<!---END-->
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
If the line is too long, make sure the operation still "connects": start a new line only when a new
operation starts, and break inside the lambdas of the operation instead:

```kotlin
df
    .update {
        colsAtAnyDepth().colsOf<Int>()
    }.with {
        sqrt(it * 1000) * it.pow(2.0)
    }
    .rename { a and b }.to("columnA", "columnB")
    .print()
```

### 5. Generate and verify

First delete the page's HTML output folder, so that outputs of renamed or deleted samples don't
survive (the tests recreate it):

```bash
rm -rf docs/StardustDocs/resources/api/<pageName>
```

Then run, in this order (`korro` **must** run after `test`):

```bash
./gradlew :samples:clean :samples:build :samples:test
./gradlew :samples:korro
./gradlew :samples:updateShadowResources
```

**Never skip `clean`, and always run it before `build`/`test`/`korro`.** Without it, outdated korro
output lines (text samples in `samples/build/`, including those of samples you renamed or deleted)
survive and get injected into the documentation, so the page silently shows wrong content.
`clean` does **not** touch the HTML outputs — they live in the tracked `docs/StardustDocs/resources/`,
which is why the page folder is deleted by hand above; `updateShadowResources` would otherwise
re-register every stale file still on disk.

Then verify, and report honestly if anything failed:

- the test run passed;
- code snippets were inserted between every `FUN`/`END` pair in the topic (no empty pairs), and
  every sample that saves an output got its `<inline-frame>` / `<img>` **inside** its block;
- no `<inline-frame>` duplicates a generated one right after `<!---END-->` (remove such leftovers
  when updating an old page);
- an HTML file exists in `docs/StardustDocs/resources/` **or one of its subfolders** (normally
  `resources/api/<pageName>/`) for every `<inline-frame src>` on the page — the `src` itself stays
  flat, and output names have no `_properties` / `_strings` suffix;
- no stale HTML of renamed or deleted samples is left in the page's output folder;
- `_shadow_resources.md` was updated.

Do **not** build, run, or deploy the website. Just point the user to
`docs/README.md#running-the-documentation-website-locally` so they can review the rendered page.

### 6. Validate links and anchors

Once the topic text is final, check every link you wrote or touched:

- Markdown links to other topics (`[…](someTopic.md)`) — the target file must exist somewhere under
  `docs/StardustDocs/topics/` (in any subfolder). Writerside resolves topics by file name, so links are
  written as the **bare file name** (`DataRow.md`, not `concepts/DataRow.md`) — never "fix" a bare link
  into a relative path; rewrite a path-style link (`concepts/x.md`) into the bare form instead.
- Anchors (`someTopic.md#section-anchor`, `#local-anchor`) — the heading must exist in the target
  file; Writerside anchors are the lowercased heading with spaces replaced by `-` and punctuation
  dropped. Also accept explicit `{id="..."}` anchors.
- `<inline-frame src="./resources/x.html">` — the file `x.html` must exist in
  `docs/StardustDocs/resources/` or one of its subfolders; the `src` itself must stay flat.
- `<img src="...">` — the file must exist in `docs/StardustDocs/images/`.
- **KDoc → website links** in
  `core/src/main/kotlin/org/jetbrains/kotlinx/dataframe/documentation/DocumentationUrls.kt`
  (`{@include [Url]}/<page>.html#<anchor>`, included into KDocs as
  `For more information: {@include [DocumentationUrls.X]}`). Nothing validates them, so for every
  topic or section you added, renamed, moved, or split, check the entries pointing at it and fix the
  page name (topic file name lowercased + `.html`) and the anchor; add an entry for a new topic if
  its KDoc refers to the website. Editing only the KDoc URL needs no `apiDump`, and
  `generated-sources` is regenerated by CI — don't hand-edit it.
- `topic="x.md"` entries you added to `d.tree` must point at existing files, and the new topic must
  be reachable from the tree.
- Links in the surrounding prose that you rewrote while editing (e.g. renamed sections) still
  resolve.

Fix every broken one, and report any you could not resolve.

### 7. Migrating old `:core` samples of the page

If the page still uses samples generated from `:core` tests:

- Remove the old Korro marks/`IMPORT` from the topic and any old-style output embeds, including
  hand-written `<inline-frame>`s after `<!---END-->` — the new ones are generated inside the blocks.
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
