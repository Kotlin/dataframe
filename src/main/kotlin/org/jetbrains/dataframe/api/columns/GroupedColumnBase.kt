package org.jetbrains.dataframe.api.columns

import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.DataFrameRow

interface GroupedColumnBase<T> : SingleColumn<DataFrameRow<T>>, DataFrame<T>