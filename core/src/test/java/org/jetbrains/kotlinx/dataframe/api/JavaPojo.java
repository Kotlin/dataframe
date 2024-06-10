package org.jetbrains.kotlinx.dataframe.api;

import java.util.Objects;

public class JavaPojo {

    private int a;
    private String b;
    private Integer c;
    private Number d;

    public JavaPojo() {}

    public JavaPojo(Number d, Integer c, String b, int a) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
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

    public Integer getC() {
        return c;
    }

    public void setC(Integer c) {
        this.c = c;
    }

    public Number getD() {
        return d;
    }

    public void setD(Number d) {
        this.d = d;
    }

    public static int getNot() {
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JavaPojo)) return false;
        JavaPojo javaPojo = (JavaPojo) o;
        return a == javaPojo.a && Objects.equals(b, javaPojo.b) && Objects.equals(c, javaPojo.c) && Objects.equals(d, javaPojo.d);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, c, d);
    }

    @Override
    public String toString() {
        return "JavaPojo{" +
            "a=" + a +
            ", b='" + b + '\'' +
            ", c=" + c +
            ", d=" + d +
            '}';
    }
}
