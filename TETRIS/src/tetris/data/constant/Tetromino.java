package tetris.data.constant;

import java.awt.Point;

public enum Tetromino {
    I(new Point[] { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) }),
    J(new Point[] { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) }),
    L(new Point[] { new Point(2, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) }),
    O(new Point[] { new Point(1, 0), new Point(2, 0), new Point(1, 1), new Point(2, 1) }),
    S(new Point[] { new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) }),
    T(new Point[] { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) }),
    Z(new Point[] { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) }),
    EMPTY(new Point[0]);

	public static final int TETROMINO_TYPE_COUNT = values().length - 1;
    private final Point[] minos;

    Tetromino(Point[] mino) {
        this.minos = mino;
    }

    public Point[] getMinos() {
        Point[] copy = new Point[minos.length];
        for (int i = 0; i < minos.length; i++) {
            copy[i] = new Point(minos[i]);
        }
        return copy;
    }

    public static Tetromino fromOrdinal(int i) {
        if (i < 0 || i >= values().length) return EMPTY;
        return values()[i];
    }
}