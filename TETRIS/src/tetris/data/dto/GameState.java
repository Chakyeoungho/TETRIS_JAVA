// 변수 정의만 분리한 클래스
package tetris.data.dto;

import static tetris.data.constant.GameConstants.STATE_PROCESSING;

public class GameState {
    // --- 게임 진행 상태 ---
    private int gameStateCode = STATE_PROCESSING;

    private volatile boolean paused = false;

    // --- Getters & Setters ---

    // 게임 진행 상태
    public int getGameStateCode() { return gameStateCode; }
    public void setGameStateCode(int gameStateCode) { this.gameStateCode = gameStateCode; }
    
    public boolean isPaused() { return paused; }
    public void pauseToggle() { paused = !paused; }
}
