package tetris.data.dto;

import static tetris.data.constant.GameConstants.BUFFER_ZONE;
import static tetris.data.constant.GameConstants.FIELD_X_COUNT;
import static tetris.data.constant.GameConstants.FIELD_Y_COUNT;

import java.util.Arrays;

import tetris.data.constant.Tetromino;

/** 테트리스 게임판(Playfield)의 상태를 저장하고 관리하는 데이터 객체 */
public class PlayField {

    // --- Static Fields ---
    // 필드에서 빈 칸을 나타내는 값 (Tetromino.EMPTY.ordinal())
    private static final int EMPTY_CELL = Tetromino.EMPTY.ordinal();

    // --- Instance Fields ---
    // 실제 게임판 데이터. [y][x] 형태의 2차원 배열. 각 칸에는 Tetromino의 ordinal 값이 저장됨.
    private final int[][] playfield = new int[FIELD_Y_COUNT + BUFFER_ZONE][FIELD_X_COUNT];
    
    // 외부에 제공될 읽기 전용 뷰. 람다식으로 구현.
    private final PlayfieldReader readOnlyView = (y, x) -> playfield[y][x];
    
    // 각 가로줄(row)에 쌓여있는 블록의 개수를 저장하는 배열. 줄 제거 판정에 사용.
    private int[] rowBlockCounts = new int[BUFFER_ZONE + FIELD_Y_COUNT];

    // --- Constructor ---
    public PlayField() {
    	resetField();
    }

    // --- Public Methods ---
    /** 각 줄의 블록 개수 배열을 반환 */
    public int[] getRowBlockCount() { return rowBlockCounts; }
    
    /** 특정 줄(y)의 블록 개수를 1 증가시킴 (블록이 쌓일 때 호출) */
    public void incrementRowBlockCount(int y) { rowBlockCounts[y]++; }
    
    /** 
     * 줄 제거 후, 위에 있던 줄의 블록 카운트 정보를 아래로 내림.
     * @param y 이동시킬 원본 줄 인덱스
     * @param clearedLine 제거된 줄의 수 (아래로 얼마나 내릴지)
     */
    public void shiftDownRowBlockCount(int y, int clearedLine) {
        int targetIndex = y + clearedLine;
        if (targetIndex >= 0 && targetIndex < rowBlockCounts.length) {
            rowBlockCounts[targetIndex] = rowBlockCounts[y];
            rowBlockCounts[y] = 0; // 원래 위치는 0으로 초기화
        }
    }
    
    /** 지정된 좌표(y, x)에 테트로미노 블록을 설정 */
    public void setCell(int y, int x, Tetromino tetromino) {
        playfield[y][x] = tetromino.ordinal();
    }
    
    /** 지정된 좌표(y, x)의 블록을 빈 칸으로 만듦 */
    public void removeCell(int y, int x) {
    	playfield[y][x] = EMPTY_CELL;
    }
    
    /** 게임판과 줄 카운트를 모두 초기 상태로 리셋 */
    public void resetField() {
        for (int y = 0; y < playfield.length; y++)
            Arrays.fill(playfield[y], EMPTY_CELL);
        rowBlockCounts = new int[BUFFER_ZONE + FIELD_Y_COUNT];
    }

    /** 외부에서 게임판 데이터를 안전하게 읽을 수 있는 읽기 전용 뷰를 반환 */
    public PlayfieldReader readFieldData() {
        return readOnlyView;
    }

    // --- Inner Interface ---
    /**
     * 외부에서 게임판 데이터를 안전하게 읽기 위한 '읽기 전용' 인터페이스.
     * 내부 배열을 직접 노출하지 않고 특정 좌표의 값만 조회하도록 함.
     */
    public interface PlayfieldReader {
        int get(int y, int x);
    }
}