package tetris.logic.tetromino;

import static tetris.data.constants.Tetromino.EMPTY;

import java.awt.Point;

import tetris.data.constants.Tetromino;
import tetris.data.model.DataManager;

public class TetrominoMover {
    private final DataManager gameData;
    private final CollisionChecker checker;
    
    /**
     * TetrominoMover 생성자.
     * DataManager와 PlayField 객체를 받아 초기화한다.
     */
    
    // TODO: 임시로 매개변수 설정해둠 테스트용
    public TetrominoMover(DataManager gameData, CollisionChecker checker) {
        this.gameData = gameData;
        this.checker = checker;
    }

    /**
     * 현재 테트로미노 위치를 기준으로 필드 셀을 지정된 값(value)으로 업데이트한다.
     * @param value 필드에 설정할 Tetromino 값 (예: EMPTY 또는 현재 테트로미노)
     */
    private void updateTetrominoOnField(Tetromino value) {
        var state = gameData.getTetrominoState();
        var coords = state.getTetrominoCoords();
        var offset = state.getTetrominoOffset();

        int offsetX = offset.x;
        int offsetY = offset.y;
        
        for (int i = 0; i < 4; i++) {
            int x = coords[i].x;
            int y = coords[i].y;
            gameData.setCell(y + offsetY, x + offsetX, value);
        }
    }

    /**
     * 이전에 그려진 테트로미노를 필드에서 지운다.
     */
    private void removePreviousTetrominoData() {
        updateTetrominoOnField(EMPTY);
    }
    
    /**
     * 테트로미노 오프셋을 dx, dy 만큼 이동시킨다.
     * 새로운 Point 객체를 생성하여 불변성을 유지한다.
     * @param dx x 방향 이동량
     * @param dy y 방향 이동량
     */
    private void translateOffset(int dx, int dy) {
        var state = gameData.getTetrominoState();
        var offset = state.getTetrominoOffset();
        state.setTetrominoOffset(new Point(offset.x + dx, offset.y + dy));
    }
    
    /**
     * 현재 테트로미노를 필드에 그린다.
     */
    private void setTetrominoData() {
        updateTetrominoOnField(gameData.getTetrominoState().getCurrentTetromino());
    }
    
    /**
     * 이전 위치를 지우고 오프셋을 이동한 후, 새로운 위치에 테트로미노를 그린다.
     * @param dx x 방향 이동량
     * @param dy y 방향 이동량
     */
    private boolean moveAndUpdate(int dx, int dy) {
        Point[] coords = gameData.getTetrominoState().getTetrominoCoords();
        Point offset = gameData.getTetrominoState().getTetrominoOffset();
        Point testOffset = new Point(offset.x + dx, offset.y + dy);
        
        removePreviousTetrominoData();
        if (!checker.canPlace(coords, testOffset)) {
            setTetrominoData();
            return false;
        }
        translateOffset(dx, dy);
        setTetrominoData();
        
        return true;
    }

    /** 테트로미노를 한 칸 아래로 이동시킨다. */
    public boolean drop() {
        return moveAndUpdate(0, 1);
    }

    /** 테트로미노를 왼쪽으로 한 칸 이동시킨다. */
    public boolean left() {
        return moveAndUpdate(-1, 0);
    }

    /** 테트로미노를 오른쪽으로 한 칸 이동시킨다. */
    public boolean right() {
        return moveAndUpdate(1, 0);
    }
}
