package tetris.data.constant;

import java.awt.Point;

/**
 * 테트로미노의 4가지 회전 상태(0, 90, 180, 270도)와
 * T-Spin 판정에 사용될 모서리(Corner) 좌표 데이터를 정의.
 */
public enum SpinState {
    // T-Spin 판정을 위해, T-블록 기준 3x3 영역의 4개 모서리(A:좌상, B:우상, C:좌하, D:우하) 좌표를 정의.
    S0(new Point[] { new Point(0, 0), new Point(2, 0), new Point(0, 2), new Point(2, 2) }), // 0도
    S1(new Point[] { new Point(2, 0), new Point(2, 2), new Point(0, 0), new Point(0, 2) }), // 90도
    S2(new Point[] { new Point(2, 2), new Point(0, 2), new Point(2, 0), new Point(0, 0) }), // 180도
    S3(new Point[] { new Point(0, 2), new Point(0, 0), new Point(2, 2), new Point(2, 0) }), // 270도
    UNKNOWN(new Point[0]); // 예외 처리용

    // 각 회전 상태에 대응하는 모서리 좌표 오프셋
    private final Point[] cornerOffsets;

    SpinState(Point[] cornerOffsets) {
        this.cornerOffsets = cornerOffsets;
    }

    /** 현재 회전 상태의 모서리 좌표 오프셋 배열을 반환 (방어적 복사) */
    public Point[] getCornerOffsets() {
        return cornerOffsets.clone();
    }

    /** 시계 방향 회전 */
    public static SpinState clockwise(SpinState state) {
        if (state == null || state == UNKNOWN) return UNKNOWN;
        return values()[(state.ordinal() + 1) % 4]; // (0->1, 1->2, 2->3, 3->0)
    }

    /** 반시계 방향 회전 */
    public static SpinState counterClockwise(SpinState state) {
        if (state == null || state == UNKNOWN) return UNKNOWN;
        return values()[(state.ordinal() + 3) % 4]; // (1->0, 0->3, 3->2, 2->1)
    }

    /** 숫자(0~3)로 회전 상태 가져오기 */
    public static SpinState fromOrdinal(int spin) {
        if (spin < 0 || spin >= 4) return UNKNOWN;
        return values()[spin];
    }
}