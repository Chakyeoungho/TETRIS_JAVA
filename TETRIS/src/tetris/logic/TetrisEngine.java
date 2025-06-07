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

/**
 * 테트리스 게임의 모든 로직을 총괄하고 조율하는 메인 엔진 클래스.
 * 각 로직 컴포넌트(이동, 회전, 점수 등)를 관리하고 게임의 흐름을 제어.
 */
public class TetrisEngine {

    // --- Static Fields ---
    // 게임 시작 시 초기 블록 하강 속도 (1초)
    private static final long INITIAL_DROP_INTERVAL = 1_000_000_000L;

    // --- Instance Fields ---
    // 핵심 데이터 및 로직 컴포넌트들
    private final DataManager gameData;
    private final ScoreManager gameScore;
    private final CascadeHandler cascade;
    private final CollisionChecker collisionChecker;
    private final HoldHandler hold;
    private final Spin spin;
    private final TetrominoGenerator tetrominoGenerator;
    private final TetrominoMover tetrominoMover;
    private final GameRenderer gameRenderer;

    // 타이머 관련 컴포넌트
    private GameTimer gameTimer; // 주기적인 블록 하강을 처리하는 메인 타이머
    private final LockDelayTimer lockDelay; // 블록 고정 지연 시간을 처리하는 타이머

    // 게임 상태 변수
    private volatile int lockDelayMoveCounter = 0; // 락 딜레이 중 이동/회전 횟수 카운터

    // --- Constructor ---
    public TetrisEngine(DataManager gameData) {
        this.gameData = gameData;
        
        // 의존성 주입: 각 로직 컴포넌트들을 생성
        this.gameScore = new ScoreManager(gameData, this);
        this.cascade = new CascadeHandler(gameData, gameScore);
        this.collisionChecker = new CollisionChecker(gameData);
        this.hold = new HoldHandler(gameData, this);
        this.spin = new Spin(gameData, this);
        this.tetrominoGenerator = new TetrominoGenerator(gameData, this);
        this.tetrominoMover = new TetrominoMover(gameData, this);
        this.gameRenderer = new GameRenderer(gameData, this);

        // 락 딜레이 타이머가 만료되었을 때 실행할 작업 정의
        this.lockDelay = new LockDelayTimer(() -> {
            // 바닥에 닿은 상태가 여전하다면 블록을 고정
            if (!tetrominoMover.canMove(0, 1)) {
                tetrominoMover.lockTetromino();
                tetrominoGenerator.generateTetromino(); // 다음 블록 생성
            }
            gameRenderer.refreshScreen(); // 화면 갱신
            startMainTimer(); // 메인 게임 타이머 재시작
        });
    }

    // --- Public Methods: Game Flow Control ---
    /** 게임을 시작. */
    public void startTetris() {
        gameData.getGameState().setCurrentState(GameStateCode.PLAYING);
        tetrominoGenerator.generateTetromino();
        // UI 관련 작업은 EDT(Event Dispatch Thread)에서 실행하도록 보장
        SwingUtilities.invokeLater(() -> {
            initGameTimer(INITIAL_DROP_INTERVAL);
            startMainTimer();
        });
    }

    /** 게임을 재시작. */
    public synchronized void restartGame() {
		resetGameData();
		startTetris();
		gameRenderer.refreshScreen();
	}

    /** 일시정지 상태를 토글. */
    public void togglePauseState() {
        gameData.getGameState().togglePause();
        if (gameTimer != null) {
            if (isPaused()) {
                stopMainTimer();
                stopLockDelay();
            } else {
                startMainTimer();
            }
        }
    }

    // --- Public Methods: Player Actions ---
    /** 홀드 기능을 실행. */
    public void hold() { hold.hold(); }
    
    // --- Public Methods: Timer Control ---
    /** 메인 게임 타이머를 재시작 (정지 후 시작). */
    public synchronized void restartMainTimer() {
        stopMainTimer();
        startMainTimer();
    }
    
    /** 락 딜레이 타이머를 시작. */
    public void startLockDelay() {
        if (!lockDelay.isRunning()) {
            lockDelay.start();
        }
    }

    /** 락 딜레이 타이머를 정지. */
    public void stopLockDelay() {
        if (lockDelay.isRunning()) {
            lockDelay.stop();
        }
    }
    
    /** 락 딜레이 타이머를 리셋 (시간 초기화). */
    public void resetLockDelay() { lockDelay.reset(); }
    
    /** 메인 타이머의 간격(블록 하강 속도)을 설정. */
    public void setIntervalNanos(long time) {
		if (gameTimer != null) {
			gameTimer.setIntervalNanos(time);
		}
	}
    
