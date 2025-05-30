package tetris.logic;

import tetris.data.model.DataManager;
import tetris.logic.tetromino.CollisionChecker;
import tetris.logic.tetromino.TetrominoGenerator;
import tetris.logic.tetromino.TetrominoMover;
import tetris.logic.tetromino.spin.Spin;
import tetris.ui.GameRenderer;

public class TetrisEngine {
	private final DataManager gameData;
	
	private final CollisionChecker collisionChecker;
	private final TetrominoMover tetrominoMover;
	private final Spin spin;
	private final GameRenderer gameRenderer;
	private final TetrominoGenerator tetrominoGenerator;
	
	public TetrisEngine(DataManager gameData) {
	    this.gameData = gameData;
	    this.collisionChecker = new CollisionChecker(gameData);
	    this.tetrominoMover = new TetrominoMover(gameData, collisionChecker);
	    this.spin = new Spin(gameData, collisionChecker);
	    this.gameRenderer = new GameRenderer(gameData);
	    this.tetrominoGenerator = new TetrominoGenerator(gameData);
	}
	
    public DataManager getDataManager() { return gameData; }
    public CollisionChecker getCollisionChecker() { return collisionChecker; }
    public TetrominoMover getTetrominoMover() { return tetrominoMover; }
    public Spin getSpin() { return spin; }
    public GameRenderer getGameRenderer() { return gameRenderer; }
    public TetrominoGenerator getTetrominoGenerator() { return tetrominoGenerator; }
}
