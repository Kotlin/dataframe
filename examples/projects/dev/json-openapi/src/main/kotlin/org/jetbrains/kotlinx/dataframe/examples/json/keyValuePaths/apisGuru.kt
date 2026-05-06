package org.jetbrains.kotlinx.dataframe.examples.json.keyValuePaths

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toStdlibInstant
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.io.readOpenApi

/**
 * Here we will use the generated data schemas for our JSON file using the generated
 * [ApisGuru] data schema.
 *
 * We will need to read it in the same way as when generating the data schema, so using `keyValuePaths`.
 *
 * See also: [our documentation](https://kotlin.github.io/dataframe/read.html#read-from-json).
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

    // and find the 1Password Connect API
    val onePasswordConnect = newApis
        .first { "1password" in name && "connect" in name }
        .value

    // It has multiple versions but states its preferred version, so let's find that one
    val preferredVersion = onePasswordConnect
        .versions
        .first { name == onePasswordConnect.preferred }
        .value

    // now, let's gather and print the data we need to start using the 1Password Connect API.
    val info = preferredVersion.toDataFrame().select {
        cols(
            info.title,
            info.version,
            swaggerYamlUrl,
            openapiVer,
        )
    }
    info.print(valueLimit = Int.MAX_VALUE)
    //               title version                                                            swaggerYamlUrl openapiVer
    // 0 1Password Connect   1.3.0 https://api.apis.guru/v2/specs/1password.local/connect/1.3.0/openapi.yaml      3.0.2

    // We could now go full-circle and fetch the OpenAPI spec, to use it inside DataFrame!
    runCatching {
        readOpenApi(
            uri = info.swaggerYamlUrl.single(),
            name = "1Password",
            extensionProperties = false,
            generateHelperCompanionObject = false,
        )
    }
    // Unfortunately, our APIs Guru database seems to be a bit out of date,
    // so the URL does not seem live anymore.
    // We did cache it on our GitHub, so take a look at the openApi directory to continue
    // this example!
}
