package tetris.logic.tetromino.spin;

import java.awt.Point;
import java.util.List;
import java.util.Map;

import tetris.data.constants.Tetromino;

public final class WallKickData {
    private WallKickData() {
        throw new AssertionError("Utility class");
    }

    // 회전 시 적용할 오프셋 목록을 담는 클래스
    public static class WallKickOffsets {
        private final List<Point> offsets;

        public WallKickOffsets(List<Point> offsets) {
            this.offsets = offsets;
        }

        public List<Point> getOffsets() {
            return offsets;
        }
    }

    // I 테트로미노용 회전별 오프셋
    private static final Map<Spin, WallKickOffsets> iOffsets = Map.of(
        Spin.S0, new WallKickOffsets(List.of(new Point(0, 0), new Point(-2, 0), new Point( 1, 0), new Point(-2,  1), new Point( 1, -2))),
        Spin.S1, new WallKickOffsets(List.of(new Point(0, 0), new Point(-1, 0), new Point( 2, 0), new Point(-1, -2), new Point( 2,  1))),
        Spin.S2, new WallKickOffsets(List.of(new Point(0, 0), new Point( 2, 0), new Point(-1, 0), new Point( 2, -1), new Point(-1,  2))),
        Spin.S3, new WallKickOffsets(List.of(new Point(0, 0), new Point( 1, 0), new Point(-2, 0), new Point( 1,  2), new Point(-2, -1)))
    );

    // 공통 테트로미노(J, L, S, T, Z)용 회전별 오프셋
    private static final Map<Spin, WallKickOffsets> commonOffsets = Map.of(
        Spin.S0, new WallKickOffsets(List.of(new Point(0, 0), new Point(-1, 0), new Point(-1, -1), new Point(0,  2), new Point(-1,  2))),
        Spin.S1, new WallKickOffsets(List.of(new Point(0, 0), new Point( 1, 0), new Point( 1,  1), new Point(0, -2), new Point( 1, -2))),
        Spin.S2, new WallKickOffsets(List.of(new Point(0, 0), new Point( 1, 0), new Point( 1, -1), new Point(0,  2), new Point( 1,  2))),
        Spin.S3, new WallKickOffsets(List.of(new Point(0, 0), new Point(-1, 0), new Point(-1,  1), new Point(0, -2), new Point(-1, -2)))
    );

    // 테트로미노 종류별 회전 오프셋 데이터
    private static final Map<Tetromino, Map<Spin, WallKickOffsets>> wallKickMap = Map.of(
        Tetromino.I, iOffsets,
        Tetromino.J, commonOffsets,
        Tetromino.L, commonOffsets,
        Tetromino.O, Map.of(),  // O 테트로미노는 회전 시 오프셋 없음
        Tetromino.S, commonOffsets,
        Tetromino.T, commonOffsets,
        Tetromino.Z, commonOffsets
    );

    /**
     * 주어진 테트로미노와 회전 상태에 대한 벽 킥 오프셋 리스트를 반환합니다.
     *
     * @param type 테트로미노 종류
     * @param rotation 현재 회전 상태
     * @return List<Point> 벽 킥 오프셋 좌표 목록
     */
    public static List<Point> getWallKickOffsets(Tetromino type, Spin rotation) {
        Map<Spin, WallKickOffsets> rotationMap = wallKickMap.get(type);
        if (rotationMap == null || !rotationMap.containsKey(rotation)) {
            System.err.println("Warning: Missing wall kick data for " + type + " / " + rotation);
            return List.of();
        }
        return rotationMap.get(rotation).getOffsets();
    }

    public static Point getWallKickOffset(Tetromino type, Spin rotation, int index) {
        List<Point> offsets = getWallKickOffsets(type, rotation);
        if (offsets.isEmpty() || index < 0 || index >= offsets.size()) {
            System.err.println("Warning: Index out of bounds or no offsets for index: " + index);
            return null;
        }
        return offsets.get(index);
    }

}
