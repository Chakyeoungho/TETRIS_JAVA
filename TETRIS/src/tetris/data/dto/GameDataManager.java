package tetris.data.dto;

import tetris.data.constants.Tetromino;
import tetris.logic.tetromino.TetrominoPocket;

/**
 * 테트리스 게임의 데이터를 통합적으로 관리하는 클래스.
 * 개별 데이터 클래스들 (필드, 포켓, 게임 상태, 테트로미노 상태)을 포함하고 있으며,
 * 외부에서 필요한 정보를 제공하거나 조작할 수 있도록 메서드를 제공한다.
 */
public class GameDataManager {
	// --- Field ---
    // 게임 필드 상태를 관리하는 객체
    private final Playfield fieldData = new Playfield();
    // 테트로미노 포켓 (현재/다음 블록 큐)을 관리하는 객체
//    private final TetrominoPocket pocket = new TetrominoPocket();
    // 게임의 전반적인 상태 (진행 중, 일시 정지 등)를 저장하는 객체
    private final GameState gameState = new GameState();
    // 현재 활성화된 테트로미노의 위치, 회전 등을 관리하는 객체
    private final TetrominoState tetrominoState = new TetrominoState();

    // --- Method ---
    // 게임 상태 객체 반환
    public GameState getGameState() { return gameState; }
    // 현재 테트로미노 상태 객체 반환
    public TetrominoState getTetrominoState() { return tetrominoState; }

    // 게임 필드의 특정 셀 값을 읽어옴
    public int getCell(int y, int x) { return fieldData.readFieldData().get(y, x); }
    // 게임 필드의 특정 셀에 테트로미노 값을 설정
    public void setCell(int y, int x, Tetromino tetromino) { fieldData.setCell(y, x, tetromino); }

//    // 현재 포켓 상태(현재 + 다음 큐)를 복사해서 반환
//    public Tetromino[][] getPocketCopy() { return pocket.getPocketCopy(); }
//    // 다음 포켓을 현재로 이동시키고 새로운 포켓을 셔플
//    public void advancePocket() { pocket.advancePocket(); }
}
