package tetris.data.constants;

public final class GameConstants {
    private GameConstants() {
        throw new AssertionError("Cannot instantiate GameConstants");
    }

    public static final int TETROMINO_SIZE	= 35;	// 블록 크기 (픽셀)
    public static final int FIELD_X_COUNT	= 10;	// 필드 가로 블록 개수
	public static final int LINE_INFO		= 1;	// 각 줄에 대한 정보
    public static final int FIELD_Y_COUNT	= 20;	// 필드 세로 블록 개수
    public static final int BUFFER_ZONE		= 20;	// 필드 상단 숨겨진 버퍼 공간
    public static final int MAX_LOCK_RESET_COUNT = 10;	// 락 타이머 초기화 가능 최대 횟수
}
