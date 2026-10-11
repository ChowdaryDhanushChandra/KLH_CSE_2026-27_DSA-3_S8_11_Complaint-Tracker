package com.texthack.metrics;

/**
 * High-precision execution timer for benchmarking algorithms.
 */
public class ExecutionTimer {
    private long startTimeNanos;
    private long elapsedTimeNanos;
    private boolean isRunning;

    public void start() {
        this.startTimeNanos = System.nanoTime();
        this.isRunning = true;
    }

    public void stop() {
        if (isRunning) {
            this.elapsedTimeNanos = System.nanoTime() - startTimeNanos;
            this.isRunning = false;
        }
    }

    public long getElapsedNanos() {
        return elapsedTimeNanos;
    }

    public double getElapsedMicros() {
        return elapsedTimeNanos / 1000.0;
    }

    public double getElapsedMillis() {
        return elapsedTimeNanos / 1_000_000.0;
    }

    public String getFormattedTime() {
        if (elapsedTimeNanos < 1000) {
            return elapsedTimeNanos + " ns";
        } else if (elapsedTimeNanos < 1_000_000) {
            return String.format("%.2f µs", getElapsedMicros());
        } else {
            return String.format("%.2f ms", getElapsedMillis());
        }
    }
}