    // --- Public Methods: State & Data Access ---
    /** 현재 일시정지 상태인지 확인. */
    public boolean isPaused() { return gameData.getGameState().isPaused(); }
    
    /** 메인 타이머가 실행 중인지 확인. */
    public boolean isMainTimerRunning() { return gameTimer != null && gameTimer.isRunning(); }

    /** 락 딜레이 타이머가 실행 중인지 확인. */
    public boolean isLockDelayRunning() { return lockDelay.isRunning(); }
    
    /** 락 딜레이 중 조작 횟수를 반환. */
    public int getLockDelayCounter() { return lockDelayMoveCounter; }
    /** 락 딜레이 중 조작 횟수를 1 증가. */
    public void increaseLockDelayCounter() { lockDelayMoveCounter++; }
    /** 락 딜레이 중 조작 횟수를 리셋. */
    public void resetLockDelayCounter() { lockDelayMoveCounter = 0; }
    
    /** UI에 표시될 '다음 블록' 6개를 큐 형태로 반환. */
    public Tetromino[] getBagPreviewQueue() {
        Tetromino[] bagPreviewQueue = new Tetromino[6];
        int currBagIndex = getTetrominoGenerator().getCurrentPocketIndex();
        int bagNumber = 0; // 0: 현재 가방, 1: 다음 가방

        for (int i = 0; i < 6; i++) {
            // 현재 가방을 다 쓰면 다음 가방으로 넘어감
            if (++currBagIndex >= 7) {
                currBagIndex = 0;
                bagNumber = 1;
            }
            bagPreviewQueue[i] = gameData.getBagCopy()[bagNumber][currBagIndex];
        }
        return bagPreviewQueue;
    }
    
    /** 레벨에 따른 블록 하강 시간(nanos)을 계산하여 반환 (공식 기반). */
    public long getDropTime(int level) {
		return (long) (Math.pow(0.8 - ((level - 1) * 0.007), level - 1) * 1_000_000_000L);
	}
    
    /** 현재 게임 상태 코드를 반환. */
	public GameStateCode getCurrentState() { return gameData.getGameState().getCurrentState(); }

    // --- Public Methods: Component Getters ---
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
    
    // --- Private Methods ---
    /** 메인 게임 타이머를 생성하고 초기화. */
    private void initGameTimer(long intervalNanos) {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        gameTimer = new GameTimer(intervalNanos, this::tick);
    }
    
    /** 메인 게임 타이머를 시작. */
    private void startMainTimer() {
        if (gameTimer != null && !gameTimer.isRunning()) {
            stopLockDelay(); // 메인 타이머가 돌 때는 락 딜레이가 필요 없음
            gameTimer.start();
        }
    }
    
    /** 메인 게임 타이머를 정지. */
    private void stopMainTimer() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
    }

    /** 메인 타이머에 의해 주기적으로 호출되는 게임의 '한 틱(tick)'. */
    private void tick() {
        if (gameData.getGameState().getCurrentState() == GameStateCode.GAME_OVER) {
            stopMainTimer();
            stopLockDelay();
            return;
        }
        
        if (isPaused()) return;

        // 블록이 바닥에 거의 닿았을 때 (2칸 이내), 메인 타이머를 멈추고 락 딜레이를 시작.
        if (!tetrominoMover.canMove(0, 2)) {
            stopMainTimer();
            resetLockDelay();
        }

        tetrominoMover.drop(); // 블록을 한 칸 내림

        // 소프트 드롭 중이면 추가 점수 획득
        if (gameScore.isSoftDrop()) {
            gameScore.increaseScore(ScoreAction.SOFT_DROP.getBaseScore());
        }

        gameRenderer.refreshScreen(); // 화면 갱신
    }
    
    /** 모든 게임 데이터를 초기 상태로 리셋. */
	private synchronized void resetGameData() {
	    // 게임 상태 및 필드 초기화
	    gameData.getGameState().setCurrentState(GameStateCode.READY);
	    gameData.resetField();
	    
	    // 점수 및 스핀 상태 초기화
	    gameScore.resetScoreData();
	    getSpin().setSpinState(SpinState.S0);

	    // 테트로미노 가방 및 생성기 상태 초기화
	    gameData.getTetrominoBag().advanceBag();
	    tetrominoGenerator.resetCurrentPocketIndex();
	    gameData.getTetrominoState().setCurrentTetromino(gameData.getTetrominoBag().getBagCopy()[0][0]);

	    // 홀드 상태 초기화
	    hold.resetHeltTetromino();
	    hold.resetIsHoldUsed();

	    // 타이머 및 락 딜레이 상태 초기화
	    lockDelayMoveCounter = 0;
	    stopMainTimer();
	    stopLockDelay();
	    initGameTimer(INITIAL_DROP_INTERVAL);
	}
}