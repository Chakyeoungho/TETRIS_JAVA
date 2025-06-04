package tetris.logic.tetromino;

import static tetris.data.constants.GameConstants.BUFFER_ZONE;
import static tetris.data.constants.Tetromino.EMPTY;

import java.awt.Point;

import tetris.data.constants.Tetromino;
import tetris.data.dto.DataManager;
import tetris.logic.TetrisEngine;
import tetris.logic.tetromino.spin.SpinState;

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
		var gameState = gameData.getTetrominoState();
		var tetState = gameData.getTetrominoState();
        Tetromino current = gameState.getCurrentTetromino();

		if (isHoldUsed) {
			return;
		} else {
			if (heldTetromino == EMPTY) {
				heldTetromino = gameState.getCurrentTetromino();
				gameEngine.getTetrominoGenerator().generateTetromino();
			} else {
	            // 스왑
	            Tetromino temp = heldTetromino;
	            heldTetromino = current;
	            gameState.setCurrentTetromino(temp);
	            tetState.setTetrominoOffset(new Point(3, BUFFER_ZONE - 1));
	            tetState.setTetrominoCoords(tetState.getCurrentTetromino().getBlocks());
	            gameData.getTetrominoState().setSpinState(SpinState.S0);
			}

            gameEngine.getTetrominoMover().drop();
			isHoldUsed = true;
		}
	}
}
