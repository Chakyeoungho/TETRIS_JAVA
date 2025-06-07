package tetris.logic.field;

import static tetris.data.constant.GameConstants.BUFFER_ZONE;
import static tetris.data.constant.GameConstants.FIELD_X_COUNT;
import static tetris.data.constant.GameConstants.FIELD_Y_COUNT;
import static tetris.data.constant.Tetromino.EMPTY;

import tetris.data.constant.Tetromino;
import tetris.data.dto.PlayField;
import tetris.logic.data.DataManager;
import tetris.logic.scoring.ScoreManager;

/** 완성된 줄을 제거하고, 위에 있는 블록들을 아래로 내리는(cascade) 역할. */
public class CascadeHandler {

    // --- Constants ---
    private static final int TOTAL_Y_SIZE = BUFFER_ZONE + FIELD_Y_COUNT; // 필드의 전체 세로 길이

    // --- Instance Fields ---
    private final DataManager gameData;  // 게임 데이터 총괄 관리자
    private final ScoreManager gameScore; // 점수 관리자

    private int clearedLine = 0; // 한 번의 cascade 호출에서 지워진 줄의 수

    // --- Constructor ---
    public CascadeHandler(DataManager gameData, ScoreManager gameScore) {
        this.gameData = gameData;
        this.gameScore = gameScore;
    }

    // --- Public Methods ---
    /**
     * 필드를 스캔하여 완성된 줄을 지우고 블록을 아래로 내립니다.
     * 이 작업은 원자적(atomic)으로 처리되어야 하므로 synchronized로 보호됩니다.
     */
    public synchronized void cascade() {
        PlayField fieldData = gameData.getPlayField();
        clearedLine = 0; // 이번 cascade 호출에서 지워진 줄 수 초기화
        
        // 필드 맨 아래부터 위로 스캔 시작
        int y = TOTAL_Y_SIZE - 1;

        // 바닥이 비어있지 않은 가장 윗 줄까지 반복
        // (완전히 빈 줄을 만나면 그 위에는 더 이상 내릴 블록이 없으므로 종료)
        while (y >= 0 && fieldData.getRowBlockCount()[y] != 0) {
            
            // Case 1: 현재 줄(y)이 꽉 찼을 경우
            if (fieldData.getRowBlockCount()[y] == FIELD_X_COUNT) {
                clearedLine++; // 지워진 줄 수 증가
                gameScore.increaseTotalClearedLine(); // 총 지운 줄 수 갱신
            } 
            // Case 2: 현재 줄이 꽉 차지 않았고, 이전에 지워진 줄이 있는 경우
            else if (clearedLine > 0) {
                // 현재 줄(y)을 지워진 줄 수(clearedLine)만큼 아래로 내림 (중력 적용)
                for (int x = 0; x < FIELD_X_COUNT; x++) {
                    int cellValue = gameData.getCell(y, x);
                    gameData.setCell(y + clearedLine, x, Tetromino.fromOrdinal(cellValue));
                }
                // 줄 별 블록 카운트 정보도 함께 아래로 이동
                fieldData.shiftDownRowBlockCount(y, clearedLine);
            }
            y--; // 다음 윗 줄로 이동
        }

        // 루프 종료 후, 상단에 새로 생긴 빈 줄들을 정리하는 로직.
        // 예를 들어, 모든 블록이 지워진 경우 이 로직이 필드 상단을 깨끗하게 비움.
        if (clearedLine > 0) {
            // y는 현재 스캔이 멈춘 위치 (가장 처음 만난 빈 줄 또는 -1)
            // 비워야 할 최상단 줄부터 clearedLine 개수만큼의 줄을 EMPTY로 채움.
            int topRowToClear = y + clearedLine;
            for (int i = topRowToClear; i > y && i >=0; i--) {
                for (int x = 0; x < FIELD_X_COUNT; x++) {
                    gameData.setCell(i, x, EMPTY);
                }
                // 줄 별 블록 카운트도 0으로 초기화
                fieldData.shiftDownRowBlockCount(i - clearedLine, clearedLine);
            }
        }
    }

    /**
     * 가장 최근 `cascade()` 호출에서 지워진 줄의 수를 반환.
     * 콤보나 점수 계산 등 다른 로직에서 이 값을 사용.
     * @return 지워진 줄 수
     */
    public int getClearedLine() {
        return clearedLine;
    }
}