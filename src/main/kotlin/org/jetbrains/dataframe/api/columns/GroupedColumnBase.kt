package org.jetbrains.dataframe.api.columns

import org.jetbrains.dataframe.ColumnDef
import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.DataRow

interface GroupedColumnBase<T> : ColumnDef<DataRow<T>>, DataFrame<T>