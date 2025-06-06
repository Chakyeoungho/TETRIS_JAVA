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
    private final Point[] blocks;

    Tetromino(Point[] blocks) {
        this.blocks = blocks;
    }

    public Point[] getBlocks() {
        Point[] copy = new Point[blocks.length];
        for (int i = 0; i < blocks.length; i++) {
            copy[i] = new Point(blocks[i]);
        }
        return copy;
    }

    public static Tetromino fromOrdinal(int i) {
        if (i < 0 || i >= values().length) return EMPTY;
        return values()[i];
    }
}