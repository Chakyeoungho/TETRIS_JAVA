package tetris.logic.tetromino.spin;

import static tetris.logic.tetromino.spin.WallKickData.getWallKickOffsets;

import java.awt.Point;

import tetris.data.constants.Tetromino;
import tetris.data.dto.DataManager;
import tetris.logic.tetromino.CollisionChecker;

/**
 * 테트로미노 회전 및 SRS(Wall Kick) 적용을 담당하는 클래스
 */
public class Spin {
	private final DataManager gameData;
	private final CollisionChecker checker;

	public Spin(DataManager gameData, CollisionChecker checker) {
		this.gameData = gameData;
		this.checker = checker;
	}

	/**
	 * 회전 후 좌표 생성 (90도 회전)
	 */
	private Point[] getRotatedCoords(Point[] coords, Tetromino type, boolean isClockWise) {
		Point[] rotated = new Point[coords.length];
		int size = getRotationMatrixSize(type);

		for (int i = 0; i < coords.length; i++) {
			if (isClockWise) {
				rotated[i] = new Point(size - 1 - coords[i].y, coords[i].x);
			} else {
				rotated[i] = new Point(coords[i].y, size - 1 - coords[i].x);
			}
		}
		return rotated;
	}

	/**
	 * 테트로미노 타입에 따른 회전 기준 크기 반환
	 */
	private int getRotationMatrixSize(Tetromino type) {
		return (type == Tetromino.I) ? 4 : 3;
	}

	/**
	 * SRS 벽킥 규칙 적용
	 */
	private void applySRSRotation(SpinState from, boolean isClockwise) {
		var state = gameData.getTetrominoState();
		Tetromino type = state.getCurrentTetromino();
		SpinState to = isClockwise ? SpinState.clockwise(from) : SpinState.counterClockwise(from);

		Point[] rotatedCoords = getRotatedCoords(state.getTetrominoCoords(), type, isClockwise);
		Point offset = state.getTetrominoOffset();

		for (Point kick : getWallKickOffsets(type, from)) {
			Point testOffset = new Point(offset.x + kick.x, offset.y + kick.y);
			if (checker.canPlace(rotatedCoords, testOffset)) {
				state.setTetrominoCoords(rotatedCoords);
				state.setTetrominoOffset(testOffset);
				state.setSpinState(to);
				return;
			}
		}
		// 모든 위치 실패 → 회전 무효
	}

	/**
	 * 회전 실행 (SRS + 벽킥)
	 */
	public void spin(boolean isClockwise) {
		Tetromino type = gameData.getTetrominoState().getCurrentTetromino();
		if (type == Tetromino.O) return; // 회전 불필요

		SpinState current = gameData.getTetrominoState().getCurrentSpinState();
		applySRSRotation(current, isClockwise);
	}
}
