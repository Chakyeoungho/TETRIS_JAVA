package tetris.data.constant;

import java.awt.Point;
import java.util.List;
import java.util.Map;

/**
 * SRS(Super Rotation System)의 '월킥(Wall Kick)' 데이터.
 * 블록 회전 시 벽/바닥에 막히면 위치를 보정해주는 좌표값들.
 */
public final class WallKickData {
    private WallKickData() {
        throw new AssertionError("Utility class");
    }

    // 오프셋 리스트를 담는 내부용 데이터 홀더
    public static class WallKickOffsets {
        private final List<Point> offsets;

        public WallKickOffsets(List<Point> offsets) {
            this.offsets = offsets;
        }

        public List<Point> getOffsets() {
            return offsets;
        }
    }

    // 월킥 테스트 순서: (0,0) 포함 5개 위치를 순서대로 테스트.
    // Point(x, y)는 (현재위치.x + x, 현재위치.y + y)로의 이동을 의미.

    // I 블록 전용 월킥 데이터
    private static final Map<SpinState, WallKickOffsets> iOffsets = Map.of(
        SpinState.S0, new WallKickOffsets(List.of(new Point(0, 0), new Point(-2, 0), new Point( 1, 0), new Point(-2,  1), new Point( 1, -2))), // 0->1
        SpinState.S1, new WallKickOffsets(List.of(new Point(0, 0), new Point(-1, 0), new Point( 2, 0), new Point(-1, -2), new Point( 2,  1))), // 1->2
        SpinState.S2, new WallKickOffsets(List.of(new Point(0, 0), new Point( 2, 0), new Point(-1, 0), new Point( 2, -1), new Point(-1,  2))), // 2->3
        SpinState.S3, new WallKickOffsets(List.of(new Point(0, 0), new Point( 1, 0), new Point(-2, 0), new Point( 1,  2), new Point(-2, -1)))  // 3->0
    );

    // I, O를 제외한 나머지 블록(J, L, S, T, Z) 공용 월킥 데이터
    private static final Map<SpinState, WallKickOffsets> commonOffsets = Map.of(
        SpinState.S0, new WallKickOffsets(List.of(new Point(0, 0), new Point(-1, 0), new Point(-1, -1), new Point(0,  2), new Point(-1,  2))), // 0->1
        SpinState.S1, new WallKickOffsets(List.of(new Point(0, 0), new Point( 1, 0), new Point( 1,  1), new Point(0, -2), new Point( 1, -2))), // 1->2
        SpinState.S2, new WallKickOffsets(List.of(new Point(0, 0), new Point( 1, 0), new Point( 1, -1), new Point(0,  2), new Point( 1,  2))), // 2->3
        SpinState.S3, new WallKickOffsets(List.of(new Point(0, 0), new Point(-1, 0), new Point(-1,  1), new Point(0, -2), new Point(-1, -2)))  // 3->0
    );

    // 블록 종류별 월킥 데이터 매핑
    private static final Map<Tetromino, Map<SpinState, WallKickOffsets>> wallKickMap = Map.of(
        Tetromino.I, iOffsets,
        Tetromino.J, commonOffsets,
        Tetromino.L, commonOffsets,
        Tetromino.O, Map.of(),  // O 블록은 월킥 없음
        Tetromino.S, commonOffsets,
        Tetromino.T, commonOffsets,
        Tetromino.Z, commonOffsets
    );

    /**
     * 블록 종류와 회전 *전* 상태에 맞는 월킥 오프셋 리스트를 반환.
     *
     * @param type 테트로미노 종류
     * @param rotation 현재 회전 상태 (회전 시도 전)
     * @return List<Point> 월킥 오프셋 좌표 목록
     */
    public static List<Point> getWallKickOffsets(Tetromino type, SpinState rotation) {
        Map<SpinState, WallKickOffsets> rotationMap = wallKickMap.get(type);
        if (rotationMap == null || !rotationMap.containsKey(rotation)) {
            // 데이터가 없으면(e.g. O블록) 빈 리스트 반환
            return List.of();
        }
        return rotationMap.get(rotation).getOffsets();
    }
}