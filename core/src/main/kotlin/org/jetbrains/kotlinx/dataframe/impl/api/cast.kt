package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.api.ExcessiveColumns
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import kotlin.reflect.KType

/**
 * Checks that [GroupBy.keys] match [keysType] and every group matches [groupsType].
 * The converted dataframes are discarded on purpose to avoid unwanted changes.
 */
@PublishedApi
internal fun GroupBy<*, *>.verifyGroupBySchemaImpl(keysType: KType, groupsType: KType) {
    keys.convertToImpl(keysType, allowConversion = false, ExcessiveColumns.Keep)
    groups.values().forEach { it.convertToImpl(groupsType, allowConversion = false, ExcessiveColumns.Keep) }
}
