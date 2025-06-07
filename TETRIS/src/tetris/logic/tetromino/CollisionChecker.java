package tetris.logic.tetromino;

import static tetris.data.constant.Tetromino.EMPTY;

import java.awt.Point;

import tetris.data.constant.GameConstants;

/**
 * 테트로미노의 충돌 여부(벽, 다른 블록)를 판단하는 역할.
 * CellReader 인터페이스를 통해 실제 필드 데이터와 분리되어 독립적으로 동작.
 */
public class CollisionChecker {
    
    // --- Inner Interface ---
    /**
     * 필드 데이터를 읽어오는 기능을 추상화한 인터페이스.
     * 이를 통해 CollisionChecker는 PlayField에 직접 의존하지 않고,
     * 유연한 테스트와 확장이 가능해짐 (의존성 역전 원칙).
     */
    public interface CellReader {
        int getCell(int y, int x);
    }

    // --- Instance Fields ---
    private final CellReader reader; // 필드 데이터를 읽어올 reader 객체

    // --- Constructor ---
    public CollisionChecker(CellReader reader) {
        this.reader = reader;
    }

    // --- Public Methods ---
    /**
     * 주어진 좌표 배열(coords)을 특정 오프셋(offset)에 배치할 수 있는지 검사.
     * @param coords 테트로미노를 구성하는 4개 미노의 상대 좌표
     * @param offset 블록의 현재 기준 위치
     * @return 배치 가능하면 true, 불가능하면 false
     */
    public boolean canPlace(Point[] coords, Point offset) {
        for (Point p : coords) {
            int y = p.y + offset.y;
            int x = p.x + offset.x;

            // 필드 경계를 벗어나거나, 해당 셀이 비어있지 않으면 충돌로 간주
            if (!isInBounds(y, x) || !isCellFree(y, x)) {
                return false;
            }
        }
        return true; // 모든 셀이 유효한 위치에 있으면 배치 가능
    }
    
    // --- Private Methods ---
    /** 좌표가 게임 필드 경계 내에 있는지 확인. */
    private boolean isInBounds(int y, int x) {
        return y >= 0 &&
               y < GameConstants.FIELD_Y_COUNT + GameConstants.BUFFER_ZONE &&
               x >= 0 &&
               x < GameConstants.FIELD_X_COUNT;
    }

    /** 해당 좌표의 셀이 비어있는지 확인. */
    private boolean isCellFree(int y, int x) {
        // reader를 통해 필드 데이터를 읽어와 EMPTY 블록인지 비교
        return reader.getCell(y, x) == EMPTY.ordinal();
    }
}