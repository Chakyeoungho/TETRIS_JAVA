package tetris.logic;

import javax.swing.SwingUtilities;

import tetris.data.dto.DataManager;
import tetris.logic.core.GameTimer;
import tetris.logic.field.CascadeHandler;
import tetris.logic.tetromino.CollisionChecker;
import tetris.logic.tetromino.HoldHandler;
import tetris.logic.tetromino.LockDelayHandler;
import tetris.logic.tetromino.TetrominoGenerator;
import tetris.logic.tetromino.TetrominoMover;
import tetris.logic.tetromino.spin.Spin;
import tetris.ui.GameRenderer;

public class TetrisEngine {
    private final DataManager gameData;
    private GameTimer gameTimer;
    private LockDelayHandler lockDelay;

    private final CollisionChecker collisionChecker;
    private final TetrominoMover tetrominoMover;
    private final Spin spin;
    private final GameRenderer gameRenderer;
    private final TetrominoGenerator tetrominoGenerator;
    private final HoldHandler hold;
    private final CascadeHandler cascade;

    private volatile boolean paused = false;

    public TetrisEngine(DataManager gameData) {
        this.gameData = gameData;
        this.collisionChecker = new CollisionChecker(gameData);
        this.tetrominoMover = new TetrominoMover(gameData, this);
        this.spin = new Spin(gameData, collisionChecker);
        this.hold = new HoldHandler(gameData, this);
        this.gameRenderer = new GameRenderer(gameData, this);
        this.tetrominoGenerator = new TetrominoGenerator(gameData);
        this.cascade = new CascadeHandler(gameData, this);
    }

    // === Getter methods ===
    public DataManager getDataManager() { return gameData; }
    public CollisionChecker getCollisionChecker() { return collisionChecker; }
    public TetrominoMover getTetrominoMover() { return tetrominoMover; }
    public Spin getSpin() { return spin; }
    public GameRenderer getGameRenderer() { return gameRenderer; }
    public TetrominoGenerator getTetrominoGenerator() { return tetrominoGenerator; }
    public HoldHandler getHoldHandler() { return hold; }
    public CascadeHandler getCascadeHandler() { return cascade; }

    public void hold() { hold.hold(); }

    public long getDropTime(int level) {
        return (long) (Math.pow(0.8 - ((level - 1) * 0.007), level - 1) * 1_000_000_000L);
    }

    public boolean isPaused() { return paused; }

    public void pauseToggle() {
        paused = !paused;
        System.out.println("게임 " + (paused ? "일시정지됨" : "재개됨"));
        if (gameTimer != null) {
            if (paused) gameTimer.stop();
            else gameTimer.start();
        }
    }

    public void startTetris() {
        tetrominoGenerator.generateTetromino();

        SwingUtilities.invokeLater(() -> {
            gameTimer = new GameTimer(1_000_000_000, this::tick);
            gameTimer.start();
        });
    }
    
    public void startLockDelay() {
    	lockDelay = new LockDelayHandler(() -> {
        	startMainTimer();
    	});
    }

    private void tick() {
        if (paused) return;

        if (!tetrominoMover.drop()) {
            tetrominoMover.lockTetromino();
            tetrominoGenerator.generateTetromino();
            tetrominoMover.drop();
        }
        gameRenderer.refreshScreen();
    }

    // Timer controls (wrapped)
    public GameTimer getGameTimer() { return gameTimer; }
    public void startMainTimer() { if (gameTimer != null) gameTimer.start(); }
    public void stopMainTimer() { if (gameTimer != null) gameTimer.stop(); }
    public void setIntervalNanos(long time) { if (gameTimer != null) gameTimer.setIntervalNanos(time); }
    public boolean isMainTimerRunning() { return gameTimer != null && gameTimer.isRunning(); }
}
