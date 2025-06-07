package tetris.logic.scoring;

public enum ScoreAction {
    NOTHING(0),               //    0
    SINGLE(100),              //  100 x Level
    DOUBLE(300),              //  300 x Level
    TRIPLE(500),              //  500 x Level
    TETRIS(800),              //  800 x Level
    MINI_T_SPIN(100),         //  100 x Level
    MINI_T_SPIN_SINGLE(200),  //  200 x Level
    T_SPIN(400),              //  400 x Level
    T_SPIN_SINGLE(800),       //  800 x Level
    T_SPIN_DOUBLE(1200),      // 1200 x Level
    T_SPIN_TRIPLE(1600),      // 1600 x Level
    BACK_TO_BACK_BONUS(2),    //  0.5 x Action Total
    SOFT_DROP(1),             //    1 x n
    HARD_DROP(2);             //    2 x m

    private final int baseScore;

    ScoreAction(int baseScore) {
        this.baseScore = baseScore;
    }

    /** 해당 액션의 기본 점수를 반환. */
    public int getBaseScore() {
        return baseScore;
    }
}