package tetris.logic.tetromino;

import tetris.logic.core.GameTimer;

public class LockDelayHandler {
    private static final long LOCK_DELAY_NANOS = 500_000_000L;

    private final Runnable lockTask;
    private GameTimer internalTimer;
    private boolean isActive = false;

    public LockDelayHandler(Runnable lockTask) {
        this.lockTask = lockTask;
    }

    public void start() {
        if (isActive) return;
        isActive = true;
        internalTimer = new GameTimer(LOCK_DELAY_NANOS, () -> {
            isActive = false;
            lockTask.run();
        });
        internalTimer.start();
    }

    public void reset() {
        if (!isActive) return;
        internalTimer.setIntervalNanos(LOCK_DELAY_NANOS);  // 즉시 다시 시작
    }

    public void cancel() {
        if (!isActive) return;
        isActive = false;
        internalTimer.stop();
    }

    public boolean isActive() {
        return isActive;
    }
}
