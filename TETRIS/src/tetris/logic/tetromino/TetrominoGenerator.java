package tetris.logic.tetromino;

import static tetris.data.constants.GameConstants.BUFFER_ZONE;
import static tetris.data.constants.Tetromino.TETROMINO_TYPE_COUNT;

import java.awt.Point;

import tetris.data.model.DataManager;
import tetris.data.model.PlayField;
import tetris.logic.tetromino.spin.SpinState;

public class TetrominoGenerator {
	private byte currentPocketIndex = (byte) (TETROMINO_TYPE_COUNT - 1);
	
	private DataManager gameData;
	
	public TetrominoGenerator(DataManager gameData) {
		this.gameData = gameData;
	}

	private void advancePocketIndex() {
		if (++currentPocketIndex >= TETROMINO_TYPE_COUNT) {
			gameData.advancePocket();
			currentPocketIndex = 0;
	    }
		updateCurrentTetromino();
	}
	
	private void updateCurrentTetromino() {
		gameData.getTetrominoState().setCurrentTetromino(gameData.getPocketCopy()[0][currentPocketIndex]);
		gameData.getTetrominoState().setTetrominoOffset(new Point(3, BUFFER_ZONE));
	}
	
	// TODO 다른거 추가
	private void initData() {
		advancePocketIndex();
		gameData.getTetrominoState().setSpinState(SpinState.S0);
	}
	
	public void generateTetromino() {
		initData();
		// TODO: 리팩터링 필요. 너무 길다.
		for (int i = 0; i < 4; i++) {
			gameData.getTetrominoState().setTetrominoCoords(gameData.getTetrominoState().getCurrentTetromino().getBlocks());
			gameData.setCell(gameData.getTetrominoState().getTetrominoCoords()[i].y + gameData.getTetrominoState().getTetrominoOffset().y, 
							gameData.getTetrominoState().getTetrominoCoords()[i].x + gameData.getTetrominoState().getTetrominoOffset().x, 
							gameData.getTetrominoState().getCurrentTetromino());
		}
	}
}
