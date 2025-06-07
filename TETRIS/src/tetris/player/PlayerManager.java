package tetris.player;

import tetris.logic.TetrisEngine;
import tetris.logic.data.DataManager;

/**
 * 게임의 핵심 컴포넌트인 DataManager와 TetrisEngine을 생성하고 관리하는 클래스.
 * 이 클래스는 전체 게임 시스템의 소유자(Owner) 역할을 합니다.
 */
public class PlayerManager {

    // --- Instance Fields ---
    // 게임의 모든 데이터를 관리하는 객체
    private final DataManager gameData;
    // 게임의 모든 로직을 총괄하는 엔진 객체
    private final TetrisEngine gameEngine;

    // --- Constructor ---
    public PlayerManager() {
        // DataManager와 TetrisEngine 인스턴스를 생성.
        // TetrisEngine은 DataManager에 의존하므로, DataManager를 먼저 생성하여 주입.
        gameData = new DataManager();
        gameEngine = new TetrisEngine(gameData);
    }

    // --- Public Methods: Getters ---
    /** DataManager 인스턴스를 반환. */
    public DataManager getDataManager() { return gameData; }
    
    /** TetrisEngine 인스턴스를 반환. */
    public TetrisEngine getGameEngine() { return gameEngine; }
}