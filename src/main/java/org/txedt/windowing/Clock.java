package org.txedt.windowing;

public final class Clock {
    private volatile long lastTick = System.currentTimeMillis();
    private volatile double fps = 0;
    private volatile long frameTime = 0;
    public Clock() {}

    public double getFps() {
        return fps;
    }

    public long getFrameTime() {
        return frameTime;
    }

    public void tick() {
        var oldTime = lastTick;
        lastTick = System.currentTimeMillis();
        frameTime = lastTick - oldTime;
        fps = frameTime == 0 ? 0 : 1000d / frameTime;
    }

    public void frameRate(double targetFps) {
        long frameDuration = (long)(1000.0 / targetFps); // ms per frame
        long now = System.currentTimeMillis();
        long elapsed = now - lastTick;
        long waitTime = frameDuration - elapsed;

        if (waitTime > 0) {
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            now = System.currentTimeMillis();
        }

        long oldTime = lastTick;
        lastTick = now;
        frameTime = lastTick - oldTime;
        fps = frameTime == 0 ? 0 : 1000d / frameTime;
    }
}
