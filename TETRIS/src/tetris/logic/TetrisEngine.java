package tetris.logic;

import javax.swing.SwingUtilities;

import tetris.data.constant.GameConstants.GameStateCode;
import tetris.data.constant.SpinState;
import tetris.data.constant.Tetromino;
import tetris.logic.core.GameTimer;
import tetris.logic.core.LockDelayTimer;
import tetris.logic.data.DataManager;
import tetris.logic.field.CascadeHandler;
import tetris.logic.scoring.ScoreAction;
import tetris.logic.scoring.ScoreManager;
import tetris.logic.tetromino.CollisionChecker;
import tetris.logic.tetromino.HoldHandler;
import tetris.logic.tetromino.Spin;
import tetris.logic.tetromino.TetrominoGenerator;
import tetris.logic.tetromino.TetrominoMover;
import tetris.ui.GameRenderer;

public class TetrisEngine {
	private static final long INITIAL_DROP_INTERVAL = 1_000_000_000L; // 1초

	// 필드 선언
	private final DataManager gameData;
	private final ScoreManager gameScore;
	private final CascadeHandler cascade;
	private final CollisionChecker collisionChecker;
	private final HoldHandler hold;
	private final Spin spin;
	private final TetrominoGenerator tetrominoGenerator;
	private final TetrominoMover tetrominoMover;
	private final GameRenderer gameRenderer;

	private GameTimer gameTimer;
	private final LockDelayTimer lockDelay;

	private volatile int lockDelayMoveCounter = 0;

	// 생성자
	public TetrisEngine(DataManager gameData) {
		this.gameData = gameData;

		this.gameScore = new ScoreManager(gameData, this);
		this.cascade = new CascadeHandler(gameData, gameScore);
		this.collisionChecker = new CollisionChecker(gameData);
		this.hold = new HoldHandler(gameData, this);
		this.spin = new Spin(gameData, this);
		this.tetrominoGenerator = new TetrominoGenerator(gameData, this);
		this.tetrominoMover = new TetrominoMover(gameData, this);
		this.gameRenderer = new GameRenderer(gameData, this);

		this.lockDelay = new LockDelayTimer(() -> {
			if (!tetrominoMover.canMove(0, 1)) {
				tetrominoMover.lockTetromino();
				tetrominoGenerator.generateTetromino();
			}
			gameRenderer.refreshScreen();
			startMainTimer();
		});
	}

	// 퍼블릭 메서드

	public void startTetris() {
		gameData.getGameState().setCurrentState(GameStateCode.PLAYING);
		tetrominoGenerator.generateTetromino();
		SwingUtilities.invokeLater(() -> {
			initGameTimer(INITIAL_DROP_INTERVAL);
			startMainTimer();
		});
	}

	public void togglePauseState() {
		gameData.getGameState().pauseToggle();
		if (gameTimer != null) {
			if (isPaused()) {
				stopMainTimer();
				stopLockDelay();
			} else {
				startMainTimer();
			}
		}
	}

	public void hold() { hold.hold(); }

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

	public void resetLockDelay() { lockDelay.reset(); }
	public boolean isLockDelayRunning() { return lockDelay.isRunning(); }

	public boolean isPaused() { return gameData.getGameState().isPaused(); }

	public boolean isMainTimerRunning() { return gameTimer != null && gameTimer.isRunning(); }

	// 락 딜레이 카운터 접근 메서드
	public int getLockDelayCounter() { return lockDelayMoveCounter; }
	public void increaseLockDelayCounter() { lockDelayMoveCounter++; }
	public void resetLockDelayCounter() { lockDelayMoveCounter = 0; }

	// 테트로미노 미리보기 큐 반환
	public Tetromino[] getBagPreviewQueue() {
		Tetromino[] bagPreviewQueue = new Tetromino[6];
		int currBagIndex = getTetrominoGenerator().getCurrentPocketIndex();
		int bagNumber = 0;

		for (int i = 0; i < 6; i++) {
			if (++currBagIndex >= 7) {
				currBagIndex = 0;
				bagNumber = 1;
			}
			bagPreviewQueue[i] = gameData.getBagCopy()[bagNumber][currBagIndex];
		}

		return bagPreviewQueue;
	}

	// Getter 메서드
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

	// private 메서드

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

	private void tick() {
		if (gameData.getGameState().getCurrentState() == GameStateCode.GAME_OVER) {
			stopMainTimer();
			stopLockDelay();
			return;
		}
		
		if (isPaused())
			return;

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
	
	public GameStateCode getCurrentState() { return gameData.getGameState().getCurrentState(); }
	
	private synchronized void resetGameData() {
	    // 게임 상태 및 필드 초기화
	    gameData.getGameState().setCurrentState(GameStateCode.READY);
	    gameData.resetField();
	    
	    // 점수 및 스핀 상태 초기화
	    getScoreManager().resetScoreData();
	    getSpin().setSpinState(SpinState.S0);

	    // 테트로미노 가방 및 생성기 상태 초기화
	    gameData.getTetrominoBag().advanceBag();
	    getTetrominoGenerator().resetCurrentPocketIndex();
	    gameData.getTetrominoState().setCurrentTetromino(gameData.getTetrominoBag().getBagCopy()[0][0]);

	    // 홀드 상태 초기화
	    getHoldHandler().resetHeltTetromino();
	    getHoldHandler().resetIsHoldUsed();

	    // 타이머 및 락 딜레이 상태 초기화
	    lockDelayMoveCounter = 0;
	    stopMainTimer();
	    stopLockDelay();
	    initGameTimer(INITIAL_DROP_INTERVAL);
	}

	
	public synchronized void restartGame() {
		resetGameData();
		startTetris();
		getGameRenderer().refreshScreen();
	}
}
