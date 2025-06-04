package tetris.ui;

import tetris.logic.scoring.ScoreAction;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import tetris.data.dto.DataManager;
import tetris.data.dto.TetrominoState;
import tetris.logic.TetrisEngine;

public class InputHandler extends KeyAdapter {
	private final DataManager gameData;
	private final TetrisEngine gameEngine;

	private boolean isDownPressed = false;

	public InputHandler(DataManager gameData, TetrisEngine gameEngine) {
		this.gameData = gameData;
		this.gameEngine = gameEngine;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		switch (keyCode) {
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
			if (isDownPressed)
				break;
			isDownPressed = true;
			gameEngine.setIntervalNanos(gameEngine.getDropTime(gameEngine.getScoreManager().getLevel()) / 20);
			gameEngine.getScoreManager().softDropping();
			break;
		case KeyEvent.VK_SPACE:
			gameEngine.stopLockDelay();
			gameEngine.getTetrominoMover().hardDrop();
			gameEngine.getScoreManager().updateScore(ScoreAction.HARD_DROP, 0);
			gameEngine.getTetrominoGenerator().generateTetromino();
			gameEngine.setIntervalNanos(gameEngine.getDropTime(gameEngine.getScoreManager().getLevel()));
			gameEngine.restartMainTimer();
			break;
		case KeyEvent.VK_C:
		case KeyEvent.VK_SHIFT:
			gameEngine.hold();
			break;
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_F1:
			gameEngine.pauseToggle();
			break;
		default:
			break;
		}

		gameEngine.getGameRenderer().refreshScreen();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			isDownPressed = false;
			gameEngine.setIntervalNanos(gameEngine.getDropTime(gameEngine.getScoreManager().getLevel()));
			gameEngine.getScoreManager().finishSoftDropping();
		}
	}

	public void resetLockDelayCounter() {
		if (gameEngine.getLockDelayCounter() < 15 && gameEngine.isLockDelayRunning() && !gameEngine.getTetrominoMover().canMove(0, 1)) {
			gameEngine.resetLockDelay();
			gameEngine.increaseLockDelayCounter();
		}
	}
}
