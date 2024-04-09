package org.jetbrains.kotlinx.dataframe.api;

import java.util.Objects;

public class JavaPojo {

    private int a;
    private String b;

    public JavaPojo() {}

    public JavaPojo(String b, int a) {
        this.a = a;
        this.b = b;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JavaPojo testPojo = (JavaPojo) o;

        if (a != testPojo.a) return false;
        return Objects.equals(b, testPojo.b);
    }

    @Override
    public int hashCode() {
        int result = a;
        result = 31 * result + (b != null ? b.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestPojo{" +
            "a=" + a +
            ", b='" + b + '\'' +
            '}';
    }
}
