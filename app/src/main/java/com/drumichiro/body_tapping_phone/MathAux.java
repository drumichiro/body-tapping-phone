package com.drumichiro.body_tapping_phone;

/**
 * Created by drumichiro on 2016/12/18.
 */
public class MathAux {
    static public int argmax(double[] value) {
        int maxPosition = 0;
        double maxValue = value[0];
        for (int i1=1; i1<value.length; ++i1) {
            if (maxValue < value[i1]) {
                maxValue = value[i1];
                maxPosition = i1;
            }
        }
        return maxPosition;
    }
}
