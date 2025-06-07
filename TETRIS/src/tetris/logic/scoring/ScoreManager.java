package tetris.logic.scoring;

import static tetris.data.constant.GameConstants.BUFFER_ZONE;
import static tetris.data.constant.GameConstants.FIELD_X_COUNT;
import static tetris.data.constant.GameConstants.FIELD_Y_COUNT;

import java.awt.Point;

import tetris.data.constant.Tetromino;
import tetris.logic.TetrisEngine;
import tetris.logic.data.DataManager;

public class ScoreManager {
	private final DataManager gameData;
	private final TetrisEngine gameEngine;

	private ScoreAction lastScoreAction = ScoreAction.NOTHING;
	private long score = 0;
	private int level = 1;
	private int totalClearedLine = 0;
	private int combo = 0;
	private int tetrisCount = 0;
	private int tSpinCount = 0;
	private boolean isSoftDrop = false;
	private boolean lastActionWasSpin = false;
	private boolean isB2BCombo = false;
	private boolean[] cornerState = new boolean[4];

	public ScoreManager(DataManager gameData, TetrisEngine gameEngine) {
		this.gameData = gameData;
		this.gameEngine = gameEngine;
	}

	// 필드 경계 확인용 헬퍼 메서드
	private boolean isInBounds(int y, int x) {
		return y >= 0 && y < BUFFER_ZONE + FIELD_Y_COUNT && x >= 0 && x < FIELD_X_COUNT;
	}

	private void updateComboAndB2B(int clearedLine) {
		if (clearedLine == 0) {
		    combo = 0;
		    isB2BCombo = (lastScoreAction == ScoreAction.T_SPIN || lastScoreAction == ScoreAction.MINI_T_SPIN);
		} else if (lastScoreAction == ScoreAction.MINI_T_SPIN_SINGLE || 
		         lastScoreAction == ScoreAction.T_SPIN_SINGLE || 
		         lastScoreAction == ScoreAction.T_SPIN_DOUBLE || 
		         lastScoreAction == ScoreAction.T_SPIN_TRIPLE || 
		         lastScoreAction == ScoreAction.TETRIS) {
		    
		    combo++;
		    
		    if (isB2BCombo) {
		        // B2B 보너스 적용 - 보너스 타입에 따라 조정
		        score += (int)(lastScoreAction.getBaseScore() * 0.5); // 예: 1.5배 점수
		    }
		    
		    isB2BCombo = true;
		} else {
		    combo++;
		    isB2BCombo = false;
		}
	}
	
	/**
	 * T-Spin 체크 A, B, C, D는 T테트로미노 중심 좌표 기준 대각선 4곳 블록 존재 여부
	 * 
	 * @param clearedLine 클리어된 줄 수
	 */
	private void checkTSpin(int clearedLine) {
		boolean A = cornerState[0]; // 코너 값 위치 오프셋
		boolean B = cornerState[1]; // A█B  C█A  D C  B█D
		boolean C = cornerState[2]; // ███   ██  ███  ██
		boolean D = cornerState[3]; // C D  D█B  B█A  A█C

		// SRS 체크 인덱스가 5 이어야 한다
		if (gameEngine.getSpin().getSpinPoint() == 5 && (A && B) && (C || D)) {
			switch (clearedLine) {
			case 0:	lastScoreAction = ScoreAction.T_SPIN; break;
			case 1:	lastScoreAction = ScoreAction.T_SPIN_SINGLE; break;
			case 2:	lastScoreAction = ScoreAction.T_SPIN_DOUBLE; break;
			case 3:	lastScoreAction = ScoreAction.T_SPIN_TRIPLE; break;
			default:
				System.err.println("Warning: Unexpected clearedLine for T-Spin: " + clearedLine);
				lastScoreAction = ScoreAction.T_SPIN;
				break;
			}
		} else if ((C && D) && (A || B)) {
			switch (clearedLine) {
			case 0:	lastScoreAction = ScoreAction.MINI_T_SPIN; break;
			case 1:	lastScoreAction = ScoreAction.MINI_T_SPIN_SINGLE; break;
			default:
				System.err.println("Warning: Unexpected clearedLine for Mini T-Spin: " + clearedLine);
				lastScoreAction = ScoreAction.MINI_T_SPIN;
				break;
			}
		} else {
			lastScoreAction = ScoreAction.NOTHING;
			tSpinCount--;
		}
		tSpinCount++;
	}

	/**
	 * 레벨 업데이트: 클리어한 줄 수에 따라 레벨 상승 적용 (최대 15)
	 * 
	 * @param clearedLine 이번 액션으로 클리어한 줄 수
	 */
	private void updateLevel(int clearedLine) {
		if (clearedLine > 0) {
			int preLevel = getLevel();
			totalClearedLine += clearedLine;
			int currentLevel = Math.min(totalClearedLine / 10 + 1, 15);
			setLevel(currentLevel);

			if (currentLevel > preLevel) {
				long nextTime = gameEngine.getDropTime(currentLevel);
				gameEngine.setIntervalNanos(nextTime);
			}
		}
	}

