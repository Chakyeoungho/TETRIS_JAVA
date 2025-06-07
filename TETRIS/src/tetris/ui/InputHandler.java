package tetris.ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import tetris.data.constant.GameConstants.GameStateCode;
import tetris.logic.TetrisEngine;

/**
 * 키보드 입력을 받아 TetrisEngine에 전달하는 역할을 하는 클래스.
 * KeyAdapter를 상속받아 필요한 메서드만 오버라이드.
 */
public class InputHandler extends KeyAdapter {

    // --- Instance Fields ---
	private final TetrisEngine gameEngine;
	private boolean isDownPressed = false; // Down 키가 계속 눌려있는지 추적

    // --- Constructor ---
	public InputHandler(TetrisEngine gameEngine) {
		this.gameEngine = gameEngine;
	}
    
    // --- Public Methods (from KeyAdapter) ---
	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		// Case 1: 게임오버 상태 - R(재시작) 키 외에 모두 무시
		if (gameEngine.getCurrentState() == GameStateCode.GAME_OVER) {
			if (keyCode == KeyEvent.VK_R) {
				gameEngine.restartGame();
			}
			return;
		}
		
		// Case 2: 일시정지 상태 - ESC, F1(일시정지 해제) 키 외에 모두 무시
		if (gameEngine.isPaused()) {
			if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_F1) {
				gameEngine.togglePauseState();
				gameEngine.getGameRenderer().refreshScreen();
			}
			return;
		}

		// 회전 키(UP, X, CTRL, Z)를 눌렀다면, T-Spin 판정을 위해 '회전' 액션을 기록
		if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_X || keyCode == KeyEvent.VK_CONTROL
				|| keyCode == KeyEvent.VK_Z) {
			gameEngine.getScoreManager().markLastActionAsSpin();
		} else {
			gameEngine.getScoreManager().clearLastActionSpinFlag();
		}
		
		// Case 3: 플레이 중 상태
		switch (keyCode) {
			// 이동
			case KeyEvent.VK_LEFT:
				gameEngine.getTetrominoMover().left();
				resetLockDelayCounter();
				break;
			case KeyEvent.VK_RIGHT:
				gameEngine.getTetrominoMover().right();
				resetLockDelayCounter();
				break;

			// 회전
			case KeyEvent.VK_UP:
			case KeyEvent.VK_X: // 시계방향 회전
				gameEngine.getSpin().spin(true);
				resetLockDelayCounter();
				break;
			case KeyEvent.VK_CONTROL:
			case KeyEvent.VK_Z: // 반시계방향 회전
				gameEngine.getSpin().spin(false);
				resetLockDelayCounter();
				break;

			// 드롭
			case KeyEvent.VK_DOWN: // 소프트 드롭
				// 키를 계속 누르고 있을 때, 이벤트가 중복 발생하는 것을 방지
				if (isDownPressed) break;
				isDownPressed = true;
				gameEngine.setIntervalNanos(gameEngine.getDropTime(gameEngine.getScoreManager().getLevel()) / 20);
				gameEngine.getScoreManager().softDropping();
				break;
			case KeyEvent.VK_SPACE: // 하드 드롭
				gameEngine.stopLockDelay(); // 하드 드롭은 즉시 고정이므로 락 딜레이를 멈춤
				gameEngine.getTetrominoMover().hardDrop();
				gameEngine.getScoreManager().updateHardDropScore();
				gameEngine.getTetrominoGenerator().generateTetromino();
				gameEngine.restartMainTimer();
				break;
				
			// 기타
			case KeyEvent.VK_C:
			case KeyEvent.VK_SHIFT: // 홀드
				gameEngine.hold();
				break;
			case KeyEvent.VK_ESCAPE:
			case KeyEvent.VK_F1: // 일시정지
				gameEngine.togglePauseState();
				break;
		}

		// 모든 키 입력 후 화면을 즉시 갱신하여 빠른 반응성을 제공
		gameEngine.getGameRenderer().refreshScreen();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Down 키에서 손을 떼면, 소프트 드롭 상태를 해제하고 타이머 속도를 원상복구
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			isDownPressed = false;
			gameEngine.setIntervalNanos(gameEngine.getDropTime(gameEngine.getScoreManager().getLevel()));
			gameEngine.getScoreManager().finishSoftDropping();
		}
	}
    
    // --- Public Methods (Helper) ---
	/**
	 * 블록이 바닥 근처일 때 조작 시, 락 딜레이를 리셋하여 더 조작할 기회를 줌.
	 * Infinity(무한 회전)을 방지하기 위해 횟수 제한.
	 */
	public void resetLockDelayCounter() {
		boolean canFall = gameEngine.getTetrominoMover().canMove(0, 1);
		
		// 락 딜레이가 실행 중이고, 최대 조작 횟수를 넘지 않았다면
		if (gameEngine.isLockDelayRunning() && gameEngine.getLockDelayCounter() < 15) {
			// 바닥에 닿은 상태(더 못내려감)라면 락 타이머 시간만 초기화
			if (!canFall) {
				gameEngine.resetLockDelay();
			} else {
				// 아직 공중에 떠있는 상태(잘못된 조작으로 딜레이가 걸린 경우)면 메인 타이머 재시작
				gameEngine.restartMainTimer();
			}
			gameEngine.increaseLockDelayCounter(); // 조작 횟수 증가
		}
	}
}