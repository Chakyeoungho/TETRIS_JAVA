package tetris.logic.tetromino;

import static tetris.data.constant.GameConstants.BUFFER_ZONE;
import static tetris.data.constant.Tetromino.EMPTY;

import java.awt.Point;

import tetris.data.constant.SpinState;
import tetris.data.constant.Tetromino;
import tetris.logic.TetrisEngine;
import tetris.logic.data.DataManager;

/** 게임의 '홀드(Hold)' 기능을 담당. */
public class HoldHandler {

    // --- Instance Fields ---
	private DataManager gameData;
	private TetrisEngine gameEngine;

	// 현재 홀드된 테트로미노. 비어있으면 EMPTY.
	private Tetromino heldTetromino = EMPTY;
	// 이번 턴에 홀드를 사용했는지 여부. (한 턴에 한 번만 사용 가능)
	private boolean isHoldUsed = false;

    // --- Constructor ---
	public HoldHandler(DataManager gameData, TetrisEngine gameEngine) {
		this.gameData = gameData;
		this.gameEngine = gameEngine;
	}

    // --- Public Methods ---
	/**
	 * 홀드 기능을 실행. 현재 블록을 홀드 칸으로 보내거나, 홀드된 블록과 교체.
	 */
	public void hold() {
		// 한 턴에 홀드를 여러 번 사용하는 것을 방지
		if (isHoldUsed) return;

		var tetState = gameData.getTetrominoState();
		Tetromino current = tetState.getCurrentTetromino();

		// Case 1: 홀드 칸이 비어있는 경우
		if (heldTetromino == EMPTY) {
			heldTetromino = current; // 현재 블록을 홀드
			gameEngine.getTetrominoGenerator().generateTetromino(); // 즉시 새 블록 생성
		} 
		// Case 2: 홀드 칸에 블록이 있는 경우
		else {
			// 현재 블록과 홀드된 블록을 교체(swap)
			Tetromino temp = heldTetromino;
			heldTetromino = current;
			tetState.setCurrentTetromino(temp);

			// 교체되어 나온 블록을 필드 시작 위치로 리셋
			tetState.setTetrominoOffset(new Point(3, BUFFER_ZONE - 3));
			tetState.setTetrominoCoords(tetState.getCurrentTetromino().getMinos());
			gameEngine.getSpin().setSpinState(SpinState.S0);
		}

		// 홀드 후 블록이 즉시 아래로 한 칸 내려가도록 처리하여 반응성을 높임
		gameEngine.getTetrominoMover().drop();
		isHoldUsed = true; // 홀드 사용 플래그 설정
	}
    
	/** 현재 홀드된 테트로미노를 반환 (UI 표시에 사용). */
	public Tetromino getHeldTetromino() { return heldTetromino; }

	/** '홀드 사용됨' 플래그를 리셋 (새 블록이 필드에 고정될 때 호출됨). */
	public void resetIsHoldUsed() { isHoldUsed = false; }

	/** 홀드된 테트로미노를 비움 (게임 리셋 시 사용). */
	public void resetHeltTetromino() { heldTetromino = EMPTY; }
}