package tetris.logic.tetromino;

import static tetris.data.constants.Tetromino.EMPTY;

import tetris.data.constants.Tetromino;
import tetris.data.dto.DataManager;
import tetris.logic.TetrisEngine;

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
	
	public void hold() {
		var state = gameData.getTetrominoState();
		
		if (isHoldUsed) {
			return;
		} else {
			heldTetromino = state.getCurrentTetromino();
			gameEngine.getTetrominoGenerator().generateTetromino();
			isHoldUsed = true;
		}
	}
}
