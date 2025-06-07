package tetris.logic.scoring;

import static tetris.data.constant.GameConstants.BUFFER_ZONE;
import static tetris.data.constant.GameConstants.FIELD_X_COUNT;
import static tetris.data.constant.GameConstants.FIELD_Y_COUNT;

import java.awt.Point;

import tetris.data.constant.Tetromino;
import tetris.logic.TetrisEngine;
import tetris.logic.data.DataManager;

/**
 * 게임의 점수, 레벨, 콤보, T-Spin 등 모든 득점 관련 로직을 관리하는 클래스.
 */
public class ScoreManager {

    // --- Instance Fields ---
    // 의존성 주입
    private final DataManager gameData;
    private final TetrisEngine gameEngine;

    // 점수 및 레벨 관련
    private long score = 0;
    private int level = 1;
    private int totalClearedLine = 0;
    
    // 콤보 및 특수 액션 관련
    private ScoreAction lastScoreAction = ScoreAction.NOTHING;
    private int combo = 0;
    private boolean isB2BCombo = false; // Back-to-Back 상태 여부
    private boolean lastActionWasSpin = false; // 직전 액션이 회전이었는지 (T-Spin 판정용)
    private boolean[] cornerState = new boolean[4]; // T-Spin 판정을 위한 코너 점유 상태
    
    // 플레이 기록
    private int tetrisCount = 0;
    private int tSpinCount = 0;
    
    // 드롭 상태
    private boolean isSoftDrop = false;

    // --- Constructor ---
    public ScoreManager(DataManager gameData, TetrisEngine gameEngine) {
        this.gameData = gameData;
        this.gameEngine = gameEngine;
    }

    // --- Public Methods: Main Logic ---
    /**
     * 클리어된 줄 수에 따라 점수, 레벨, 콤보 등을 종합적으로 갱신.
     * @param clearedLine 이번에 지워진 줄 수
     */
    public synchronized void updateScore(int clearedLine) {
        // 1. T-Spin 판정
        if (lastActionWasSpin && gameData.getTetrominoState().getCurrentTetromino() == Tetromino.T) {
            checkTSpin(clearedLine);
        } else {
            lastScoreAction = ScoreAction.NOTHING;
        }

        // 2. 줄 클리어 점수 계산
        switch (clearedLine) {
            case 0: // T-Spin (No-Line)
                if (lastScoreAction == ScoreAction.T_SPIN) increaseScore(level * ScoreAction.T_SPIN.getBaseScore());
                else if (lastScoreAction == ScoreAction.MINI_T_SPIN) increaseScore(level * ScoreAction.MINI_T_SPIN.getBaseScore());
                else lastScoreAction = ScoreAction.NOTHING;
                break;
            case 1: // Single or T-Spin Single
                if (lastScoreAction == ScoreAction.T_SPIN_SINGLE) increaseScore(level * ScoreAction.T_SPIN_SINGLE.getBaseScore());
                else if (lastScoreAction == ScoreAction.MINI_T_SPIN_SINGLE) increaseScore(level * ScoreAction.MINI_T_SPIN_SINGLE.getBaseScore());
                else {
                    increaseScore(level * ScoreAction.SINGLE.getBaseScore());
                    lastScoreAction = ScoreAction.SINGLE;
                }
                break;
            case 2: // Double or T-Spin Double
                if (lastScoreAction == ScoreAction.T_SPIN_DOUBLE) increaseScore(level * ScoreAction.T_SPIN_DOUBLE.getBaseScore());
                else {
                    increaseScore(level * ScoreAction.DOUBLE.getBaseScore());
                    lastScoreAction = ScoreAction.DOUBLE;
                }
                break;
            case 3: // Triple or T-Spin Triple
                if (lastScoreAction == ScoreAction.T_SPIN_TRIPLE) increaseScore(level * ScoreAction.T_SPIN_TRIPLE.getBaseScore());
                else {
                    increaseScore(level * ScoreAction.TRIPLE.getBaseScore());
                    lastScoreAction = ScoreAction.TRIPLE;
                }
                break;
            case 4: // Tetris
                increaseScore(level * ScoreAction.TETRIS.getBaseScore());
                lastScoreAction = ScoreAction.TETRIS;
                tetrisCount++;
                break;
            default:
                System.err.println("Warning: Unexpected clearedLine: " + clearedLine);
                break;
        }
        
        // 3. 후속 처리
        clearLastActionSpinFlag(); // 스핀 플래그 초기화
        updateComboAndB2B(clearedLine);
        updateLevel(clearedLine);
    }

    /** 하드 드롭으로 얻는 점수를 계산하여 추가. */
    public synchronized void updateHardDropScore() {
        int hardDropCount = gameEngine.getTetrominoMover().getHardDroppedCells();
        increaseScore(hardDropCount * ScoreAction.HARD_DROP.getBaseScore());
    }
    
    /** T-Spin 판정을 위해, 현재 테트로미노의 4개 코너가 막혀있는지 상태를 갱신. */
    public void setSpinCornerStatus() {
        Point offset = gameData.getTetrominoState().getTetrominoOffset();
        Point[] currentCornerOffset = gameEngine.getSpin().getCurrentSpinState().getCornerOffsets();

        for (int i = 0; i < 4; i++) {
            int y = offset.y + currentCornerOffset[i].y;
            int x = offset.x + currentCornerOffset[i].x;
            // 코너가 필드 밖이거나, 비어있지 않으면 true
            cornerState[i] = !isInBounds(y, x) || gameData.getCell(y, x) != Tetromino.EMPTY.ordinal();
        }
    }

