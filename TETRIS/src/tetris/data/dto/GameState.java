package tetris.data.dto;

import tetris.data.constant.GameConstants.GameStateCode;

/** 게임의 현재 상태(READY, PLAYING 등)를 관리하는 데이터 객체 */
public class GameState {
    private GameStateCode currentState = GameStateCode.READY;

    /** 현재 게임 상태를 반환 */
    public GameStateCode getCurrentState() { return currentState; }
    /** 현재 게임 상태를 설정 */
    public void setCurrentState(GameStateCode state) { this.currentState = state; }
    
    /** 게임이 일시정지 상태인지 확인 */
    public boolean isPaused() { return currentState == GameStateCode.PAUSED; }

    /** 
     * 일시정지 상태를 토글.
     * PLAYING -> PAUSED, PAUSED -> PLAYING 으로 전환.
     * 다른 상태(READY, GAME_OVER)에서는 아무 동작도 하지 않음.
     */
    public void togglePause() {
        if (currentState == GameStateCode.PLAYING) {
            currentState = GameStateCode.PAUSED;
        } else if (currentState == GameStateCode.PAUSED) {
            currentState = GameStateCode.PLAYING;
        }
    }
}