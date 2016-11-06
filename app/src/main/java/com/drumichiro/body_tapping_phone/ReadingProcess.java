package com.drumichiro.body_tapping_phone;

/**
 * Created by drumichiro on 2016/11/06.
 */
public interface ReadingProcess {
    void process(int sensorType, double[] current, double[] difference);
}
