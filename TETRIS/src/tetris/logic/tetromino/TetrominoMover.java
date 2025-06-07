package tetris.logic.tetromino;

import static tetris.data.constant.GameConstants.BUFFER_ZONE;

import java.awt.Point;

import tetris.data.constant.GameConstants.GameStateCode;
import tetris.data.constant.Tetromino;
import tetris.logic.TetrisEngine;
import tetris.logic.data.DataManager;

/** 테트로미노의 이동(좌, 우, 하강) 및 필드에 고정(Lock)하는 로직을 담당. */
public class TetrominoMover {

    // --- Instance Fields ---
	private final DataManager gameData;
	private final TetrisEngine gameEngine;
	private final CollisionChecker checker;
	
	// 하드 드롭 시 이동한 칸 수 (점수 계산용)
	private int droppedCells = 0;

    // --- Constructor ---
	public TetrominoMover(DataManager gameData, TetrisEngine gameEngine) {
		this.gameData = gameData;
		this.gameEngine = gameEngine;
		this.checker = gameEngine.getCollisionChecker();
	}

    // --- Public Methods ---
	/** 한 칸 아래로 이동. */
	public synchronized void drop() { move(0, 1); }
	/** 한 칸 왼쪽으로 이동. */
	public synchronized void left() { move(-1, 0); }
	/** 한 칸 오른쪽으로 이동. */
	public synchronized void right() { move(1, 0); }

	/** 블록을 가능한 가장 아래까지 즉시 내리고 고정. */
	public synchronized void hardDrop() {
		Point dropOffset = getHardDropOffset();
		gameData.getTetrominoState().setTetrominoOffset(dropOffset);
		// 블록을 필드에 즉시 고정
		lockTetromino();
	}

	/** 현재 테트로미노를 필드에 영구적으로 고정. 후속 처리(줄 제거, 점수 계산 등)를 트리거. */
	public synchronized void lockTetromino() {
		var cascadeHandler = gameEngine.getCascadeHandler();
		var scoreManager = gameEngine.getScoreManager();
		Point[] coords = gameData.getTetrominoState().getTetrominoCoords();
		Point offset = gameData.getTetrominoState().getTetrominoOffset();
		
		// 1. 블록을 필드 배열에 기록
		placeTetrominoOnField(coords, offset);
		updateRowBlockCounts(coords, offset);
		
		// 2. 점수 및 필드 관련 후속 로직 실행
		scoreManager.setSpinCornerStatus(); // T-Spin 판정을 위한 코너 상태 기록
		cascadeHandler.cascade();           // 줄 제거 및 중력 적용
		scoreManager.updateScore(cascadeHandler.getClearedLine()); // 점수 갱신
		
		// 3. 타이머 리셋 및 다음 턴 준비
		gameEngine.resetLockDelayCounter(); // 락 딜레이 타이머 중지/리셋
		// (참고) 보통 이 시점에서 다음 블록을 생성하는 gameEngine.getTetrominoGenerator().generateTetromino() 호출이 필요.

		// 4. 게임 오버(Block Out) 판정: 블록이 완전히 필드 밖에서 고정되면 게임 종료
		int countLockOutMino = 0;
		for (Point p : coords) {
			if (p.y + offset.y < BUFFER_ZONE) countLockOutMino++;
		}
		if (countLockOutMino == 4) {
			gameData.getGameState().setCurrentState(GameStateCode.GAME_OVER);
		}
	}

	/** 하드 드롭 시 최종적으로 도착할 위치를 계산하여 반환. */
	public Point getHardDropOffset() {
		Point[] coords = gameData.getTetrominoState().getTetrominoCoords();
		Point offset = gameData.getTetrominoState().getTetrominoOffset();
		Point testOffset = new Point(offset);
		droppedCells = 0;

		// 한 칸씩 아래로 내리면서 충돌 지점을 찾음
		while (true) {
			testOffset.y++;
			if (!checker.canPlace(coords, testOffset)) {
				testOffset.y--; // 충돌 직전 위치로 복원
				break;
			}
			droppedCells++;
		}
		return testOffset;
	}

	/** 특정 방향으로 이동이 가능한지 확인. */
	public boolean canMove(int dx, int dy) {
		Point[] coords = gameData.getTetrominoState().getTetrominoCoords();
		Point offset = gameData.getTetrominoState().getTetrominoOffset();
		Point testOffset = new Point(offset.x + dx, offset.y + dy);

		return checker.canPlace(coords, testOffset);
	}
	
	/** 마지막 하드드롭 시 이동한 칸 수를 반환. */
	public int getHardDroppedCells() { return droppedCells; }

    // --- Private Methods ---
	/** 블록의 기준 좌표(offset)를 dx, dy만큼 이동 (충돌 검사 없음). */
	private void translateOffset(int dx, int dy) {
		var state = gameData.getTetrominoState();
		Point offset = state.getTetrominoOffset();
		state.setTetrominoOffset(new Point(offset.x + dx, offset.y + dy));
	}

	/** 충돌 검사 후 안전하게 블록을 이동시키는 범용 메서드. */
	private void move(int dx, int dy) {
		if (canMove(dx, dy)) {
			translateOffset(dx, dy);
		}
	}

	/** 테트로미노의 4개 미노를 실제 필드 배열에 기록(burn). */
	private void placeTetrominoOnField(Point[] coords, Point offset) {
		Tetromino current = gameData.getTetrominoState().getCurrentTetromino();
		for (Point coord : coords) {
			int y = coord.y + offset.y;
			int x = coord.x + offset.x;
			gameData.setCell(y, x, current);
		}
	}

	/** 블록이 놓인 각 줄의 블록 카운트를 증가시켜, 줄 완성 검사를 용이하게 함. */
	private void updateRowBlockCounts(Point[] coords, Point offset) {
		for (Point coord : coords) {
			int y = coord.y + offset.y;
			gameData.getPlayField().incrementRowBlockCount(y);
		}
	}
}