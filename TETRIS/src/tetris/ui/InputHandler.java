package tetris.ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import tetris.Tetris;
import tetris.logic.TetrisEngine;

public class InputHandler extends KeyAdapter {
	private final TetrisEngine gameEngine;

	private boolean isDownPressed = false;

	public InputHandler(TetrisEngine gameEngine) {
		this.gameEngine = gameEngine;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if (gameEngine.isPaused()) {
			if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_F1) {
				gameEngine.togglePauseState();
				return;
			}
			return;
		}
		
		if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_X || 
				keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_Z) {
			gameEngine.getScoreManager().markLastActionAsSpin();
		} else gameEngine.getScoreManager().clearLastActionSpinFlag();

		switch (keyCode) {
//		case KeyEvent.VK_R: // 게임 재시작
//			Tetris.resetGame();
//			break;
		case KeyEvent.VK_LEFT:
			gameEngine.getTetrominoMover().left();
			resetLockDelayCounter();
			break;
		case KeyEvent.VK_RIGHT:
			gameEngine.getTetrominoMover().right();
			resetLockDelayCounter();
			break;
		case KeyEvent.VK_UP:
		case KeyEvent.VK_X:
			gameEngine.getSpin().spin(true);
			resetLockDelayCounter();
			break;
		case KeyEvent.VK_CONTROL:
		case KeyEvent.VK_Z:
			gameEngine.getSpin().spin(false);
			resetLockDelayCounter();
			break;
		case KeyEvent.VK_DOWN:
			if (isDownPressed) break; // 꾸 눌러도 처음 한번만 인식
			isDownPressed = true; // 누름 상태
			gameEngine.setIntervalNanos(gameEngine.getDropTime(gameEngine.getScoreManager().getLevel()) / 20); // 소프트드랍 시간 설정
			gameEngine.getScoreManager().softDropping(); // 소프트드랍 상태
			break;
		case KeyEvent.VK_SPACE:
			gameEngine.stopLockDelay(); // 락딜레이 멈춤
			gameEngine.getTetrominoMover().hardDrop(); // 하드드롭
			gameEngine.getScoreManager().updateHardDropScore(); // 점수 업데이트
			gameEngine.getTetrominoGenerator().generateTetromino(); // 다음테트로미노 생성
			gameEngine.restartMainTimer(); // 메인 타이머 재시작
			break;
		case KeyEvent.VK_C:
		case KeyEvent.VK_SHIFT:
			gameEngine.hold(); // 홀드
			break;
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_F1:
			gameEngine.togglePauseState(); // 일시정지 토글
			break;
		default:
			break;
		}

		gameEngine.getGameRenderer().refreshScreen();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			isDownPressed = false; // 누름 상태 취소
			gameEngine.setIntervalNanos(gameEngine.getDropTime(gameEngine.getScoreManager().getLevel())); // 메인타이머 원래시간으로 초기화
			gameEngine.getScoreManager().finishSoftDropping(); // 소프트드랍 상테 취소
		}
	}

	// 바닥에 닿은 후 락딜레이 타이머 초기화하며 움직일 수 있는 횟수
	public void resetLockDelayCounter() {
		if (gameEngine.isLockDelayRunning() && gameEngine.getLockDelayCounter() < 15
				&& !gameEngine.getTetrominoMover().canMove(0, 1)) {
			gameEngine.resetLockDelay();
			gameEngine.increaseLockDelayCounter();
		}
	}
}
