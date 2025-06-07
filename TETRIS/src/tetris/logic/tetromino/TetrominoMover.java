package tetris.logic.tetromino;

import static tetris.data.constant.GameConstants.BUFFER_ZONE;

import java.awt.Point;

import tetris.data.constant.GameConstants.GameStateCode;
import tetris.data.constant.Tetromino;
import tetris.logic.TetrisEngine;
import tetris.logic.data.DataManager;

public class TetrominoMover {
	private final DataManager gameData;
	private final TetrisEngine gameEngine;
	private final CollisionChecker checker;
	
	private int droppedCells = 0;

	public TetrominoMover(DataManager gameData, TetrisEngine gameEngine) {
		this.gameData = gameData;
		this.gameEngine = gameEngine;
		this.checker = gameEngine.getCollisionChecker();
	}

	// ==========================
	// Public Movement Methods
	// ==========================

	public synchronized void drop() { move(0, 1); }
	public synchronized void left() { move(-1, 0); }
	public synchronized void right() { move(1, 0); }
	public synchronized void hardDrop() {
		Point dropOffset = getHardDropOffset();
		gameData.getTetrominoState().setTetrominoOffset(dropOffset);
		lockTetromino();
	}

	public synchronized void lockTetromino() {
		var cascadeHandler = gameEngine.getCascadeHandler();
		var scoreManager = gameEngine.getScoreManager();
		Point[] coords = gameData.getTetrominoState().getTetrominoCoords();
		Point offset = gameData.getTetrominoState().getTetrominoOffset();
		int countLockOutMino = 0;
		
		placeTetrominoOnField(coords, offset);
		updateRowBlockCounts(coords, offset);
		scoreManager.setSpinCornerStatus();
		cascadeHandler.cascade();
		scoreManager.updateScore(cascadeHandler.getClearedLine());
		
		gameEngine.resetLockDelayCounter();

		for (Point p : coords)
			if (p.y + offset.y < BUFFER_ZONE) countLockOutMino++;
		if (countLockOutMino == 4) gameData.getGameState().setCurrentState(GameStateCode.GAME_OVER);
	}

	public Point getHardDropOffset() {
		Point[] coords = gameData.getTetrominoState().getTetrominoCoords();
		Point offset = gameData.getTetrominoState().getTetrominoOffset();
		Point testOffset = new Point(offset);
		droppedCells = 0;

		while (true) {
			testOffset.y++;
			if (!checker.canPlace(coords, testOffset)) {
				testOffset.y--;
				break;
			}
			droppedCells++;
		}
		return testOffset;
	}

	public boolean canMove(int dx, int dy) {
		Point[] coords = gameData.getTetrominoState().getTetrominoCoords();
		Point offset = gameData.getTetrominoState().getTetrominoOffset();
		Point testOffset = new Point(offset.x + dx, offset.y + dy);

		if (!checker.canPlace(coords, testOffset))
			return false;

		return true;
	}
	
	public int getHardDroppedCells() { return droppedCells; }

	// ==========================
	// Private Movement Logic
	// ==========================

	private void translateOffset(int dx, int dy) {
		var state = gameData.getTetrominoState();
		Point offset = state.getTetrominoOffset();
		state.setTetrominoOffset(new Point(offset.x + dx, offset.y + dy));
	}

	private void move(int dx, int dy) {
		if (!canMove(dx, dy))
			return;

		translateOffset(dx, dy);
	}

	// ==========================
	// Tetromino Placement Logic
	// ==========================

	private void placeTetrominoOnField(Point[] coords, Point offset) {
		Tetromino current = gameData.getTetrominoState().getCurrentTetromino();
		for (Point coord : coords) {
			int y = coord.y + offset.y;
			int x = coord.x + offset.x;
			gameData.setCell(y, x, current);
		}
	}

	private void updateRowBlockCounts(Point[] coords, Point offset) {
		for (Point coord : coords) {
			int y = coord.y + offset.y;
			gameData.getPlayField().incrementRowBlockCount(y);
		}
	}
}