    /** 게임 재시작 등을 위해 모든 점수 관련 데이터를 초기화. */
    public void resetScoreData() {
        lastScoreAction = ScoreAction.NOTHING;
        score = 0;
        level = 1;
        totalClearedLine = 0;
        combo = 0;
        tetrisCount = 0;
        tSpinCount = 0;
        isSoftDrop = false;
        lastActionWasSpin = false;
        isB2BCombo = false;
        cornerState = new boolean[4];
    }
    
    // --- Private Methods: Sub Logic ---
    /** 
     * T-Spin 또는 Mini T-Spin 인지 판별.
     * T블록 주변 4개 코너 중 3개 이상이 막혀있어야 함.
     * @param clearedLine 지워진 줄 수
     */
    private void checkTSpin(int clearedLine) {
		boolean A = cornerState[0]; // 코너 값 위치 오프셋
		boolean B = cornerState[1]; // A█B  C█A  D C  B█D
		boolean C = cornerState[2]; // ███   ██  ███  ██
		boolean D = cornerState[3]; // C D  D█B  B█A  A█C
		
        // SRS 규칙: T-Spin 조건 (전면 2개 코너 + 후면 1개 이상 점유)
        if (gameEngine.getSpin().getSpinPoint() == 5 && (A && B) && (C || D)) {
            tSpinCount++;
            switch (clearedLine) {
                case 0: lastScoreAction = ScoreAction.T_SPIN; break;
                case 1: lastScoreAction = ScoreAction.T_SPIN_SINGLE; break;
                case 2: lastScoreAction = ScoreAction.T_SPIN_DOUBLE; break;
                case 3: lastScoreAction = ScoreAction.T_SPIN_TRIPLE; break;
            }
        // Mini T-Spin 조건 (후면 2개 코너 + 전면 1개 이상 점유)
        } else if ((C && D) && (A || B)) {
            tSpinCount++;
            switch (clearedLine) {
                case 0: lastScoreAction = ScoreAction.MINI_T_SPIN; break;
                case 1: lastScoreAction = ScoreAction.MINI_T_SPIN_SINGLE; break;
            }
        } else {
            // T-Spin 실패
            lastScoreAction = ScoreAction.NOTHING;
        }
    }
    
    /** 콤보 및 Back-to-Back(B2B) 보너스 상태를 갱신. */
    private void updateComboAndB2B(int clearedLine) {
        if (clearedLine == 0) {
            combo = 0; // 줄을 못 지우면 콤보 초기화
            // B2B는 T-Spin/Tetris 성공 시 유지, 일반 클리어 시 초기화.
            isB2BCombo = (lastScoreAction == ScoreAction.T_SPIN || lastScoreAction == ScoreAction.MINI_T_SPIN);
        } else {
            combo++; // 줄을 지웠으면 콤보 증가
            // T-Spin 또는 Tetris 액션이었는지 확인
            if (lastScoreAction.getBaseScore() >= ScoreAction.TETRIS.getBaseScore()) {
                if (isB2BCombo) {
                    // B2B 보너스 적용: 기본 점수의 0.5배 추가 (총 1.5배)
                    score += (long) (lastScoreAction.getBaseScore() * level * 0.5);
                }
                isB2BCombo = true; // B2B 상태 유지 또는 활성화
            } else {
                isB2BCombo = false; // 일반 클리어는 B2B를 끊음
            }
        }
    }
    
    /** 레벨을 갱신하고, 레벨이 오르면 게임 속도를 조절. */
    private void updateLevel(int clearedLine) {
        if (clearedLine > 0) {
            int prevLevel = level;
            // 10줄마다 레벨업, 최대 15레벨
            level = Math.min(totalClearedLine / 10 + 1, 15);

            if (level > prevLevel) {
                // 레벨업 시 게임 속도(블록 하강 시간) 증가
                long nextDropTime = gameEngine.getDropTime(level);
                gameEngine.setIntervalNanos(nextDropTime);
            }
        }
    }

    /** 좌표가 필드 경계 내에 있는지 확인. */
    private boolean isInBounds(int y, int x) {
        return y >= 0 && y < BUFFER_ZONE + FIELD_Y_COUNT && x >= 0 && x < FIELD_X_COUNT;
    }

    // --- Public Methods: Getters & Setters ---
    public long getScore() { return score; }
    public synchronized void increaseScore(long amount) { this.score += amount; }
    public void resetScore() { this.score = 0; }
    
    public int getLevel() {return level; }
    public void setLevel(int level) { this.level = level; }
    
    public int getTotalClearedLine() { return totalClearedLine; }
    public void increaseTotalClearedLine() { this.totalClearedLine++; }
    public void resetTotalClearedLine() { totalClearedLine = 0; }

    public int getComboCount() { return combo; }
    public int getTSpinCount() { return tSpinCount; }
    public int getTetrisCount() { return tetrisCount; }
    public boolean getIsB2B() { return isB2BCombo; }
    public ScoreAction getLastAction() { return lastScoreAction; }
    
    public boolean isSoftDrop() { return isSoftDrop; }
    public void softDropping() { isSoftDrop = true; }
    public void finishSoftDropping() { isSoftDrop = false; }

    public void markLastActionAsSpin() { lastActionWasSpin = true; }
    public void clearLastActionSpinFlag() { lastActionWasSpin = false; }
}