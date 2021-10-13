package org.jetbrains.kotlinx.dataframe.impl.receivers

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.SelectReceiver
import org.jetbrains.kotlinx.dataframe.impl.DataFrameReceiver

internal open class SelectReceiverImpl<T>(source: DataFrame<T>, allowMissingColumns: Boolean) :
    DataFrameReceiver<T>(source, allowMissingColumns), SelectReceiver<T>
