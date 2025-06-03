package tetris.logic.core;

public class GameTimer implements Runnable {
    private volatile boolean running = false;
    private volatile long intervalNanos;
    private final Runnable task;
    private Thread thread;

    private final Object lock = new Object();
    private volatile boolean forceRunRequested = false;

    public GameTimer(long intervalNanos, Runnable task) {
        if (intervalNanos <= 0) {
            throw new IllegalArgumentException("intervalNanos must be > 0");
        }
        this.intervalNanos = intervalNanos; // 직접 할당
        this.task = task;
    }

    public void start() {
        if (running) return;
        forceRunRequested = false; // 방어 코드
        running = true;
        thread = new Thread(this, "TetrisTimerThread");
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        running = false;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setIntervalNanos(long newIntervalNanos) {
        if (newIntervalNanos <= 0) {
            throw new IllegalArgumentException("intervalNanos must be > 0");
        }
        intervalNanos = newIntervalNanos;
        requestImmediateRun();
    }

    public long getIntervalNanos() {
        return intervalNanos;
    }

    private void requestImmediateRun() {
        forceRunRequested = true;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                task.run(); // 첫 실행

                long sleepNanos = intervalNanos;
                long deadline = System.nanoTime() + sleepNanos;

                while (running) {
                    long now = System.nanoTime();
                    long remaining = deadline - now;

                    if (forceRunRequested) {
                        forceRunRequested = false;
                        break; // 즉시 다음 주기로 이동
                    }

                    if (remaining <= 0) break;

                    if (remaining > 2_000_000) {
                        synchronized (lock) {
                            lock.wait(remaining / 1_000_000, (int) (remaining % 1_000_000));
                        }
                    } else {
                        Thread.onSpinWait();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (Exception e) {
                System.err.println("[GameTimer] Task threw exception:");
                e.printStackTrace();
            }
        }
    }
}
