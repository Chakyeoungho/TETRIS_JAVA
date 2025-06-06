package tetris.logic.core;

public class LockDelayTimer implements Runnable {
    private static final long LOCK_DELAY_NANOS = 500_000_000L; // 0.5초 딜레이

    private volatile boolean running = false;
    private final Runnable task;
    private Thread thread;

    private final Object lock = new Object();
    private volatile boolean forceRunRequested = false;

    public LockDelayTimer(Runnable task) {
        this.task = task;
    }

    public synchronized void start() {
        if (running) return;
        forceRunRequested = false;
        running = true;
        thread = new Thread(this, "LockDelayTimerThread");
        thread.setDaemon(true);
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public void reset() {
        stop();
        try {
            if (thread != null) {
                thread.join(); // 기존 스레드가 종료될 때까지 대기
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        start();
    }

    public boolean isRunning() {
        return running;
    }

    public void requestImmediateRun() {
        forceRunRequested = true;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                long deadline = System.nanoTime() + LOCK_DELAY_NANOS;

                while (running) {
                    if (forceRunRequested) {
                        forceRunRequested = false;
                        break;
                    }

                    long remaining = deadline - System.nanoTime();
                    if (remaining <= 0) break;

                    if (remaining > 2_000_000) {
                        synchronized (lock) {
                            lock.wait(remaining / 1_000_000, (int) (remaining % 1_000_000));
                        }
                    } else {
                        Thread.onSpinWait();
                    }
                }

                if (!running) break;
                task.run();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (Exception e) {
                System.err.println("[LockDelayTimer] Task threw exception:");
                e.printStackTrace();
            }
		}
	}
}
