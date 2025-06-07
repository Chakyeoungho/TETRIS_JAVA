package tetris.data.dto;

import static tetris.data.constant.Tetromino.TETROMINO_TYPE_COUNT;

import tetris.data.constant.Tetromino;
import tetris.logic.core.WELL1024a;

/**
 * 7-Bag 시스템을 구현한 테트로미노 공급 객체.
 * 7개의 서로 다른 테트로미노 한 세트를 무작위 순서로 제공.
 */
public class TetrominoBag {
    
    // --- Static Fields ---
    private static final int CURRENT_BAG = 0; // 현재 사용 중인 가방 인덱스
    private static final int NEXT_POCKET = 1; // 다음에 사용할 가방 인덱스 (원본 변수명 존중)

    // --- Instance Fields ---
    private WELL1024a wellRng = new WELL1024a(); // 난수 생성기
    private Tetromino[][] tetrominoBag = new Tetromino[2][TETROMINO_TYPE_COUNT]; // [0]: 현재, [1]: 다음

    // --- Constructor ---
    public TetrominoBag () {
        // 첫 시작 시, '다음 가방'을 먼저 채우고 섞어둠.
        for (int i = 0; i < TETROMINO_TYPE_COUNT; i++) 
            tetrominoBag[NEXT_POCKET][i] = Tetromino.fromOrdinal(i);
        shuffleBag(tetrominoBag[NEXT_POCKET]);
        
        // 그 후, '다음 가방'을 '현재 가방'으로 만들고 새 '다음 가방'을 준비.
        advanceBag();
    }

    // --- Public Methods ---
    /**
     * 현재 가방과 다음 가방의 상태를 복사하여 반환 (외부 조작 방지용).
     * UI의 'Next' 블록 표시에 주로 사용됨.
     */
    public Tetromino[][] getBagCopy() {
    	Tetromino[][] copy = new Tetromino[2][TETROMINO_TYPE_COUNT];
    	
        System.arraycopy(tetrominoBag[CURRENT_BAG], 0, 
        				 copy[CURRENT_BAG], 0, 
        				 tetrominoBag[CURRENT_BAG].length);
        
        System.arraycopy(tetrominoBag[NEXT_POCKET], 0, 
        				 copy[NEXT_POCKET], 0, 
        				 tetrominoBag[NEXT_POCKET].length);
        
        return copy;
    }

    /**
     * '다음 가방'을 '현재 가방'으로 가져오고,
     * 비어있는 '다음 가방'은 새로 7개의 블록을 채워 섞어둔다.
     */
    public void advanceBag() {
        System.arraycopy(tetrominoBag[NEXT_POCKET], 0, 
                         tetrominoBag[CURRENT_BAG], 0, 
                         tetrominoBag[NEXT_POCKET].length);
        
        // 다음 가방 새로 채우기
        for (int i = 0; i < TETROMINO_TYPE_COUNT; i++) {
            tetrominoBag[NEXT_POCKET][i] = Tetromino.fromOrdinal(i);
        }
        shuffleBag(tetrominoBag[NEXT_POCKET]);
    }

    // --- Private Methods ---
    /**
     * Fisher-Yates 셔플 알고리즘을 사용하여 배열의 순서를 무작위로 섞음.
     * @param array 섞을 테트로미노 배열
     */
    private void shuffleBag(Tetromino[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = (int)(wellRng.WELLRNG1024a() * (i + 1));
            Tetromino temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}