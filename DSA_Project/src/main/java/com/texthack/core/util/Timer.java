package com.texthack.core.util;

/**
 * High-precision timer measuring algorithmic runtimes in nanoseconds and microseconds.
 */
public class Timer {
    private long startTime;
    private long stopTime;
    private boolean running;

    public Timer() {
        this.running = false;
    }

    public void start() {
        this.startTime = System.nanoTime();
        this.running = true;
    }

    public void stop() {
        this.stopTime = System.nanoTime();
        this.running = false;
    }

    public long getElapsedNanos() {
        return running ? (System.nanoTime() - startTime) : (stopTime - startTime);
    }

    public long getElapsedMicros() {
        return getElapsedNanos() / 1000;
    }

    public double getElapsedMillis() {
        return getElapsedNanos() / 1_000_000.0;
    }
}
