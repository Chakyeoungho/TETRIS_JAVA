package tetris;

import javax.swing.SwingUtilities;

import tetris.data.dto.GameDataManager;
import tetris.logic.GameTimer;
import tetris.ui.GameRenderer;

public class Tetris {
	public static void main(String[] args) {
	    SwingUtilities.invokeLater(() -> {
	        // UI 생성
	        GameRenderer renderer = new GameRenderer();

	        // UI와 관련된 데이터 초기화도 여기서
	        GameDataManager player = new GameDataManager();

	        // 타이머 시작 (UI에 영향을 준다면 역시 EDT에서)
	        GameTimer t = new GameTimer(500_000_000, () -> {
	            System.out.println("타이머2 실행 at " + System.currentTimeMillis());
	            System.out.flush(); // 콘솔 출력 확인용
	            
	            renderer.refreshScreen();
	        });
	        
	        t.start();
	    });
	}
}
