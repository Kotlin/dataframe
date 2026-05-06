package org.jetbrains.kotlinx.dataframe.examples.json.keyValuePaths

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.convertToUrl
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toStdlibInstant
import org.jetbrains.kotlinx.dataframe.api.toUrl
import org.jetbrains.kotlinx.dataframe.io.readJson

/**
 * Here we will use the generated data schemas for our JSON file using the generated
 * [ApisGuru] data schema.
 *
 * We will need to read it in the same way as when generating the data schema, so using `keyValuePaths`.
 */
fun main() {
    val apiGuruList = object {}::class.java.getResource("/apis_guru_list.json")!!

    val rawDf = DataFrame.readJson(
        url = apiGuruList,
        keyValuePaths = listOf(
            JsonPath(),
            JsonPath().appendWildcard().append("versions"),
        ),
    ).cast<ApisGuru>() // now we can cast to ApisGuru and get started!

    val df = rawDf.value.single()
    df.print()
    //                                        name                                    value
    //  0                               1forge.com { added:2017-05-30T08:34:14.000Z, pre...
    //  1                     1password.com:events { added:2021-07-19T10:17:09.188Z, pre...
    //  2                  1password.local:connect { added:2021-04-16T15:56:45.939Z, pre...
    //  3            6-dot-authentiqio.appspot.com { added:2017-03-15T14:45:58.000Z, pre...
    //  4                         ably.io:platform { added:2019-07-13T11:28:07.000Z, pre...
    //  5                         ably.net:control { added:2021-07-26T09:45:31.536Z, pre...
    //  6              abstractapi.com:geolocation { added:2021-04-14T17:12:40.648Z, pre...
    //  7                             adafruit.com { added:2018-02-10T10:41:43.000Z, pre...
    // ...

    // let's get relatively newly added APIs
    val startOf2021 = LocalDateTime(2021, 1, 1, 0, 0).toInstant(TimeZone.UTC)
    val newApis = df.convert { value.added }.toStdlibInstant()
        .filter { value.added >= startOf2021 }

    // and find the youtube-analytics API
    val youtubeAnalytics = newApis
        .first { "youtubeAnalytics" in name }
        .value

    // It has multiple versions but states its preferred version, so let's find that one
    val preferredVersion = youtubeAnalytics
        .versions
        .first { name == youtubeAnalytics.preferred }
        .value

    // now, let's gather and print the data we need to start using the YouTube Analytics API.
    val info = preferredVersion.toDataFrame().select {
        cols(
            info.title,
            info.version,
            swaggerYamlUrl,
            openapiVer,
            // we need to point to this column manually; not every nested dataframe has it,
            // so we won't have column accessors for it
            "externalDocs"["url"]<String>(),
        )
    }

    info.print(valueLimit = Int.MAX_VALUE)
    //                   title version                                                                 swaggerYamlUrl openapiVer                                             url
    // 0 YouTube Analytics API      v1 https://api.apis.guru/v2/specs/googleapis.com/youtubeAnalytics/v1/openapi.yaml      3.0.0 https://developers.google.com/youtube/analytics

    // ERROR:
    // val DataRow<X-origin_731>.url: String:
    //  Unresolved reference. None of the following candidates is applicable because of a receiver type mismatch:
    // val DataRow<X-origin_731>.url: String
    info.url

//    val fullOpenApiSpec = info.swaggerYamlUrl.convertToUrl().single().readText()
//    println(fullOpenApiSpec)
}
