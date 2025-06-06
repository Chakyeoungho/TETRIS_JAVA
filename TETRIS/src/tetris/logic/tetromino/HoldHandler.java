package tetris.logic.tetromino;

import static tetris.data.constant.GameConstants.BUFFER_ZONE;
import static tetris.data.constant.Tetromino.EMPTY;

import java.awt.Point;

import tetris.data.constant.SpinState;
import tetris.data.constant.Tetromino;
import tetris.logic.TetrisEngine;
import tetris.logic.data.DataManager;

public class HoldHandler {
	private DataManager gameData;
	private TetrisEngine gameEngine;

	// 홀드 관련
	private Tetromino heldTetromino = EMPTY;
	private boolean isHoldUsed = false;

	public HoldHandler(DataManager gameData, TetrisEngine gameEngine) {
		this.gameData = gameData;
		this.gameEngine = gameEngine;
	}

	public Tetromino getHeldTetromino() {
		return heldTetromino;
	}

	public void resetIsHoldUsed() {
		isHoldUsed = false;
	}

	public void hold() {
		var tetState = gameData.getTetrominoState();
		Tetromino current = tetState.getCurrentTetromino();

		if (isHoldUsed) return;

		if (heldTetromino == EMPTY) {
			heldTetromino = tetState.getCurrentTetromino();
			gameEngine.getTetrominoGenerator().generateTetromino();
		} else {
			// 스왑
			Tetromino temp = heldTetromino;
			heldTetromino = current;
			tetState.setCurrentTetromino(temp);
			tetState.setTetrominoOffset(new Point(3, BUFFER_ZONE - 1));
			tetState.setTetrominoCoords(tetState.getCurrentTetromino().getBlocks());
			gameEngine.getSpin().setSpinState(SpinState.S0);
		}

		gameEngine.getTetrominoMover().drop();
		isHoldUsed = true;
	}
}
