package com.mieyde.tx.common.thread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * positive atomic counter, begin with 0, ensure the number is positive.
 *
 */
public class PositiveAtomicCounter {
    private static final int MASK = 0x7FFFFFFF;
    private final AtomicInteger atom;

    public PositiveAtomicCounter() {
        atom = new AtomicInteger(0);
    }

    public final int incrementAndGet() {
        return atom.incrementAndGet() & MASK;
    }

    public final int getAndIncrement() {
        return atom.getAndIncrement() & MASK;
    }

    public int get() {
        return atom.get() & MASK;
    }

}