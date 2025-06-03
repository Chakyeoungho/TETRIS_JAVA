package tetris;

import tetris.logic.TetrisEngine;
import tetris.player.PlayerManager;

public class Tetris {

    public static void main(String[] args) {
        PlayerManager player = new PlayerManager();
        TetrisEngine gameEngine = player.getGameEngine();

        try {
            gameEngine.startTetris();
        } catch (Exception e) {
            System.err.println("게임 실행 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
