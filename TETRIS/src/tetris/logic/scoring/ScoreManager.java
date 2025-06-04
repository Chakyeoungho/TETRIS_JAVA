package tetris.logic.scoring;

import tetris.data.dto.DataManager;
import tetris.logic.TetrisEngine;

public class ScoreManager {
	private final DataManager gameData;
	private final TetrisEngine gameEngine;

    private int level = 1;
    private long score = 0;
    private int totalClearedLine = 0;
    private boolean isSoftDrop = false;
    private boolean isLastActionSpin = false;
    private byte lastScoreAction;

	public ScoreManager(DataManager gameData, TetrisEngine gameEngine) {
		this.gameData = gameData;
		this.gameEngine = gameEngine;
	}
	
	public synchronized void updateScore(ScoreAction action, int clearedLine) {
		if (action == null) return; // TODO
		switch(action) {
		case HARD_DROP:
			increaseScore(gameEngine.getTetrominoMover().getHardDroppedCells() * ScoreAction.HARD_DROP.getBaseScore());
			break;
		default:
			//increaseScore(action.getBaseScore() * level);
			break;
		}
	}

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
	
    public long getScore() { return score; }
    public void increaseScore(long score) { this.score += score; }
    public void resetScore() { score = 0; }

	public int getTotalClearedLine() { return totalClearedLine; }
	public void increaseTotalClearedLine() { this.totalClearedLine++; }
	public void resetTotalClearedLine() { this.totalClearedLine = 0; }
	
	public boolean isSoftDrop() { return isSoftDrop; }
	public void softDropping() { isSoftDrop = true; }
	public void finishSoftDropping() { isSoftDrop = false; }
	
    public byte getLastScoreAction() { return lastScoreAction; }
    public void setLastScoreAction(byte lastScoreAction) { this.lastScoreAction = lastScoreAction; }
}
