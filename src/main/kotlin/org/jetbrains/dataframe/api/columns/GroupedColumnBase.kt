package org.jetbrains.dataframe.api.columns

import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.DataRow

interface GroupedColumnBase<T> : SingleColumn<DataRow<T>>, DataFrame<T>