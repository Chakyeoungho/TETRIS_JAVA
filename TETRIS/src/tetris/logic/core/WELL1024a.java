/* ***************************************************************************** */
/* Copyright:      Francois Panneton and Pierre L'Ecuyer, University of Montreal */
/*                 Makoto Matsumoto, Hiroshima University                        */
/* Notice:         This code can be used freely for personal, academic,          */
/*                 or non-commercial purposes. For commercial purposes,          */
/*                 please contact P. L'Ecuyer at: lecuyer@iro.UMontreal.ca       */
/* ***************************************************************************** */

// Note: This is an external library for a high-quality random number generator (WELL1024a).
// The original source code has been preserved without modification.
// (참고: 고품질 난수 생성기(WELL1024a)를 위한 외부 라이브러리입니다.)

package tetris.logic.core;

import java.util.Random;

@SuppressWarnings("all")
public class WELL1024a {
    private final int W = 32;
    private final int R = 32;
    private final int M1 = 3;
    private final int M2 = 24;
    private final int M3 = 10;

    private final double FACT = 2.32830643653869628906e-10;

    private int state_i = 0;
    private int[] STATE = new int[R];
    private int z0, z1, z2;
    
    public WELL1024a() {
        Random rand = new Random();
        int[] init = new int[R];
        for (int i = 0; i < R; i++) {
            int high = rand.nextInt(1 << 16);
            int low = rand.nextInt(1 << 16);
            init[i] = (high << 16) | low;
        }
        init(init);
    }

    private int MAT0POS(int t, int v) {
        return v ^ (v >>> t);
    }

    private int MAT0NEG(int t, int v) {
        return v ^ (v << (-t));
    }

    private int Identity(int v) {
        return v;
    }

    private void init(int[] init) {
        if (init.length != R) {
            throw new IllegalArgumentException("Init array length must be " + R);
        }
        state_i = 0;
        System.arraycopy(init, 0, STATE, 0, R);
    }

    public double WELLRNG1024a() {
        z0 = STATE[(state_i + 31) & 0x1F];
        z1 = Identity(STATE[state_i]) ^ MAT0POS(8, STATE[(state_i + M1) & 0x1F]);
        z2 = MAT0NEG(-19, STATE[(state_i + M2) & 0x1F]) ^ MAT0NEG(-14, STATE[(state_i + M3) & 0x1F]);
        STATE[(state_i + 31) & 0x1F] = MAT0NEG(-11, z0) ^ MAT0NEG(-7, z1) ^ MAT0NEG(-13, z2);
        STATE[state_i] = z1 ^ z2;
        state_i = (state_i + 31) & 0x1F;
        return (STATE[state_i] & 0xFFFFFFFFL) * FACT;
    }
}