	public synchronized void updateHardDropScore() {
		int hardDropCount = gameEngine.getTetrominoMover().getHardDroppedCells();
		increaseScore(hardDropCount * ScoreAction.HARD_DROP.getBaseScore());
	}

	/**
	 * 점수 갱신
	 * 
	 * @param clearedLine 이번 액션으로 클리어한 줄 수
	 */
	public synchronized void updateScore(int clearedLine) {
		// T-Spin 판정을 위한 조건:
		// 1) 마지막 동작이 스핀이어야 하고,
		// 2) 현재 테트로미노가 T여야 한다.
		if (lastActionWasSpin && gameData.getTetrominoState().getCurrentTetromino() == Tetromino.T) {
			checkTSpin(clearedLine);
		} else {
			lastScoreAction = ScoreAction.NOTHING;
		}

		switch (clearedLine) {
		case 0:
			if (lastScoreAction == ScoreAction.T_SPIN) {
				increaseScore(level * ScoreAction.T_SPIN.getBaseScore());
			} else if (lastScoreAction == ScoreAction.MINI_T_SPIN) {
				increaseScore(level * ScoreAction.MINI_T_SPIN.getBaseScore());
			} else
				lastScoreAction = ScoreAction.NOTHING;
			break;
		case 1:
			if (lastScoreAction == ScoreAction.T_SPIN_SINGLE) {
				increaseScore(level * ScoreAction.T_SPIN_SINGLE.getBaseScore());
			} else if (lastScoreAction == ScoreAction.MINI_T_SPIN_SINGLE) {
				increaseScore(level * ScoreAction.MINI_T_SPIN_SINGLE.getBaseScore());
			} else {
				increaseScore(level * ScoreAction.SINGLE.getBaseScore());
				lastScoreAction = ScoreAction.SINGLE;
			}
			break;
		case 2:
			if (lastScoreAction == ScoreAction.T_SPIN_DOUBLE) {
				increaseScore(level * ScoreAction.T_SPIN_DOUBLE.getBaseScore());
			} else {
				increaseScore(level * ScoreAction.DOUBLE.getBaseScore());
				lastScoreAction = ScoreAction.DOUBLE;
			}
			break;
		case 3:
			if (lastScoreAction == ScoreAction.T_SPIN_TRIPLE) {
				increaseScore(level * ScoreAction.T_SPIN_TRIPLE.getBaseScore());
			} else {
				increaseScore(level * ScoreAction.TRIPLE.getBaseScore());
				lastScoreAction = ScoreAction.TRIPLE;
			}
			break;
		case 4:
			increaseScore(level * ScoreAction.TETRIS.getBaseScore());
			lastScoreAction = ScoreAction.TETRIS;
			tetrisCount++;
			break;
		default:
			System.err.println("Warning: Unexpected clearedLine: " + clearedLine);
			break;
		}
		// 회전 플래그 초기화
		clearLastActionSpinFlag();
		updateComboAndB2B(clearedLine);
		updateLevel(clearedLine);
	}

	public int getLevel() {return level; }
	public void setLevel(int level) { this.level = level; }

	public ScoreAction getLastAction() { return lastScoreAction; }

	public long getScore() { return score; }
	public synchronized void increaseScore(long score) { this.score += score; }
	public void resetScore() {
		score = 0;
		lastScoreAction = ScoreAction.NOTHING;
	}

	public int getTotalClearedLine() { return totalClearedLine; }

	/**
	 * 총 클리어 라인 수 증가 (한 번에 여러 줄 가능)
	 * 
	 * @param count 증가할 라인 수
	 */
	public void increaseTotalClearedLine() { this.totalClearedLine++; }
	public void resetTotalClearedLine() { totalClearedLine = 0; }

	public boolean isSoftDrop() { return isSoftDrop; }
	public void softDropping() { isSoftDrop = true; }
	public void finishSoftDropping() { isSoftDrop = false; }

	public void markLastActionAsSpin() { lastActionWasSpin = true; }
	public void clearLastActionSpinFlag() { lastActionWasSpin = false; }

	public void setSpinCornerStatus() {
		Point offset = gameData.getTetrominoState().getTetrominoOffset();
		Point[] currentCornerOffset = gameEngine.getSpin().getCurrentSpinState().getCornerOffsets();

		for (int i = 0; i < 4; i++) {
			int y = offset.y + currentCornerOffset[i].y;
			int x = offset.x + currentCornerOffset[i].x;

			cornerState[i] = !isInBounds(y, x) || gameData.getCell(y, x) != Tetromino.EMPTY.ordinal();
		}
	}
	
	public int getComboCount() { return combo; }
	public int getTSpinCount() { return tSpinCount; }
	public int getTetrisCount() { return tetrisCount; }
	public boolean getIsB2B() { return isB2BCombo; }

	/**
	 * 게임 초기화용 전체 리셋 메서드
	 */
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
}
