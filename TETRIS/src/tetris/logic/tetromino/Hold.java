package tetris.logic.tetromino;

import tetris.data.constants.Tetromino;
import tetris.data.model.DataManager;

public class Hold {
	private Tetromino hold;
	private final DataManager manager;
	
	Hold(DataManager manager) {
		this.manager = manager;
	}
}
