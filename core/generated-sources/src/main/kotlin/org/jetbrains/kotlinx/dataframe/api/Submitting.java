package org.jetbrains.kotlinx.dataframe.api;

import kotlin.Unit;

import java.io.File;

public class Submitting {

    public boolean sample() {
        int number = 1;
        File file = new File("file.json");
        return FirstKt.submit(number, file, e -> {
            System.out.println(e.getMessage());
            return Unit.INSTANCE;
        });
    }
}
