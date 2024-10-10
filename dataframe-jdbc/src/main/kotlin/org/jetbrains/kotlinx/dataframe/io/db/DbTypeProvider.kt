package org.jetbrains.kotlinx.dataframe.io.db

public interface DbTypeProvider {
    public fun getDbType(): DbType?
}
