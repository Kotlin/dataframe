package org.jetbrains.kotlinx.dataframe.io;

import org.jetbrains.kotlinx.dataframe.DataFrame;
import org.jetbrains.kotlinx.dataframe.annotations.RequiredByIntellijPlugin;

import java.util.Collections;

class JsonFacadeForDebugger {
    /**
     * utility for rendering dataframe as interactive table in the debugger - it needs json model
     * Java class easier to discover in the debugger
     * DO NOT BREAK ABI OF THIS METHOD!!
     * Keep it for backward compatibility, create a new method if signature must change
     */
    @RequiredByIntellijPlugin
    static String convertToJson(DataFrame<?> df, int rowLimit, Integer nestedRowLimit) {
        return JsonKt.toJsonWithMetadata(df, rowLimit, nestedRowLimit, false, Collections.emptyList(), false);
    }
}
