// 변수 정의만 분리한 클래스
package tetris.data.dto;

import static tetris.data.constants.GameConstants.BUFFER_ZONE;
import static tetris.data.constants.GameConstants.FIELD_Y_COUNT;
import static tetris.data.constants.GameConstants.GAME_PROCESS;

public class GameState {
    // --- 게임 진행 상태 ---
    private int gameStateCode = GAME_PROCESS;

    // --- Getters & Setters ---

    // 게임 진행 상태
    public int getGameStateCode() { return gameStateCode; }
    public void setGameStateCode(int gameStateCode) { this.gameStateCode = gameStateCode; }
}
