package tetris.logic;

import javax.swing.SwingUtilities;

import tetris.data.dto.DataManager;
import tetris.logic.core.GameTimer;
import tetris.logic.core.LockDelayTimer;
import tetris.logic.field.CascadeHandler;
import tetris.logic.scoring.ScoreAction;
import tetris.logic.scoring.ScoreManager;
import tetris.logic.tetromino.CollisionChecker;
import tetris.logic.tetromino.HoldHandler;
import tetris.logic.tetromino.TetrominoGenerator;
import tetris.logic.tetromino.TetrominoMover;
import tetris.logic.tetromino.spin.Spin;
import tetris.ui.GameRenderer;

public class TetrisEngine {
    private static final long INITIAL_DROP_INTERVAL = 1_000_000_000L; // 1초

    private final DataManager gameData;
    private final GameRenderer gameRenderer;

    private final CollisionChecker collisionChecker;
    private final TetrominoMover tetrominoMover;
    private final Spin spin;
    private final TetrominoGenerator tetrominoGenerator;
    private final HoldHandler hold;
    private final CascadeHandler cascade;
    private final ScoreManager gameScore;

    private GameTimer gameTimer;
    private final LockDelayTimer lockDelay;

    private volatile boolean paused = false;

    public TetrisEngine(DataManager gameData) {
        this.gameData = gameData;

        this.collisionChecker = new CollisionChecker(gameData);
        this.tetrominoMover = new TetrominoMover(gameData, this);
        this.spin = new Spin(gameData, collisionChecker);
        this.hold = new HoldHandler(gameData, this);
        this.tetrominoGenerator = new TetrominoGenerator(gameData);
        this.cascade = new CascadeHandler(gameData, this);
        this.gameRenderer = new GameRenderer(gameData, this);
        this.gameScore = new ScoreManager(gameData, this);

        this.lockDelay = new LockDelayTimer(() -> {
            if (!tetrominoMover.canMove(0, 1)) {
                tetrominoMover.lockTetromino();
                tetrominoGenerator.generateTetromino();
            }
            gameRenderer.refreshScreen();
            startMainTimer();
        });
    }

    public void startTetris() {
        tetrominoGenerator.generateTetromino();
        SwingUtilities.invokeLater(() -> {
            initGameTimer(INITIAL_DROP_INTERVAL);
            startMainTimer();
        });
    }

    public void pauseToggle() {
        paused = !paused;
        if (gameTimer != null) {
            if (paused) {
                gameTimer.stop();
                stopLockDelay();
            } else {
                gameTimer.start();
            }
        }
    }

    public void hold() { hold.hold(); }

    private void initGameTimer(long intervalNanos) {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        gameTimer = new GameTimer(intervalNanos, this::tick);
    }

    private void startMainTimer() {
        if (gameTimer != null && !gameTimer.isRunning()) {
            stopLockDelay();
            gameTimer.start();
        }
    }

    private void stopMainTimer() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
    }

    public synchronized void restartMainTimer() {
        stopMainTimer();
        startMainTimer();
    }

    public void startLockDelay() {
        if (!lockDelay.isRunning()) {
            lockDelay.start();
        }
    }

    public void stopLockDelay() {
        if (lockDelay.isRunning()) {
            lockDelay.stop();
        }
    }

    public void resetLockDelay() {
        lockDelay.reset();
    }

    public boolean isLockDelayRunning() {
        return lockDelay.isRunning();
    }

    private void tick() {
        if (paused) return;

        if (!tetrominoMover.canMove(0, 2)) {
            stopMainTimer();
            resetLockDelay();
        }

        tetrominoMover.drop();

        if (gameScore.isSoftDrop()) {
            gameScore.increaseScore(ScoreAction.SOFT_DROP.getBaseScore());
        }

        gameRenderer.refreshScreen();
    }

    public long getDropTime(int level) {
        return (long) (Math.pow(0.8 - ((level - 1) * 0.007), level - 1) * 1_000_000_000L);
    }

    public void setIntervalNanos(long time) {
        if (gameTimer != null) {
            gameTimer.setIntervalNanos(time);
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isMainTimerRunning() {
        return gameTimer != null && gameTimer.isRunning();
    }

    // --- Getters ---
    public DataManager getDataManager() { return gameData; }
    public CollisionChecker getCollisionChecker() { return collisionChecker; }
    public TetrominoMover getTetrominoMover() { return tetrominoMover; }
    public Spin getSpin() { return spin; }
    public GameRenderer getGameRenderer() { return gameRenderer; }
    public TetrominoGenerator getTetrominoGenerator() { return tetrominoGenerator; }
    public HoldHandler getHoldHandler() { return hold; }
    public CascadeHandler getCascadeHandler() { return cascade; }
    public GameTimer getGameTimer() { return gameTimer; }
    public ScoreManager getScoreManager() { return gameScore; }
}
