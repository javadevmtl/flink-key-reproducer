package com.xxx.entity;

public final class MyEventCountKey {
    private final String countDateTime;
    private final String val1;
    private final String val2;

    public MyEventCountKey(final String countDateTime, final String val1, final String val2) {
        this.countDateTime = countDateTime;
        this.val1 = val1;
        this.val2 = val2;
    }

    public String getCountDateTime() {
        return countDateTime;
    }

    public String getVal1() {
        return val1;
    }

    public String getVal2() {
        return val2;
    }

    @Override
    public String toString() {
        return countDateTime + "|" + val1 + "|" + val2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyEventCountKey that = (MyEventCountKey) o;
        return countDateTime.equals(that.countDateTime) &&
                val1.equals(that.val1) &&
                val2.equals(that.val2);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + countDateTime.hashCode();
        result = prime * result + val1.hashCode();
        result = prime * result +  val2.hashCode();
        return result;
    }
}
