package tetris.logic.tetromino;

import static tetris.data.constant.GameConstants.BUFFER_ZONE;
import static tetris.data.constant.Tetromino.TETROMINO_TYPE_COUNT;

import java.awt.Point;

import tetris.data.constant.GameConstants.GameStateCode;
import tetris.data.constant.SpinState;
import tetris.data.constant.Tetromino;
import tetris.data.dto.TetrominoState;
import tetris.logic.TetrisEngine;
import tetris.logic.data.DataManager;

/** 7-Bag 시스템을 기반으로 새로운 테트로미노를 생성하고 필드에 배치. */
public class TetrominoGenerator {

    // --- Instance Fields ---
    private final DataManager gameData;
    private final TetrisEngine gameEngine;
    // 현재 가방(Bag)에서 몇 번째 조각을 사용할 차례인지 가리키는 인덱스
    private int currentPocketIndex = TETROMINO_TYPE_COUNT;

    // --- Constructor ---
    public TetrominoGenerator(DataManager gameData, TetrisEngine gameEngine) {
        this.gameData = gameData;
        this.gameEngine = gameEngine;
    }

    // --- Public Methods ---
    /** 새로운 테트로미노를 생성하여 필드에 배치하는 메인 메서드. */
    public synchronized void generateTetromino() {
    	initData(); // 새 블록 데이터 초기화
        var tetState = gameData.getTetrominoState();
        // 블록의 초기 모양(좌표) 설정
    	tetState.setTetrominoCoords(tetState.getCurrentTetromino().getMinos());
    }
    
    /** 새 블록을 생성하기 위한 데이터 초기화 및 배치 준비. */
    public void initData() {
        advancePocketIndex(); // 다음 블록을 가리키도록 인덱스 이동
        updateCurrentTetromino(); // 인덱스에 맞는 블록을 현재 테트로미노로 설정
        gameEngine.getSpin().setSpinState(SpinState.S0); // 회전 상태 초기화
        gameEngine.getHoldHandler().resetIsHoldUsed(); // 홀드 사용 가능하도록 리셋
    }

    /** 디버깅용: 현재 생성기 및 가방 상태를 콘솔에 출력. */
    public void dumpState() {
        // ... (내용은 동일)
    }
    
    public int getCurrentPocketIndex() { return currentPocketIndex; }
    public void resetCurrentPocketIndex() { currentPocketIndex = TETROMINO_TYPE_COUNT; }
    
    // --- Private Methods ---
    /** 다음 블록을 꺼내기 위해 가방(pocket) 인덱스를 1 증가. 가방이 비면 새로 채움. */
    private void advancePocketIndex() {
        if (++currentPocketIndex >= TETROMINO_TYPE_COUNT) {
            gameData.advanceBag(); // 현재 가방을 비우고 다음 가방을 가져옴
            currentPocketIndex = 0; // 새 가방의 첫 번째 블록부터 시작
        }
    }

    /** 인덱스에 해당하는 테트로미노를 현재 조작 블록으로 설정하고 필드에 배치. */
    private void updateCurrentTetromino() {
    	TetrominoState tetState = gameData.getTetrominoState();
        Tetromino[][] pocket = gameData.getBagCopy();
        Tetromino next = pocket[0][currentPocketIndex];

        // 방어 코드: 비정상적으로 EMPTY 블록이 나온 경우 복구 시도
        if (next == Tetromino.EMPTY) {
            System.err.println("Pocket에서 EMPTY Tetromino가 나왔음! 복구 시도 중...");
            advancePocketIndex();
            next = gameData.getBagCopy()[0][currentPocketIndex];
        }

        tetState.setCurrentTetromino(next);
        // 블록을 필드 상단 시작 위치로 설정
        tetState.setTetrominoOffset(new Point(3, BUFFER_ZONE - 3));
        
        // 게임 오버(Lock Out) 판정: 새 블록이 시작 위치에 놓일 수 없다면 게임 종료
        if (!gameEngine.getCollisionChecker().canPlace(tetState.getTetrominoCoords(), tetState.getTetrominoOffset())) {
        	gameData.getGameState().setCurrentState(GameStateCode.GAME_OVER);
        }
        
        // 생성 후 바로 한 칸 드롭
        gameEngine.getTetrominoMover().drop();
    }
}