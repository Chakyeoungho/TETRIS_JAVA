package tetris.data.dto;

import static tetris.data.constant.GameConstants.BUFFER_ZONE;
import static tetris.data.constant.GameConstants.FIELD_X_COUNT;
import static tetris.data.constant.GameConstants.FIELD_Y_COUNT;

import java.util.Arrays;

import tetris.data.constant.Tetromino;

public class PlayField {
    // --- Interface ---
    // 내부 인터페이스: 플레이필드(게임판)에서 특정 좌표의 셀 값을 읽는 용도
    // (y, x) 좌표를 인자로 받아 해당 위치의 값을 반환하는 읽기 전용 인터페이스
    public interface PlayfieldReader {
        int get(int y, int x);
    }

    // --- Constant ---
    // 상수: 빈 칸을 나타내는 셀 값. Tetromino 열거형의 EMPTY 멤버의 ordinal값 사용
    private static final int EMPTY_CELL = Tetromino.EMPTY.ordinal();

    // --- Field ---
    // 인스턴스 필드: 플레이필드 2차원 배열
    // 필드 높이와 너비에 버퍼 및 라인 정보 상수를 더해 배열 크기 지정
    // 각 칸은 int로 테트로미노 종류 또는 빈 칸 상태를 저장
    private final int[][] playfield = new int[FIELD_Y_COUNT + BUFFER_ZONE][FIELD_X_COUNT];

    // 읽기 전용 람다: 외부에서 플레이필드 데이터를 읽기만 할 수 있도록 제한된 뷰 제공
    // 내부 배열을 직접 노출하지 않고 안전하게 데이터를 조회 가능
    private final PlayfieldReader readOnlyView = (y, x) -> playfield[y][x];
    
    // --- 줄 정보 ---
    private int[] rowBlockCounts = new int[BUFFER_ZONE + FIELD_Y_COUNT];

    // --- 줄 정보 ---
    public int[] getRowBlockCount() { return rowBlockCounts; }
    public void incrementRowBlockCount(int y) { rowBlockCounts[y]++; }
    public void shiftDownRowBlockCount(int y, int clearedLine) {
        int targetIndex = y + clearedLine;
        if (targetIndex >= 0 && targetIndex < rowBlockCounts.length) {
            rowBlockCounts[targetIndex] = rowBlockCounts[y];
            rowBlockCounts[y] = 0;
        }
    }
    
    // --- Constructor ---
    // 생성자: 객체 생성 시 플레이필드를 빈 칸(EMPTY_CELL)으로 초기화
    // Arrays.fill 메서드를 통해 각 행을 빈 칸 값으로 채움
    public PlayField() {
    	resetField();
    }

    // --- Public Method ---
    // 공개 메서드: 지정한 (y, x) 위치에 특정 테트로미노 종류 값을 설정
    // ordinal 값을 저장하므로 나중에 해당 위치의 테트로미노 종류를 쉽게 판별 가능
    public void setCell(int y, int x, Tetromino tetromino) {
        playfield[y][x] = tetromino.ordinal();
    }
    
    public void removeCell(int y, int x) {
    	playfield[y][x] = EMPTY_CELL;
    }
    
    public void resetField() {
        for (int y = 0; y < playfield.length; y++)
            Arrays.fill(playfield[y], EMPTY_CELL);
    }

    // 공개 메서드: 읽기 전용 뷰를 반환하여 외부에 안전한 접근 경로 제공
    public PlayfieldReader readFieldData() {
        return readOnlyView;
    }
}
