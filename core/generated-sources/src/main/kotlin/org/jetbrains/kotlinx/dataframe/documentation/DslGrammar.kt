package org.jetbrains.kotlinx.dataframe.documentation

/**
 * ## DSL Grammar
 *
 * If you've come across notations like **`a(`**` (`**`b`**` | [`**`c, .. `**`] ) `**`)`**
 * either in the KDocs or on the website and would like some further explanation
 * for what it means, you've come to the right place.
 *
 * The notation we use is _roughly_ based on [EBNF](https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form)
 * with some slight deviations to improve readability in the context of Kotlin.
 * The grammars are also almost always decorated with highlighted code snippets allowing you to click around and explore!
 *
 * ### Symbols
 * - '**`bold text`**' : literal Kotlin notation, e.g. '**`myFunction`**', '**`{ }`**', '**`[ ]`**', etc.
 * - '`normal text`' : Definitions or types existing either just in the grammar or in the library itself.
 * - '`:`' : Separates a definition from its type, e.g. '`name: `[String]'.
 * - '`|`', '`/`' : Separates multiple possibilities, often clarified with `()` brackets or spaces, e.g. '**`a`**` ( `**`b`**` | `**`c`**` )`'.
 * - '`[ ... ]`' : Indicates that the contents are optional, e.g. '`[ `**`a`**` ]`'. Careful to not confuse this with **bold** Kotlin brackets **`[]`**.
 *    - NOTE: sometimes **`function`**` [`**`{ }`**`]` notation is used to indicate that the function has an optional lambda. This function will still require **`()`** brackets to work without lambda.
 * - '**`,`**` ..`' : Indicates that the contents can be repeated with multiple arguments of the same type(s), e.g. '`[ `**`a,`**` .. ]`'.
 * - '`( ... )`' : Indicates grouping, e.g. '`( `**`a`**` | `**`b`**` )` **`c`**'.
 *
 * No other symbols of [EBNF](https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form) are used.
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Note that the grammar is not always 100% accurate to keep the readability acceptable.
 * Always use your common sense reading it and if you're unsure, try out the function yourself or check
 * the source code :).
 */
public typealias DslGrammar = Nothing

/** [(What is this notation?)][DslGrammar] */
internal typealias DslGrammarLink = Nothing
