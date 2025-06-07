package tetris.data.constant;

import java.awt.Point;

/** 7가지 테트로미노 종류와 초기 형태(좌표) 정의 */
public enum Tetromino {
    // 4x4 그리드 기준, 각 블록의 초기 좌표(S0 상태)
    I(new Point[] { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) }),
    J(new Point[] { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) }),
    L(new Point[] { new Point(2, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) }),
    O(new Point[] { new Point(1, 0), new Point(2, 0), new Point(1, 1), new Point(2, 1) }),
    S(new Point[] { new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) }),
    T(new Point[] { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) }),
    Z(new Point[] { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) }),
    EMPTY(new Point[0]); // 필드의 빈 칸 or 오류 처리용

	// 실제 블록 종류 수 (EMPTY 제외)
	public static final int TETROMINO_TYPE_COUNT = values().length - 1;
    private final Point[] minos; // 블록을 구성하는 4개 미노의 상대 좌표

    Tetromino(Point[] mino) {
        this.minos = mino;
    }

    /** 
     * 미노들의 좌표 배열을 반환.
     * 원본 데이터 보호를 위해 깊은 복사(deep copy)된 새 배열을 반환함.
     */
    public Point[] getMinos() {
        Point[] copy = new Point[minos.length];
        for (int i = 0; i < minos.length; i++) {
            copy[i] = new Point(minos[i]); // Point 객체도 새로 생성
        }
        return copy;
    }

    /** 숫자(0~6)로 테트로미노 가져오기 */
    public static Tetromino fromOrdinal(int i) {
        if (i < 0 || i >= TETROMINO_TYPE_COUNT) return EMPTY;
        return values()[i];
    }
}