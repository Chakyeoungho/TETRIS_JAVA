package tetris.logic.tetromino.spin;

//회전 상태를 나타내는 enum
public enum Spin {
	S0, S1, S2, S3, UNKNOWN;
	
	public Spin clockwise() {
	    return values()[(this.ordinal() + 1) % values().length];
	}
	
	public Spin counterClockwise() {
	    return values()[(this.ordinal() + values().length - 1) % values().length];
	}
	
	public static Spin fromOrdinal(int spin) {
	    if (spin < 0 || spin >= values().length) return UNKNOWN;
	    return values()[spin];
	}
}