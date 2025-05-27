package tetris.logic.tetromino;

import static tetris.data.constants.GameConstants.BUFFER_ZONE;
import static tetris.data.constants.Tetromino.TETROMINO_TYPE_COUNT;

import java.awt.Point;

import tetris.data.model.DataManager;
import tetris.data.model.PlayField;

public class TetrominoGenerator {
	private byte currentPocketIndex = (byte) (TETROMINO_TYPE_COUNT - 1);
	
	private DataManager manager;
	
	public TetrominoGenerator(DataManager manager) {
		this.manager = manager;
	}

	private void advancePocketIndex() {
		if (++currentPocketIndex >= TETROMINO_TYPE_COUNT) {
			manager.advancePocket();
			currentPocketIndex = 0;
	    }
		updateCurrentTetromino();
	}
	
	private void updateCurrentTetromino() {
		manager.getTetrominoState().setCurrentTetromino(manager.getPocketCopy()[0][currentPocketIndex]);
		manager.getTetrominoState().setTetrominoOffset(new Point(3, BUFFER_ZONE));
	}
	
	public void generateTetromino() {
		advancePocketIndex();
		// TODO: 리팩터링 필요. 너무 길다.
		for (int i = 0; i < 4; i++) {
			manager.getTetrominoState().setTetrominoCoords(manager.getTetrominoState().getCurrentTetromino().getBlocks());
			manager.setCell(manager.getTetrominoState().getTetrominoCoords()[i].y + manager.getTetrominoState().getTetrominoOffset().y, 
							manager.getTetrominoState().getTetrominoCoords()[i].x + manager.getTetrominoState().getTetrominoOffset().x, 
							manager.getTetrominoState().getCurrentTetromino());
		}
	}
}
