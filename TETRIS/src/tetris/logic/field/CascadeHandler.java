package tetris.logic.field;

import static tetris.data.constants.GameConstants.BUFFER_ZONE;
import static tetris.data.constants.GameConstants.FIELD_X_COUNT;
import static tetris.data.constants.GameConstants.FIELD_Y_COUNT;
import static tetris.data.constants.Tetromino.EMPTY;

import tetris.data.constants.Tetromino;
import tetris.data.dto.DataManager;
import tetris.data.dto.PlayField;
import tetris.logic.TetrisEngine;
import tetris.logic.scoring.ScoreManager;

public class CascadeHandler {
	private final DataManager gameData;
	private final TetrisEngine gameEngine;

	private final static int TOTAL_Y_SIZE = BUFFER_ZONE + FIELD_Y_COUNT;

	public CascadeHandler(DataManager gameData, TetrisEngine gameEngine) {
		this.gameData = gameData;
		this.gameEngine = gameEngine;
	}

	public synchronized void cascade() {
		PlayField fieldData = gameData.getPlayField();
		ScoreManager gameScore = gameEngine.getScoreManager();
		int clearedLine = 0;
		int y = TOTAL_Y_SIZE - 1;

		while (y >= 0 && fieldData.getRowBlockCount()[y] != 0) {
			if (fieldData.getRowBlockCount()[y] == FIELD_X_COUNT) {
				clearedLine++;
				gameEngine.getScoreManager().increaseTotalClearedLine();
			} else {
				if (y + clearedLine < TOTAL_Y_SIZE && clearedLine > 0) {
					for (int x = 0; x < FIELD_X_COUNT; x++)
						gameData.setCell(y + clearedLine, x, Tetromino.fromOrdinal(gameData.getCell(y, x)));
					fieldData.shiftDownRowBlockCount(y, clearedLine);
				}
			}
			y--;
		}

		if (y - clearedLine >= 0 && clearedLine > 0) {
			for (int i = y; i > y - clearedLine; i--) {
				for (int x = 0; x < FIELD_X_COUNT; x++)
					gameData.setCell(i + clearedLine, x, EMPTY);
				gameData.getPlayField().shiftDownRowBlockCount(i, clearedLine);
			}
		}

		int totalClearedLine = gameEngine.getScoreManager().getTotalClearedLine();

		if (clearedLine > 0) {
			int preLevel = gameScore.getLevel(); // 먼저 저장
			int currentLevel = Math.min(totalClearedLine / 10 + 1, 15);
			gameScore.setLevel(currentLevel);

			if (currentLevel > preLevel) {
				long nextTime = gameEngine.getDropTime(currentLevel);
				gameEngine.setIntervalNanos(nextTime);
			}
		}
		
		gameEngine.getScoreManager().updateScore(null, clearedLine);
	}
}
