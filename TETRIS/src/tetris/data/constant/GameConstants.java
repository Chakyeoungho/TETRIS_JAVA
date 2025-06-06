package tetris.data.constant;

public final class GameConstants {
    private GameConstants() {} // 인스턴스화 방지

    public static final int TETROMINO_SIZE 		   = 30; // 블록 크기 (픽셀)
    public static final int TETROMINO_PREVIEW_SIZE = 20; // 미리보기 블록 크기 (픽셀)
    public static final int FIELD_X_COUNT 		   = 10; // 필드 가로 블록 개수
    public static final int FIELD_Y_COUNT 		   = 20; // 필드 세로 블록 개수
    public static final int BUFFER_ZONE 		   = 20; // 필드 상단 숨겨진 버퍼 공간
    public static final int MAX_LOCK_RESET_COUNT   = 10; // 락 타이머 초기화 가능 최대 횟수

    public static final int STATE_PROCESSING = 1000; // 게임 진행 중 상태
    public static final int STATE_PAUSED 	 = 1001; // 게임 일시정지 상태
    public static final int STATE_GAME_OVER  = 2000; // 게임 종료 상태
}
