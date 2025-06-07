package tetris.data.constant;

/** 게임 설정 및 규칙 관련 상수 모음 */
public final class GameConstants {
    private GameConstants() {} // 인스턴스화 방지

    public static final int TETROMINO_SIZE 		   = 30; // 블록 크기 (픽셀)
    public static final int TETROMINO_PREVIEW_SIZE = 20; // 미리보기 블록 크기 (픽셀)
    public static final int FIELD_X_COUNT 		   = 10; // 필드 가로 블록 개수
    public static final int FIELD_Y_COUNT 		   = 20; // 필드 세로 블록 개수
    public static final int BUFFER_ZONE 		   = 20; // 필드 상단 숨겨진 버퍼 공간
    public static final int MAX_LOCK_RESET_COUNT   = 15; // 락 타이머 초기화 가능 최대 횟수. '무한 스핀' 방지용.
    
    // 게임 상태 enum
    public enum GameStateCode {
        READY,		// 준비
        PLAYING,	// 플레이 중
        PAUSED,		// 일시정지
        GAME_OVER	// 게임오버
    }
}