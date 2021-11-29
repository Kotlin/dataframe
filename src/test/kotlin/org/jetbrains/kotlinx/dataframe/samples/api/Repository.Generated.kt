@file:Suppress(
    "RemoveRedundantBackticks",
    "RemoveRedundantQualifierName",
    "unused", "ObjectPropertyName",
    "UNCHECKED_CAST", "PropertyName",
    "ClassName"
)
package `org`.`jetbrains`.`kotlinx`.`dataframe`.`samples`.`api`

import org.jetbrains.kotlinx.dataframe.annotations.*

// GENERATED. DO NOT EDIT MANUALLY
@DataSchema
interface Repository {
    @ColumnName("full_name")
    val `full_name`: kotlin.String
    @ColumnName("html_url")
    val `html_url`: java.net.URL
    @ColumnName("stargazers_count")
    val `stargazers_count`: kotlin.Int
    val topics: kotlin.String
    val watchers: kotlin.Int
}
