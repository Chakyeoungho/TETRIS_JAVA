package tetris.data.model;

import java.awt.Point;

import tetris.data.constants.Tetromino;
import tetris.logic.tetromino.spin.SpinState;

public class TetrominoState {
    // --- Fields ---
    // 현재 조작 중인 테트로미노 정보
    private Tetromino currentTetromino;

    // 홀드 관련

    // 위치 및 미리보기
    private final Tetromino[] tetrominoQueue = new Tetromino[6];
    private Point tetrominoOffset = new Point();
    private Point[] tetrominoCoords = new Point[4];

    // --- 생성자 ---
    TetrominoState() {
        for (int i = 0; i < tetrominoCoords.length; i++) {
            tetrominoCoords[i] = new Point();
        }
        this.currentRotationState = SpinState.S0;
        this.currentTetromino = Tetromino.EMPTY;
    }

    // --- Getters & Setters ---
    // 현재 조작 중인 테트로미노 정보
    public Tetromino getCurrentTetromino() { return currentTetromino; }
    public void setCurrentTetromino(Tetromino currTetromino) { this.currentTetromino = currTetromino; }

    private SpinState currentRotationState;
    public SpinState getCurrentRotationState() { return currentRotationState; }
    public void setRotationState(SpinState currentRotationState) { this.currentRotationState = currentRotationState; }

    private byte lastScoreAction;
    public byte getLastScoreAction() { return lastScoreAction; }
    public void setLastScoreAction(byte lastScoreAction) { this.lastScoreAction = lastScoreAction; }

    // 홀드 관련
    private Tetromino heldTetromino;
    public Tetromino getHeldTetromino() { return heldTetromino; }
    public void setHeldTetromino(Tetromino heldTetromino) { this.heldTetromino = heldTetromino; }

    private boolean isHoldUsed;
    public boolean isHoldUsed() { return isHoldUsed; }
    public void setHoldUsed(boolean isHoldUsed) { this.isHoldUsed = isHoldUsed; }

    // 위치 및 미리보기
    public Point getTetrominoOffset() { return (Point) tetrominoOffset.clone(); }
    public void setTetrominoOffset(Point tetrominoOffset) { this.tetrominoOffset = new Point(tetrominoOffset); }

    public Point[] getTetrominoCoords() {
        Point[] copy = new Point[tetrominoCoords.length];
        for (int i = 0; i < tetrominoCoords.length; i++) {
            copy[i] = new Point(tetrominoCoords[i]);
        }
        return copy;
    }
    public void setTetrominoCoords(Point[] newCoords) {
        for (int i = 0; i < Math.min(tetrominoCoords.length, newCoords.length); i++) {
            tetrominoCoords[i].setLocation(newCoords[i]);
        }
    }

    public Tetromino[] getTetrominoQueue() { return tetrominoQueue.clone(); }
    public void setTetrominoQueue(Tetromino[] newQueue) {
        System.arraycopy(newQueue, 0, tetrominoQueue, 0, Math.min(tetrominoQueue.length, newQueue.length));
    }
}
