package tetris.data.model;

public class GameState {
    // --- Fields ---
    // 게임 진행 상태
    private int gameStateCode = 0;
    private int level = 1;

    // 점수 관련

    // 잠금(lock) 관련
    private int lockDelayTime = 0;
    private int lockResetCount = 0;
    private boolean isLockTimerRunning = false;
    private boolean isLastMoveSpin = false;


    // --- Getters & Setters ---
    // 게임 진행 상태
    public int getGameStateCode() { return gameStateCode; }
    public void setGameStateCode(int gameStateCode) { this.gameStateCode = gameStateCode; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }


    // 점수 관련
    private long score = 0;
    public long getScore() { return score; }
    public void setScore(long score) { this.score = score; }

    private int clearedLines = 0;
    public int getClearedLines() { return clearedLines; }
    public void setClearedLines(int clearedLines) { this.clearedLines = clearedLines; }


    // 잠금(lock) 관련
    public int getLockDelayTime() { return lockDelayTime; }
    public void setLockDelayTime(int lockDelayTime) { this.lockDelayTime = lockDelayTime; }

    public int getLockResetCount() { return lockResetCount; }
    public void setLockResetCount(int lockResetCount) { this.lockResetCount = lockResetCount; }

    public boolean isLockInProgress() { return isLockTimerRunning; }
    public void setLockInProgress(boolean isLockTimerRunning) { this.isLockTimerRunning = isLockTimerRunning; }

    public boolean isLastMoveSpin() { return isLastMoveSpin; }
    public void setLastMoveSpin(boolean isLastMoveSpin) { this.isLastMoveSpin = isLastMoveSpin; }
}
