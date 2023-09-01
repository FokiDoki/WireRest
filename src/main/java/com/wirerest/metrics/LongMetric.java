package com.wirerest.metrics;

import java.util.concurrent.atomic.AtomicLong;


public class LongMetric {
    private final AtomicLong value = new AtomicLong(0);

    public void increment() {
        value.incrementAndGet();
    }

    public void decrement() {
        value.decrementAndGet();
    }

    public void add(long n) {
        value.addAndGet(n);
    }

    public void set(long n) {
        value.set(n);
    }

    public void subtract(long n) {
        value.addAndGet(n * -1);
    }

    public long get() {
        return value.get();
    }

}
