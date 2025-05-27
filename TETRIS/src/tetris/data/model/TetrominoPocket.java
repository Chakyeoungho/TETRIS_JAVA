package tetris.data.model;

import static tetris.data.constants.Tetromino.TETROMINO_TYPE_COUNT;

import tetris.data.constants.Tetromino;
import tetris.logic.core.WELL1024a;

public class TetrominoPocket {
	// --- Constant ---
    // 상수: 클래스 내에서 변경하지 않는 고정 값으로, 포켓의 현재와 다음 상태를 구분하는 용도
    private static final int CURRENT_POCKET = 0;
    private static final int NEXT_POCKET = 1;

    // --- Field ---
    // 인스턴스 필드: 난수 생성기 객체와 테트로미노 포켓 데이터를 저장하는 배열
    // tetrominoPocket[0]은 현재 포켓, tetrominoPocket[1]은 다음 포켓을 의미
    private WELL1024a wellRng = new WELL1024a();
    private Tetromino[][] tetrominoPocket = new Tetromino[2][TETROMINO_TYPE_COUNT];

    // --- Constructor ---
    // 생성자: 객체 생성 시 실행되는 초기화 블록
    // 다음 포켓 배열에 0부터 TETROMINO_TYPE_COUNT-1까지 인덱스 넣고 셔플하여 초기 랜덤 상태 생성
    public TetrominoPocket () {
        for (int i = 0; i < TETROMINO_TYPE_COUNT; i++) 
            tetrominoPocket[NEXT_POCKET][i] = Tetromino.fromOrdinal(i);
        shufflePocket(tetrominoPocket[NEXT_POCKET]);
    }

    // 비공개 필드: 읽기 전용 복사본 반환 함수
    // getPocketData() 호출 시 이 복사된 값을 반환하여 외부에서 내부 배열 변경을 방지
    Tetromino[][] getPocketCopy() {
    	Tetromino[][] copy = new Tetromino[2][TETROMINO_TYPE_COUNT];
    	
        System.arraycopy(tetrominoPocket[CURRENT_POCKET], 0, 
        				 copy[CURRENT_POCKET], 0, 
        				 tetrominoPocket[CURRENT_POCKET].length);
        
        System.arraycopy(tetrominoPocket[NEXT_POCKET], 0, 
        				 copy[NEXT_POCKET], 0, 
        				 tetrominoPocket[NEXT_POCKET].length);
        
        return copy;
    }

    // --- Public Method ---
    // 공개 메서드: 다음 포켓을 현재 포켓으로 이동시키고, 새 포켓은 다시 셔플하여 갱신
    // 이 메서드를 호출하면 게임 진행에 필요한 새로운 테트로미노 순서가 준비됨
    void advancePocket() {
        System.arraycopy(tetrominoPocket[NEXT_POCKET], 0, 
                         tetrominoPocket[CURRENT_POCKET], 0, 
                         tetrominoPocket[NEXT_POCKET].length);
        shufflePocket(tetrominoPocket[NEXT_POCKET]);
    }

    // --- Private Method ---
    // 비공개 메서드: Fisher-Yates 알고리즘을 사용한 배열 셔플 함수
    // 포켓 내 테트로미노 순서를 무작위로 섞어 게임의 랜덤성을 보장
    private void shufflePocket(Tetromino[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = (int)(wellRng.WELLRNG1024a() * (i + 1));
            Tetromino temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}
