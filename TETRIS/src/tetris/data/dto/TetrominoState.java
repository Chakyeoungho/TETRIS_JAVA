package tetris.data.dto;

import java.awt.Point;

import tetris.data.constant.Tetromino;

public class TetrominoState {
    // --- Fields ---
    // 현재 조작 중인 테트로미노 정보
    private Tetromino currentTetromino;

    // 위치 및 미리보기
    private Point tetrominoOffset = new Point();
    private Point[] tetrominoCoords = new Point[4];

    // --- 생성자 ---
    public TetrominoState() {
        for (int i = 0; i < tetrominoCoords.length; i++) {
            tetrominoCoords[i] = new Point();
        }
        this.currentTetromino = Tetromino.EMPTY;
    }

    // --- Getters & Setters ---
    // 현재 조작 중인 테트로미노 정보
    public Tetromino getCurrentTetromino() { return currentTetromino; }
    public void setCurrentTetromino(Tetromino tetromino) {
    	// Heisenbug 오휴 발생 가능! 현제 테트로미노가 EMPTY가 아닌데 EMPTY로 판정 가능
        if (tetromino == Tetromino.EMPTY)
            throw new IllegalStateException("Current tetromino is EMPTY. This should never happen.");
        this.currentTetromino = tetromino;
    }
    
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
}
