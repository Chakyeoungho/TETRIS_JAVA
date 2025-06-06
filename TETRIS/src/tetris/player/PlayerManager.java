package tetris.player;

import tetris.logic.TetrisEngine;
import tetris.logic.data.DataManager;

public class PlayerManager {
	private final DataManager gameData;
	private final TetrisEngine gameEngine;
	
	public PlayerManager() {
		gameData = new DataManager();
		gameEngine = new TetrisEngine(gameData);
	}
	
	public DataManager getDataManager() { return gameData; }
	public TetrisEngine getGameEngine() { return gameEngine; }
}
