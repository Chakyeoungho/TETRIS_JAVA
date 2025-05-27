package tetris;

import java.awt.Point;

import javax.swing.SwingUtilities;

import tetris.data.model.DataManager;
import tetris.logic.core.GameTimer;
import tetris.logic.tetromino.CollisionChecker;
import tetris.logic.tetromino.TetrominoGenerator;
import tetris.logic.tetromino.TetrominoMover;
import tetris.ui.GameRenderer;

public class Tetris {
	public static void main(String[] args) {
	    SwingUtilities.invokeLater(() -> {
	    	DataManager manager = new DataManager();
	    	CollisionChecker checker = new CollisionChecker(manager);
	    	TetrominoMover move = new TetrominoMover(manager, checker);
	        // UI 생성
	        GameRenderer renderer = new GameRenderer(manager);
	        
	        TetrominoGenerator generate = new TetrominoGenerator(manager);
	        generate.generateTetromino();
	        
	        // 타이머 시작 (UI에 영향을 준다면 역시 EDT에서)
	        GameTimer t = new GameTimer(1_000_000_000, () -> {
	            System.out.println("타이머2 실행 at " + System.currentTimeMillis());
	            System.out.flush(); // 콘솔 출력 확인용

	            if (!move.drop()) generate.generateTetromino();
	            
	            renderer.refreshScreen();
	        });
	        
	        t.start();
	    });
	}
}
