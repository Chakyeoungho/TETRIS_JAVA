package tetris.logic.field;

import static tetris.data.constant.GameConstants.BUFFER_ZONE;
import static tetris.data.constant.GameConstants.FIELD_X_COUNT;
import static tetris.data.constant.GameConstants.FIELD_Y_COUNT;
import static tetris.data.constant.Tetromino.EMPTY;

import tetris.data.constant.Tetromino;
import tetris.data.dto.PlayField;
import tetris.logic.data.DataManager;
import tetris.logic.scoring.ScoreManager;

public class CascadeHandler {
	private final DataManager gameData;
	private final ScoreManager gameScore;

	private final static int TOTAL_Y_SIZE = BUFFER_ZONE + FIELD_Y_COUNT;
	
	private int clearedLine = 0;

	public CascadeHandler(DataManager gameData, ScoreManager gameScore) {
		this.gameData = gameData;
		this.gameScore = gameScore;
	}

	public synchronized void cascade() {
		PlayField fieldData = gameData.getPlayField();
		int y = TOTAL_Y_SIZE - 1;
		clearedLine = 0;

		while (y >= 0 && fieldData.getRowBlockCount()[y] != 0) {
			if (fieldData.getRowBlockCount()[y] == FIELD_X_COUNT) {
				clearedLine++;
				gameScore.increaseTotalClearedLine();
		    } else if (clearedLine > 0 && y + clearedLine < TOTAL_Y_SIZE) {
		        for (int x = 0; x < FIELD_X_COUNT; x++) {
		            gameData.setCell(y + clearedLine, x, Tetromino.fromOrdinal(gameData.getCell(y, x)));
		        }
		        fieldData.shiftDownRowBlockCount(y, clearedLine);
		    }
			y--;
		}

		if (clearedLine > 0 && y - clearedLine >= 0) {
			for (int i = y; i > y - clearedLine; i--) {
				for (int x = 0; x < FIELD_X_COUNT; x++)
					gameData.setCell(i + clearedLine, x, EMPTY);
				gameData.getPlayField().shiftDownRowBlockCount(i, clearedLine);
			}
		}
	}
	
	public int getClearedLine() { return clearedLine; }
}
