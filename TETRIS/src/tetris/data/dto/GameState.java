package tetris.data.dto;

import tetris.data.constant.GameConstants.GameStateCode;

public class GameState {
    private GameStateCode currentState = GameStateCode.READY;

    // 일시정지상태 토글

    // 현재 회전상태 반환 밑 설정
    public GameStateCode getCurrentState() { return currentState; }
    public void setCurrentState(GameStateCode state) { this.currentState = state; }
    
    // 일시정지상태 반환 및 설정
    public boolean isPaused() { return currentState == GameStateCode.PAUSED; }
    public void togglePause() {
        if (currentState == GameStateCode.PLAYING) currentState = GameStateCode.PAUSED;
        else if (currentState == GameStateCode.PAUSED) currentState = GameStateCode.PLAYING;
    }
}
