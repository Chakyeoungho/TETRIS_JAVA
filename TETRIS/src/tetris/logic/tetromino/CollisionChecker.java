package tetris.logic.tetromino;

import static tetris.data.constant.Tetromino.EMPTY;

import java.awt.Point;

import tetris.data.constant.GameConstants;

/**
 * CollisionChecker 클래스는 테트로미노의 충돌 여부를 판단하는 역할을 한다.
 */
public class CollisionChecker {
    public interface CellReader {
        int getCell(int y, int x);
    }

    private final CellReader reader;

    public CollisionChecker(CellReader reader) {
        this.reader = reader;
    }

    private boolean isInBounds(int y, int x) {
        return y >= 0 &&
               y < GameConstants.FIELD_Y_COUNT + GameConstants.BUFFER_ZONE &&
               x >= 0 &&
               x < GameConstants.FIELD_X_COUNT;
    }

    private boolean isCellFree(int y, int x) {
        return reader.getCell(y, x) == EMPTY.ordinal();
    }

    /**
     * 블록 좌표 + 오프셋 위치에 테트로미노를 둘 수 있는지 검사
     */
    public boolean canPlace(Point[] coords, Point offset) {
        for (Point p : coords) {
            int y = p.y + offset.y;
            int x = p.x + offset.x;

            if (!isInBounds(y, x) || !isCellFree(y, x)) {
                return false;
            }
        }
        return true;
    }
}
