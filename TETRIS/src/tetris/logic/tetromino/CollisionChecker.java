package tetris.logic.tetromino;

import static tetris.data.constants.Tetromino.EMPTY;

import java.awt.Point;

import tetris.data.constants.GameConstants;

/**
 * CollisionChecker 클래스는 테트로미노의 충돌 여부를 판단하는 역할을 한다.
 * 필드 내에서 테트로미노가 유효한 위치에 놓일 수 있는지를 검사한다.
 */
public class CollisionChecker {

    /**
     * CellReader 인터페이스는 필드의 특정 위치 (y, x)의 셀 값을 읽기 위한 추상화된 메서드를 정의한다.
     * 이를 통해 충돌 검사 시 다양한 데이터 소스로부터 값을 가져올 수 있다.
     */
    public interface CellReader {
        int getCell(int y, int x);
    }

    private final CellReader reader;

    /**
     * CollisionChecker 생성자
     * @param reader 필드의 셀 상태를 읽기 위한 CellReader 구현체
     */
    public CollisionChecker(CellReader reader) {
        this.reader = reader;
    }

    /**
     * 주어진 좌표 (y, x)가 필드 내부인지 확인한다.
     * @param y Y 좌표
     * @param x X 좌표
     * @return 필드 내부면 true, 아니면 false
     */
    private boolean isInBounds(int y, int x) {
        return y >= 0 && y < GameConstants.FIELD_Y_COUNT + GameConstants.BUFFER_ZONE
            && x >= 0 && x < GameConstants.FIELD_X_COUNT;
    }

    /**
     * 주어진 블록 좌표 배열과 오프셋이 필드 내 유효한 위치인지 검사한다.
     * 충돌 여부, 필드 범위 초과 여부를 함께 확인한다.
     *
     * @param coords 테트로미노의 상대 좌표 배열 (길이 4의 Point 배열)
     * @param offset 테트로미노의 현재 위치 오프셋 (필드 기준 좌표)
     * @return 유효한 위치이면 true, 그렇지 않으면 false
     */
    public boolean canPlace(Point[] coords, Point offset) {
        for (Point p : coords) {
            int x = p.x + offset.x;
            int y = p.y + offset.y;

            // 범위를 벗어나거나 이미 채워진 셀이면 배치 불가
            if (!isInBounds(y, x) || reader.getCell(y, x) != EMPTY.ordinal()) {
                return false;
            }
        }
        return true;
    }
}
