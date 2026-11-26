package org.jetbrains.kotlinx.dataframe.api;

public class JavaRecordWrapper {
    private final String name;
    private final JavaRecord record;

    public JavaRecordWrapper(String name, JavaRecord record) {
        this.name = name;
        this.record = record;
    }

    public String getName() {
        return name;
    }

    public JavaRecord getRecord() {
        return record;
    }
}
