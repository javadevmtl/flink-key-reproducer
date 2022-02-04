package com.xxx.entity;

import java.time.Instant;

public final class MyEvent {
    private final String event;
    private final String val1;
    private final String val2;
    private final Instant transactionDateTime;

    public MyEvent(String event, String val1, String val2, Instant transactionDateTime) {
        this.event = event;
        this.val1 = val1;
        this.val2 = val2;
        this.transactionDateTime = transactionDateTime;
    }

    public String getEvent() {
        return event;
    }

    public String getVal1() {
        return val1;
    }

    public String getVal2() {
        return val2;
    }

    public Instant getTransactionDateTime() {
        return transactionDateTime;
    }

    @Override
    public String toString() {
        return "MyEvent{" +
                "event='" + event + '\'' +
                ", val1='" + val1 + '\'' +
                ", val2='" + val2 + '\'' +
                ", transactionDateTime=" + transactionDateTime +
                '}';
    }
}
