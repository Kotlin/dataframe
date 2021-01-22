package org.jetbrains.dataframe.api.columns

import org.jetbrains.dataframe.ColumnReference
import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.DataRow

interface GroupedColumnBase<T> : ColumnReference<DataRow<T>>, DataFrame<T>