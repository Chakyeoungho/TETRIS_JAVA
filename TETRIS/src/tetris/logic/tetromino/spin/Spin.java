package tetris.logic.tetromino.spin;

import static tetris.data.constants.Tetromino.EMPTY;
import static tetris.logic.tetromino.spin.WallKickData.getWallKickOffsets;

import java.awt.Point;

import tetris.data.constants.Tetromino;
import tetris.data.model.DataManager;
import tetris.logic.tetromino.CollisionChecker;

/**
 * 테트로미노 회전 및 SRS(Wall Kick) 적용을 담당하는 클래스
 */
public class Spin {
	private final DataManager gameData;
	private final CollisionChecker checker;

	/**
	 * 생성자
	 * 
	 * @param gameData 테트로미노 및 게임 데이터 관리 객체
	 * @param checker 충돌 검사기
	 */
	public Spin(DataManager gameData, CollisionChecker checker) {
		this.gameData = gameData;
		this.checker = checker;
	}

	/**
	 * 필드에 현재 테트로미노를 지정된 값으로 표시
	 * 
	 * @param value 테트로미노 블록 또는 EMPTY (빈 칸)
	 */
	private void updateTetrominoOnField(Tetromino value) {
		var state = gameData.getTetrominoState();
		var coords = state.getTetrominoCoords();
		var offset = state.getTetrominoOffset();

		for (int i = 0; i < 4; i++) {
			int x = coords[i].x;
			int y = coords[i].y;
			gameData.setCell(y + offset.y, x + offset.x, value);
		}
	}

	/**
	 * 필드에서 이전 테트로미노 데이터를 제거 (빈 칸으로 덮음)
	 */
	private void removePreviousTetrominoData() {
		updateTetrominoOnField(EMPTY);
	}

	/**
	 * 회전 테스트용 좌표 생성 (회전 변환 적용)
	 * 좌표계 기준으로 단순 90도 회전을 구현 (시계/반시계 방향)
	 * 
	 * @param isClockWise 시계방향 회전 여부
	 * @return 회전된 테트로미노 블록 좌표 배열 (상대 좌표)
	 */
	private Point[] getTestSpinCoords(boolean isClockWise) {
		Point[] coords = gameData.getTetrominoState().getTetrominoCoords();
		Point[] rotatedCoords = new Point[4];
		
		// I 테트로미노는 4x4, 나머지는 3x3 기준 회전
		int length = (gameData.getTetrominoState().getCurrentTetromino() == Tetromino.I) ? 4 : 3;

		for (int i = 0; i < 4; i++) {
			if (isClockWise) {
				// 시계방향 90도 회전 공식: (x, y) -> (length - 1 - y, x)
				rotatedCoords[i] = new Point(length - 1 - coords[i].y, coords[i].x);
			} else {
				// 반시계방향 90도 회전 공식: (x, y) -> (y, length - 1 - x)
				rotatedCoords[i] = new Point(coords[i].y, length - 1 - coords[i].x);
			}
		}

		return rotatedCoords;
	}

	/**
	 * SRS Wall Kick 규칙에 따라 회전 오프셋 적용
	 * 
	 * @param from 현재 SpinState
	 * @param isClockwise 시계방향 회전 여부
	 */
	private void translateSRSOffset(SpinState from, boolean isClockwise) {
		var state = gameData.getTetrominoState();
		SpinState to = isClockwise ? SpinState.clockwise(from) : SpinState.counterClockwise(from);
		Tetromino type = state.getCurrentTetromino();
		Point offset = state.getTetrominoOffset();
		Point[] rotatedCoords = getTestSpinCoords(isClockwise);	// 회전 후 좌표 계산 (벽 킥 전 상태)

		// 벽 킥 데이터에 따라 가능한 위치 검사
		for (Point wallKickData : getWallKickOffsets(type, to)) {
			Point testOffset = new Point(offset.x + wallKickData.x, offset.y + wallKickData.y);

			// 해당 위치에 배치 가능한지 충돌 검사
			if (checker.canPlace(rotatedCoords, testOffset)) {
				// 회전 좌표, 오프셋, 회전 상태 갱신
				state.setTetrominoCoords(rotatedCoords);
				state.setTetrominoOffset(testOffset);
				state.setSpinState(to);
				return;
			}
		}
		// 모든 벽 킥 실패 시 회전하지 않음
	}

	/**
	 * 현재 회전 상태 및 위치 기준으로 테트로미노를 필드에 그림
	 */
	private void setTetrominoData() {
		updateTetrominoOnField(gameData.getTetrominoState().getCurrentTetromino());
	}

	/**
	 * 현재 테트로미노를 지정된 방향으로 회전시킵니다. 
	 * Super Rotation System(SRS)에 따라 벽 킥(wall kick) 처리를 적용하여 
	 * 가능한 회전 위치를 찾고, 해당 위치로 블록을 이동시킵니다.
	 * 
	 * @param isClockwise true면 시계방향, false면 반시계방향으로 회전
	 */
	public void spin(boolean isClockwise) {
		// O 블록은 회전해도 형태가 변하지 않으므로 처리 생략
		if (gameData.getTetrominoState().getCurrentTetromino() == Tetromino.O) return;

		// 현재 회전 상태 가져오기
		SpinState currentSpin = gameData.getTetrominoState().getCurrentSpinState();

		removePreviousTetrominoData();           // 기존 블록을 필드에서 제거
		translateSRSOffset(currentSpin, isClockwise); // 회전 방향에 따라 SRS 규칙 적용 및 위치 조정
		setTetrominoData();                      // 회전 및 이동된 블록을 필드에 다시 그림
	}

}
