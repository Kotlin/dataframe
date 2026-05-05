package org.jetbrains.kotlinx.dataframe.examples.openapi.onePassword

import kotlinx.datetime.LocalDateTime
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.explode
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.pivot
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.with

/**
 * Let's try to read a response from the [1Password API](https://developer.1password.com/docs/connect/api-reference/#item-object).
 *
 * We will use the [OnePassword] interface we generated in `onePasswordDataSchema.kt` to parse the JSON response.
 */
fun main() {
    readVaults()
    readFullItems()
}

fun readVaults() {
    // To list all vaults, according to the API definition, we can call:
    // GET /v1/vaults
    // In DataFrame this looks like:
    runCatching {
        // (this will fail now because we don't have a valid URL)
        OnePassword.Vault.readJson("URL_TO_1PASSS/v1/vaults")
    }

    // Let's use a sample response to see it in action:
    val sampleResponseUrl = object {}::class.java.getResource("/1passVaultsResponseSample.json")!!

    val df = OnePassword.Vault.readJson(sampleResponseUrl)
    df.print()
    //                           id                name attributeVersion contentVersion items         type           createdAt           updatedAt description
    // 0 ytrfte14kw1uex5txaore1emkz                Demo                1             72     7 USER_CREATED 2021-04-10T17:34:26 2021-04-13T14:33:50        null
    // 1 ftz4pm2xxwmwrsd7rjqn7grzfz                Work                2            134    23 USER_CREATED    2022-01-05T09:12 2023-11-18T16:45:22        null
    // 2 h8nkqv3ppzytcwe6slma5rbxdj Shared Team Secrets                1             58    12     EVERYONE    2022-03-20T11:00 2024-02-14T08:30:05        null

    df.schema().print()
    // id: String?
    // name: String?
    // description: String?
    // attributeVersion: Int?
    // contentVersion: Int?
    // items: Int?
    // type: OnePassword.Type?
    // createdAt: LocalDateTime?

    // Now you can see all columns can be accessed safely using the compiler plugin!
    df.dropNulls { type..updatedAt }
        .filter { type == OnePassword.Type.USER_CREATED }
        .filter {
            val startOf2022 = LocalDateTime(2022, 1, 1, 0, 0)
            createdAt > startOf2022 || updatedAt > startOf2022
        }.print()
    //                           id name attributeVersion contentVersion items         type        createdAt           updatedAt description
    // 0 ftz4pm2xxwmwrsd7rjqn7grzfz Work                2            134    23 USER_CREATED 2022-01-05T09:12 2023-11-18T16:45:22        null
}

fun readFullItems() {
    // To list all items in our vault with a known UUID, according to the API definition, we can call:
    // GET /v1/vaults/{vaultUUID}/items
    // In DataFrame this looks like:
    runCatching {
        // (this will fail now because we don't have a valid URL and vault UUID)
        OnePassword.FullItem.readJson("URL_TO_1PASSS/v1/vaults/{vaultUUID}/items")
    }

    // Let's use a sample response to see it in action:
    val sampleResponseUrl = object {}::class.java.getResource("/1passItemsResponseSample.json")!!

    val df = OnePassword.FullItem.readJson(sampleResponseUrl)
    df.print()
    //                           id                   title            tags                             vault        category                                 sections  fields                                    files                     createdAt                     updatedAt favorite lastEditedBy state    urls version
    // 0 2fcbqwe9ndg175zg2dzwftvkpa Secrets Automation Item   [connect, 🐧] { id:ftz4pm2xxwmwrsd7rjqn7grzfz }           LOGIN [1 x 2] { id:95cdbc3b-7742-47ec-9056-... [6 x 8]                                  [2 x 4] 2021-04-10T17:20:05.989445270 2021-04-13T17:20:05.989445411     null         null  null [0 x 0]    null
    // 1 7hkzprt3mce284aw9qxnb6ylso      Work Email Account   [work, email] { id:ftz4pm2xxwmwrsd7rjqn7grzfz }           LOGIN [1 x 2] { id:a1b2c3d4-e5f6-7890-abcd-... [5 x 8] [1 x 4] { id:3m87qkv44canznomn7q22sj5... 2022-08-15T09:34:12.123456789 2023-02-20T14:55:30.987654321     null         null  null [0 x 0]    null
    // 2 9xmbnqf5rth317bv8kywc2epui             Home Router [home, network] { id:ftz4pm2xxwmwrsd7rjqn7grzfz } WIRELESS_ROUTER                                  [2 x 2] [6 x 8]                                  [0 x 0]              2020-11-03T18:00 2024-01-07T08:22:45.112233445     null         null  null [0 x 0]    null

    // find all username + passwords with title from the LOGIN category
    df.filter { category == OnePassword.Category.LOGIN }
        .explode { fields }
        .filter { fields.label in setOf("username", "password") }
        .groupBy { title }.pivot(inward = false) { fields.label }.with { fields.value }
        .print()
    //                     title          username                  password
    // 0 Secrets Automation Item             wendy mjXehR*uCj!aoe!iktt9KMtWb
    // 1      Work Email Account alice@company.com          Xk9#mP2@vLqR!nWs
}
