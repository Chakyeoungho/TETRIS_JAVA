package tetris.data.dto;

import tetris.data.constant.GameConstants.GameStateCode;

public class GameState {
    private GameStateCode currentState = GameStateCode.READY;

    public void togglePause() {
        if (currentState == GameStateCode.PLAYING) {
            currentState = GameStateCode.PAUSED;
        } else if (currentState == GameStateCode.PAUSED) {
            currentState = GameStateCode.PLAYING;
        }
    }

    public GameStateCode getCurrentState() { return currentState; }
    public void setCurrentState(GameStateCode state) { this.currentState = state; }
    
    public boolean isPaused() { return currentState == GameStateCode.PAUSED; }
    public void pauseToggle() {
    	if (currentState == GameStateCode.PAUSED) currentState = GameStateCode.PLAYING;
    	else currentState = GameStateCode.PAUSED;
    }
}
