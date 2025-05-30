package tetris.logic.tetromino.spin;

//회전 상태를 나타내는 enum
public enum SpinState {
	S0, S1, S2, S3, UNKNOWN;
	
    public static SpinState clockwise(SpinState state) {
        if (state == null || state == UNKNOWN) return UNKNOWN;
        return values()[(state.ordinal() + 1) % (values().length - 1)];  // UNKNOWN 제외
    }

    public static SpinState counterClockwise(SpinState state) {
        if (state == null || state == UNKNOWN) return UNKNOWN;
        return values()[(state.ordinal() + values().length - 2) % (values().length - 1)];  // UNKNOWN 제외
    }
	
	public static SpinState fromOrdinal(int spin) {
	    if (spin < 0 || spin >= values().length) return UNKNOWN;
	    return values()[spin];
	}
}