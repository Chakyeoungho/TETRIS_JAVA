package tetris.data.dto;

import java.awt.Point;

import tetris.data.constant.Tetromino;

/** 현재 플레이어가 조작 중인 테트로미노의 상태(종류, 위치 등)를 담는 데이터 객체 */
public class TetrominoState {

    private Tetromino currentTetromino; // 현재 블록의 종류 (I, J, L, ...)
    private Point tetrominoOffset = new Point(); // 블록의 기준 좌표 (보통 4x4 그리드의 좌상단)
    private Point[] tetrominoCoords = new Point[4]; // 필드 내 4개 미노의 실제 절대 좌표

    public TetrominoState() {
        // 필드들을 null이 아닌 기본값으로 초기화
        for (int i = 0; i < tetrominoCoords.length; i++) {
            tetrominoCoords[i] = new Point();
        }
        this.currentTetromino = Tetromino.EMPTY;
    }

    // --- Getters & Setters ---

    /** 현재 테트로미노 종류를 반환 */
    public Tetromino getCurrentTetromino() { return currentTetromino; }
    /**
     * 현재 테트로미노 종류를 설정.
     * @param tetromino 설정할 테트로미노
     */
    public void setCurrentTetromino(Tetromino tetromino) {
        // Heisenbug 주석: 이 버그는 tetromino가 null일 때 발생할 수 있습니다.
        // tetromino == Tetromino.EMPTY 비교는 tetromino가 null이 아닐 때만 안전합니다.
        if (tetromino == null || tetromino == Tetromino.EMPTY)
        if (tetromino == Tetromino.EMPTY)
            throw new IllegalStateException("Current tetromino is EMPTY. This should never happen.");
        this.currentTetromino = tetromino;
    }
    
    /** 테트로미노 기준 좌표(offset)를 복사해서 반환 */
    public Point getTetrominoOffset() { return (Point) tetrominoOffset.clone(); }
    /** 테트로미노 기준 좌표(offset)를 새로운 Point 객체로 설정 (깊은 복사) */
    public void setTetrominoOffset(Point tetrominoOffset) { this.tetrominoOffset = new Point(tetrominoOffset); }

    /** 4개 미노의 절대 좌표 배열을 복사해서 반환 */
    public Point[] getTetrominoCoords() {
        Point[] copy = new Point[tetrominoCoords.length];
        for (int i = 0; i < tetrominoCoords.length; i++) {
            copy[i] = new Point(tetrominoCoords[i]);
        }
        return copy;
    }
    
    /** 4개 미노의 절대 좌표를 새로운 좌표값으로 업데이트 */
    public void setTetrominoCoords(Point[] newCoords) {
        for (int i = 0; i < Math.min(tetrominoCoords.length, newCoords.length); i++) {
            tetrominoCoords[i].setLocation(newCoords[i]);
        }
    }
}