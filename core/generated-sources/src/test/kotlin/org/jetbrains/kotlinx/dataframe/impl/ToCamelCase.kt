package org.jetbrains.kotlinx.dataframe.impl

import io.kotest.matchers.shouldBe
import org.junit.Test

class ToCamelCase {
    @Test
    fun defaultDelimitersSimpleUseCases() {
        val testCases = listOf(
            "hello_world",
            "HelloWorld",
            "json.parser.Config",
            "my.var_name test",
            "thirdColumn",
            "someHTMLParser",
            "RESTApi",
            "OAuth2Token",
            "GraphQLQuery",
            "TCP_3_PROTOCOL",
            "123hello_world456",
            "API_Response_2023",
            "UPPER_case-LOWER",
            "12parse34CamelCase",
            "snake_case_example",
            "dot.separated.words",
            "kebab-case-example",
            "MIXED_Case_with_123Numbers",
            "___!!!___",
            "1000.2000.3000",
            "UPPERCASE",
            "alreadyCamelCased",
            "justNumbers123",
            "Just_Special\$Chars!!",
            "singleword",
            "word_with_underscores_and-dashes",
            "10-20-aa",
            "ROOM_1.11",
        )
        val expected = listOf(
            "helloWorld",
            "helloWorld",
            "jsonParserConfig",
            "myVarNameTest",
            "thirdColumn",
            "someHtmlParser",
            "restApi",
            "oAuth2Token",
            "graphQlQuery",
            "tcp3Protocol",
            "123HelloWorld456",
            "apiResponse2023",
            "upperCaseLower",
            "12Parse34CamelCase",
            "snakeCaseExample",
            "dotSeparatedWords",
            "kebabCaseExample",
            "mixedCaseWith123Numbers",
            "___!!!___",
            "1000_2000_3000",
            "uppercase",
            "alreadyCamelCased",
            "justNumbers123",
            "justSpecialChars",
            "singleword",
            "wordWithUnderscoresAndDashes",
            "10_20Aa",
            "room1_11",
        )

        testCases.zip(expected).forEach { (input, expected) ->
            input.toCamelCaseByDelimiters() shouldBe expected
        }
    }

    @Test
    fun specialCharacters() {
        "música_lírica".toCamelCaseByDelimiters() shouldBe "músicaLírica"
        "тут был Андрей".toCamelCaseByDelimiters() shouldBe "тутБылАндрей"
        "汉字_拼音".toCamelCaseByDelimiters() shouldBe "汉字拼音"
        "X Æ A-12 34".toCamelCaseByDelimiters() shouldBe "xÆA12_34"
        "kæt_wɪð_æk!t".toCamelCaseByDelimiters() shouldBe "kætWɪðÆkT"
        "Gëëxplodeerd,_of_geïntegreerd?".toCamelCaseByDelimiters() shouldBe "gëëxplodeerdOfGeïntegreerd"
        "Äüßergewöhnlich_könnte_flüssig_sein,_aber_wie_öfter?".toCamelCaseByDelimiters() shouldBe
            "äüßergewöhnlichKönnteFlüssigSeinAberWieÖfter"
    }
}
