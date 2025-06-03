// 변수 정의만 분리한 클래스
package tetris.data.dto;

import static tetris.data.constants.GameConstants.BUFFER_ZONE;
import static tetris.data.constants.GameConstants.FIELD_Y_COUNT;
import static tetris.data.constants.GameConstants.GAME_PROCESS;

public class GameState {
    // --- 게임 진행 상태 ---
    private int gameStateCode = GAME_PROCESS;
    private int level = 1;

    // --- 점수 관련 ---
    private long score = 0;
    private int totalClearedLine = 0;
    
    // --- 줄 정보 ---
    private int[] rowBlockCounts = new int[BUFFER_ZONE + FIELD_Y_COUNT];

    // --- Getters & Setters ---

    // 게임 진행 상태
    public int getGameStateCode() { return gameStateCode; }
    public void setGameStateCode(int gameStateCode) { this.gameStateCode = gameStateCode; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    // 점수 관련
    public long getScore() { return score; }
    public void setScore(long score) { this.score = score; }

	public int getTotalClearedLine() { return totalClearedLine; }
	public void increaseTotalClearedLine() { this.totalClearedLine++; }
	public void resetTotalClearedLine() { this.totalClearedLine = 0; }

    // --- 줄 정보 ---
    public int[] getRowBlockCount() { return rowBlockCounts; }
    public void incrementRowBlockCount(int y) { rowBlockCounts[y]++; }
    public void shiftDownRowBlockCount(int y, int clearedLine) {
        int targetIndex = y + clearedLine;
        if (targetIndex >= 0 && targetIndex < rowBlockCounts.length) {
            rowBlockCounts[targetIndex] = rowBlockCounts[y];
            rowBlockCounts[y] = 0;
        }
    }

}
