package tetris.logic.tetromino;

import static tetris.data.constant.WallKickData.getWallKickOffsets;

import java.awt.Point;

import tetris.data.constant.SpinState;
import tetris.data.constant.Tetromino;
import tetris.logic.TetrisEngine;
import tetris.logic.data.DataManager;

/**
 * 테트로미노 회전 및 SRS(Super Rotation System)의 월킥(Wall Kick) 적용을 담당.
 */
public class Spin {

    // --- Instance Fields ---
	private final DataManager gameData;
	private final TetrisEngine gameEngine;
	private final CollisionChecker checker;
	
	// 몇 번째 월킥 테스트에서 성공했는지 기록 (T-Spin 점수 판정용)
	private int spinPoint = 0;
	// 현재 블록의 회전 상태 (S0, S1, S2, S3)
	SpinState currentSpinState = SpinState.S0;

    // --- Constructor ---
	public Spin(DataManager gameData, TetrisEngine gameEngine) {
		this.gameData = gameData;
		this.gameEngine = gameEngine;
		checker = gameEngine.getCollisionChecker();
	}

    // --- Public Methods ---
	/**
	 * 테트로미노를 회전시킴 (O-블록 제외).
	 * @param isClockwise true면 시계방향, false면 반시계방향
	 */
	public void spin(boolean isClockwise) {
		Tetromino type = gameData.getTetrominoState().getCurrentTetromino();
		// O-블록은 회전이 의미 없으므로 중단
		if (type == Tetromino.O) return;

		// 실제 회전 및 월킥 로직 호출
		applySRSRotation(currentSpinState, isClockwise);
	}
	
	/** 마지막 회전 시 사용된 월킥 인덱스를 반환. */
	public int getSpinPoint() { return spinPoint; }
	
	/** 현재 회전 상태를 반환. */
	public SpinState getCurrentSpinState() { return currentSpinState; }
	
	/** 현재 회전 상태를 강제로 설정 (새 블록 생성 시 S0로 리셋하는 등). */
	public void setSpinState(SpinState currentRotationState) { this.currentSpinState = currentRotationState; }

    // --- Private Methods ---
	/** SRS(Super Rotation System) 월킥 규칙을 적용하여 회전을 시도. */
	private void applySRSRotation(SpinState from, boolean isClockwise) {
		var state = gameData.getTetrominoState();
		Tetromino type = state.getCurrentTetromino();
		SpinState to = isClockwise ? SpinState.clockwise(from) : SpinState.counterClockwise(from);
		
		// 1. 순수하게 회전되었을 때의 좌표를 먼저 계산
		Point[] rotatedCoords = getRotatedCoords(state.getTetrominoCoords(), type, isClockwise);
		Point offset = state.getTetrominoOffset();
		
		spinPoint = 1; // 월킥 테스트 카운터 (1부터 시작)
		// 2. 월킥 데이터에 따라 5가지 위치를 순서대로 테스트 (0,0 부터)
		for (Point kick : getWallKickOffsets(type, from)) {
			// 이동할 위치를 계산 (현재 위치 + 킥 오프셋)
			Point testOffset = new Point(offset.x + kick.x, offset.y + kick.y);
			if (checker.canPlace(rotatedCoords, testOffset)) {
				// 3. 성공: 가능한 위치를 찾으면 좌표와 오프셋, 회전 상태을 적용하고 즉시 종료
				state.setTetrominoCoords(rotatedCoords);
				state.setTetrominoOffset(testOffset);
				setSpinState(to);
				gameEngine.getScoreManager().markLastActionAsSpin();
				return;
			}
			spinPoint++; // 다음 월킥 테스트로
		}
		// 4. 실패: 5가지 위치 모두 실패하면 회전은 무효 처리
		gameEngine.getScoreManager().clearLastActionSpinFlag();
	}
    
	/** 회전 행렬을 이용해 좌표를 90도 회전. */
	private Point[] getRotatedCoords(Point[] coords, Tetromino type, boolean isClockWise) {
		Point[] rotated = new Point[coords.length];
		int size = getRotationMatrixSize(type);

		for (int i = 0; i < coords.length; i++) {
			if (isClockWise) { // 시계방향: (x, y) -> (size-1-y, x)
				rotated[i] = new Point(size - 1 - coords[i].y, coords[i].x);
			} else { // 반시계방향: (x, y) -> (y, size-1-x)
				rotated[i] = new Point(coords[i].y, size - 1 - coords[i].x);
			}
		}
		return rotated;
	}

	/** 테트로미노 타입에 따른 회전 기준 행렬 크기(3x3 or 4x4)를 반환. */
	private int getRotationMatrixSize(Tetromino type) {
		// I는 4x4, 나머지는 3x3 기준
		return (type == Tetromino.I) ? 4 : 3;
	}
}