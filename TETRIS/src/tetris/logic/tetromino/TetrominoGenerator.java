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

public class TetrominoGenerator {
    private int currentPocketIndex = (byte) (TETROMINO_TYPE_COUNT);
    private final DataManager gameData;
    private final TetrisEngine gameEngine;

    public TetrominoGenerator(DataManager gameData, TetrisEngine gameEngine) {
        this.gameData = gameData;
        this.gameEngine = gameEngine;
    }

    private void advancePocketIndex() {
        if (++currentPocketIndex >= TETROMINO_TYPE_COUNT) {
            gameData.advanceBag();
            currentPocketIndex = 0;
        }
    }

    private void updateCurrentTetromino() {
    	TetrominoState tetState = gameData.getTetrominoState();
        Tetromino[][] pocket = gameData.getBagCopy();

        if (pocket.length == 0 || pocket[0].length != TETROMINO_TYPE_COUNT) {
            throw new IllegalStateException("Pocket 구조가 비정상입니다.");
        }

        Tetromino next = pocket[0][currentPocketIndex];

        if (next == Tetromino.EMPTY) {
            System.err.println("Pocket에서 EMPTY Tetromino가 나왔음! 복구 시도 중...");
            gameData.advanceBag();
            currentPocketIndex = 0;
            next = gameData.getBagCopy()[0][currentPocketIndex];
        }

        tetState.setCurrentTetromino(next);
        tetState.setTetrominoOffset(new Point(3, BUFFER_ZONE - 3));
        
        if (!gameEngine.getCollisionChecker().canPlace(tetState.getTetrominoCoords(), tetState.getTetrominoOffset()))
        	gameData.getGameState().setCurrentState(GameStateCode.GAME_OVER);
        
        gameEngine.getTetrominoMover().drop();
    }

    public void initData() {
        advancePocketIndex();
        updateCurrentTetromino();
        gameEngine.getSpin().setSpinState(SpinState.S0);
        gameEngine.getHoldHandler().resetIsHoldUsed();
    }

    public synchronized void generateTetromino() {
    	TetrominoState tetState = gameData.getTetrominoState();
    	
        initData();
        tetState.setTetrominoCoords(tetState.getCurrentTetromino().getBlocks());
    }

    public void dumpState() {
        System.out.println("=== TetrominoGenerator 상태 덤프 ===");
        System.out.println("currentPocketIndex: " + currentPocketIndex);
        Tetromino[][] pockets = gameData.getBagCopy();
        for (int i = 0; i < pockets.length; i++) {
            System.out.print("Pocket[" + i + "]: ");
            for (Tetromino t : pockets[i]) {
                System.out.print(t + " ");
            }
            System.out.println();
        }
        System.out.println("현재 테트로미노: " + gameData.getTetrominoState().getCurrentTetromino());
    }
    
    public int getCurrentPocketIndex() { return currentPocketIndex; }
    public void resetCurrentPocketIndex() { currentPocketIndex = TETROMINO_TYPE_COUNT; }
}
