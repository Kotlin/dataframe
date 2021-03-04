package org.jetbrains.dataframe.columns

import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.DataRow

interface ColumnGroup<T> : ColumnReference<DataRow<T>>, DataFrame<T>