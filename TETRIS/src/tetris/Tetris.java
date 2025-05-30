package tetris;

import javax.swing.SwingUtilities;

import tetris.data.model.DataManager;
import tetris.logic.TetrisEngine;
import tetris.logic.core.GameTimer;
import tetris.logic.tetromino.CollisionChecker;
import tetris.logic.tetromino.TetrominoGenerator;
import tetris.logic.tetromino.TetrominoMover;
import tetris.logic.tetromino.spin.Spin;
import tetris.ui.GameRenderer;

public class Tetris {
	public static void main(String[] args) {
	    SwingUtilities.invokeLater(() -> {
	    	DataManager gameData1 = new DataManager();
	    	TetrisEngine gameEngine = new TetrisEngine(gameData1);
	        
	    	gameEngine.getTetrominoGenerator().generateTetromino();
	        
	        // 타이머 시작 (UI에 영향을 준다면 역시 EDT에서)
	        GameTimer t = new GameTimer(1_000_000_000, () -> {
	            System.out.println("타이머2 실행 at " + System.currentTimeMillis());
	            System.out.flush(); // 콘솔 출력 확인용

	            //if (!move.drop()) generate.generateTetromino();
	            gameEngine.getSpin().spin(true);
	            
	            gameEngine.getGameRenderer().refreshScreen();
	        });
	        
	        t.start();
	    });
	}
}
