package tetris;

import tetris.logic.TetrisEngine;
import tetris.player.PlayerManager;

/** 테트리스 게임 실행기 (Entry Point) */
public class Tetris {

	// 프로그램 시작점
	public static void main(String[] args) throws InterruptedException {
		// 플레이어 및 게임 엔진 관리자 생성
		PlayerManager player = new PlayerManager();
		// 실제 게임 로직을 처리하는 엔진 가져오기
		TetrisEngine gameEngine = player.getGameEngine();

		// 게임 실행 및 예외 처리
		try {
			// 게임 시작
			gameEngine.startTetris();
		} catch (Exception e) {
			System.err.println("게임 실행 중 오류 발생: " + e.getMessage());
			e.printStackTrace();
		}
	}
}