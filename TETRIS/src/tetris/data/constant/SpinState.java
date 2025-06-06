package tetris.data.constant;

import java.awt.Point;

/**
 * 테트로미노 회전 상태를 나타내는 열거형
 */
public enum SpinState {
    // 0도 회전 상태, 각 점은 필드 내 블록의 상대 좌표 (A: 좌상, B: 우상, C: 좌하, D: 우하)
    S0(new Point[] {
        new Point(0, 0),   // A - 좌상
        new Point(0, 2),   // B - 우상
        new Point(2, 0),   // C - 좌하
        new Point(2, 2)    // D - 우하
    }),
    // 90도 시계방향 회전 상태
    S1(new Point[] {
        new Point(0, 2),   // A
        new Point(2, 2),   // B
        new Point(0, 0),   // C
        new Point(2, 0)    // D
    }),
    // 180도 회전 상태
    S2(new Point[] {
        new Point(2, 2),   // A
        new Point(2, 0),   // B
        new Point(0, 2),   // C
        new Point(0, 0)    // D
    }),
    // 270도 시계방향 (또는 90도 반시계방향) 회전 상태
    S3(new Point[] {
        new Point(2, 0),   // A
        new Point(0, 0),   // B
        new Point(2, 2),   // C
        new Point(0, 2)    // D
    }),
    // 알 수 없는 회전 상태 (예외 처리용)
    UNKNOWN(new Point[0]);

    // 각 회전 상태별로 대응하는 4개 모서리 좌표 배열
    private final Point[] cornerOffsets;

    SpinState(Point[] cornerOffsets) {
        this.cornerOffsets = cornerOffsets;
    }

    /**
     * 회전 상태의 모서리 좌표 배열을 복사본으로 반환하여 외부에서 변경할 수 없도록 함
     * @return 복사된 Point 배열
     */
    public Point[] getCornerOffsets() {
        return cornerOffsets.clone();
    }

    /**
     * 현재 상태에서 시계방향으로 한 단계 회전한 상태 반환
     * @param state 현재 SpinState
     * @return 시계방향 회전된 SpinState, 입력이 null이거나 UNKNOWN이면 UNKNOWN 반환
     */
    public static SpinState clockwise(SpinState state) {
        if (state == null || state == UNKNOWN) return UNKNOWN;
        return values()[(state.ordinal() + 1) % 4];
    }

    /**
     * 현재 상태에서 반시계방향으로 한 단계 회전한 상태 반환
     * @param state 현재 SpinState
     * @return 반시계방향 회전된 SpinState, 입력이 null이거나 UNKNOWN이면 UNKNOWN 반환
     */
    public static SpinState counterClockwise(SpinState state) {
        if (state == null || state == UNKNOWN) return UNKNOWN;
        return values()[(state.ordinal() + 3) % 4];
    }

    /**
     * 주어진 정수값에 대응하는 SpinState 반환, 유효하지 않은 값이면 UNKNOWN 반환
     * @param spin 0~3 사이 정수 (회전 상태 인덱스)
     * @return 해당 SpinState 또는 UNKNOWN
     */
    public static SpinState fromOrdinal(int spin) {
        if (spin < 0 || spin >= 4) return UNKNOWN;
        return values()[spin];
    }
}
