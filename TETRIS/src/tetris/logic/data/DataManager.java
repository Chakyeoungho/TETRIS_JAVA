package tetris.logic.data;

import tetris.data.constant.Tetromino;
import tetris.data.dto.GameState;
import tetris.data.dto.PlayField;
import tetris.data.dto.TetrominoBag;
import tetris.data.dto.TetrominoState;
import tetris.logic.tetromino.CollisionChecker.CellReader;

/**
 * 게임의 모든 핵심 데이터(필드, 테트로미노, 게임 상태 등)를 통합 관리하는 클래스.
 * 각 데이터 객체에 대한 접근을 중개하는 역할을 함 (Facade 역할).
 */
public class DataManager implements CellReader {

    // --- Instance Fields ---
    // 게임의 모든 상태 데이터를 담는 final 필드들.
    private final PlayField fieldData = new PlayField();          // 게임판 데이터
    private final TetrominoBag bag = new TetrominoBag();          // 테트로미노 공급 가방
    private final GameState gameState = new GameState();          // 게임 상태 (READY, PLAYING...)
    private final TetrominoState tetrominoState = new TetrominoState(); // 현재 조작 중인 테트로미노 상태

    // --- Public Methods (Getters for DTOs) ---
    /** 게임 상태(GameState) 객체를 반환. */
    public GameState getGameState() { return gameState; }
    
    /** 현재 조작 중인 테트로미노 상태(TetrominoState) 객체를 반환. */
    public TetrominoState getTetrominoState() { return tetrominoState; }
    
    /** 게임 필드(PlayField) 객체를 반환. */
    public PlayField getPlayField() { return fieldData; } 
    
    /** 테트로미노 공급 가방(TetrominoBag) 객체를 반환. */
    public TetrominoBag getTetrominoBag() { return bag; } 
    
    // --- Public Methods (Delegates for PlayField) ---
    /**
     * 게임 필드의 특정 좌표(y, x) 값을 읽어옴. (CellReader 인터페이스 구현)
     * CollisionChecker에서 필드 충돌 검사를 위해 사용.
     */
    @Override
    public int getCell(int y, int x) { 
        return fieldData.readFieldData().get(y, x); 
    }
    
    /** 게임 필드의 특정 셀에 테트로미노 값을 설정 (fieldData에 위임). */
    public void setCell(int y, int x, Tetromino tetromino) { 
        fieldData.setCell(y, x, tetromino); 
    }
    
    /** 게임 필드의 특정 셀을 빈 칸으로 만듦 (fieldData에 위임). */
    public void removeCell(int y, int x) { 
        fieldData.removeCell(y, x); 
    }
    
    /** 게임 필드를 초기화 (fieldData에 위임). */
    public void resetField() { 
        fieldData.resetField(); 
    }

    // --- Public Methods (Delegates for TetrominoBag) ---
    /** 현재/다음 테트로미노 가방의 복사본을 반환 (bag에 위임). */
    public Tetromino[][] getBagCopy() { 
        return bag.getBagCopy(); 
    }
    
    /** 다음 테트로미노 가방을 준비시킴 (bag에 위임). */
    public void advanceBag() { 
        bag.advanceBag(); 
    }
}