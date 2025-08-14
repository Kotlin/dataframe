package org.jetbrains.kotlinx.dataframe.codeGen

import io.kotest.matchers.shouldBe
import org.junit.Test

class ValidFieldNameOfTest {

    data class Case(val input: String, val expected: String, val needsQuote: Boolean)

    @Test
    fun `ValidFieldName of covers diverse inputs`() {
        val cases = listOf(
            // simple valid identifiers (no quoting)
            Case("abc", "abc", false),
            Case("a_b9", "a_b9", false),
            Case("AbC123", "AbC123", false),
            Case("Привет", "Привет", false), // Cyrillic letters are letters
            Case("mañana", "mañana", false), // Latin letter with diacritic
            Case("Δelta", "Δelta", false), // Greek uppercase letter
            Case("__name", "__name", false),
            // needs quoting due to rules
            Case("_", "_", true), // all underscores
            Case("__", "__", true), // all underscores
            Case("1abc", "1abc", true), // starts with digit
            Case("", "", true), // empty
            Case("   ", "   ", true), // blank (spaces)
            Case("fun", "fun", true), // modifier keyword
            Case("class", "class", true), // hard keyword
            Case("!in", "!in", true), // hard keyword (also special char)
            Case("hello world", "hello world", true), // contains space (special char per regex)
            Case("a-b", "a-b", true), // '-' is quoted char, not replaced
            Case("a|b", "a|b", true), // '|' is quoted char, not replaced
            Case("a?b", "a?b", true),
            Case("a!b", "a!b", true),
            Case("a@b", "a@b", true),
            Case("a#b", "a#b", true),
            Case("a\$b", "a\$b", true),
            Case("a%b", "a%b", true),
            Case("a^b", "a^b", true),
            Case("a&b", "a&b", true),
            Case("a*b", "a*b", true),
            Case("a(b)c", "a(b)c", true), // parentheses are quoted, not replaced
            Case("{x}", "{x}", true), // braces are quoted, not replaced
            // quoting due to non-letter symbol categories
            Case("😀", "😀", true), // emoji
            Case("你好", "你好", true), // CJK: category OTHER_LETTER -> quote
            Case("क", "क", true), // Devanagari OTHER_LETTER -> quote
            Case("a\tb", "a\tb", true), // tab (CONTROL) -> quote, not replaced
            Case("a\u200Bb", "a\u200Bb", true), // zero-width space (FORMAT) -> quote
            // precise replacement tests when quoting is needed
            Case("<name>", "{name}", true), // < > -> { }
            Case("a::b", "a - b", true), // :: ->  -
            Case("a:b", "a - b", true), // : ->  -
            Case("a: b", "a - b", true), // :  ->  -
            Case("a.b", "a b", true), // . -> space
            Case("a/b", "a-b", true), // / -> -
            Case("a[b]", "a{b}", true), // [ ] -> { }
            Case("a`b", "a'b", true), // backtick -> apostrophe
            Case("a;b", "a b", true), // ; -> space
            Case("a\\b", "a b", true), // backslash -> space
            Case("a\nb", "a b", true), // newline -> space
            Case("a\rb", "a b", true), // carriage return -> space
            Case(
                "a.b/c[d]`e;f\\g\nh\ri",
                "a b-c{d}'e f g h i",
                true,
            ),
            Case(": leading colon", " - leading colon", true), // ": " -> " - "
            Case("a:bc", "a - bc", true), // ":" -> " - "
            Case("a: bc", "a - bc", true), // ": " -> " - "
            Case("<tag>", "{tag}", true),
            Case("name<generic>", "name{generic}", true),
            Case("x>y", "x}y", true),
            Case("a.b.c", "a b c", true),
            Case("a/b/c", "a-b-c", true),
            // extra heavy special-symbol cases
            Case("::", " - ", true),
            Case(":::", " -  - ", true),
            Case("...---", "   ---", true),
            Case("//\\", "-- ", true),
            Case("[[]]<>", "{{}}{}", true),
            Case("x`;;`y", "x'  'y", true),
            Case("a:::b", "a -  - b", true),
            Case("..a..", "  a  ", true),
            // already quoted stays as-is and does not need quoting
            Case("`already quoted`", "`already quoted`", false),
        )

        cases.forEach { (input, expected, needsQuote) ->
            val vf = ValidFieldName.of(input)
            vf.unquoted shouldBe expected
            vf.needsQuote shouldBe needsQuote
            val expectedQuotedIfNeeded = if (needsQuote) "`$expected`" else expected
            vf.quotedIfNeeded shouldBe expectedQuotedIfNeeded
        }
    }
}
