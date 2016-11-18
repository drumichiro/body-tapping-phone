package com.drumichiro.body_tapping_phone;

import java.util.List;

import junit.framework.Assert;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by drumichiro on 2016/11/06.
 */
public class SensorReader implements SensorEventListener {

    static private class SensorMode {
        public int length;
        public String string;
        SensorMode(int length, String string) {
            this.length = length;
            this.string = string;
        }
    }

    private static final SensorMode[] SensorReportingModes = {
        new SensorMode(0, "padding because sensor types start at 1)"),
        new SensorMode(3, "ACCELEROMETER"),
        new SensorMode(3, "MAGNETIC_FIELD"),
        new SensorMode(3, "ORIENTATION"),
        new SensorMode(3, "GYROSCOPE"),
        new SensorMode(3, "LIGHT"),
        new SensorMode(3, "PRESSURE"),
        new SensorMode(3, "TEMPERATURE"),
        new SensorMode(3, "PROXIMITY"),
        new SensorMode(3, "GRAVITY"),
        new SensorMode(3, "LINEAR_ACCELERATION"),   // 10
        new SensorMode(5, "ROTATION_VECTOR"),
        new SensorMode(3, "RELATIVE_HUMIDITY"),
        new SensorMode(3, "AMBIENT_TEMPERATURE"),
        new SensorMode(6, "MAGNETIC_FIELD_UNCALIBRATED"),
        new SensorMode(4, "GAME_ROTATION_VECTOR"),
        new SensorMode(6, "GYROSCOPE_UNCALIBRATED"),
        new SensorMode(1, "SIGNIFICANT_MOTION"),
        new SensorMode(1, "STEP_DETECTOR"),
        new SensorMode(1, "STEP_COUNTER"),
        new SensorMode(5, "GEOMAGNETIC_ROTATION_VECTOR"),   // 20
        new SensorMode(1, "HEART_RATE_MONITOR"),
        new SensorMode(1, "WAKE_UP_TILT_DETECTOR"),
        new SensorMode(1, "WAKE_GESTURE"),
        new SensorMode(1, "GLANCE_GESTURE"),
        new SensorMode(1, "PICK_UP_GESTURE"),
        new SensorMode(0, "padding"),           // Not tested.
        new SensorMode(0, "padding"),           // Not tested.
        new SensorMode(1, "POSE_6DOF"),         // Not tested.
        new SensorMode(1, "STATIONARY_DETECT"), // Not tested.
        new SensorMode(1, "MOTION_DETECT"),     // Not tested.
        new SensorMode(1, "HEART_BEAT"),        // Not tested.
    };

    private final ReadingProcess defaultProcessor = new DefaultProcessor();
    private ReadingProcess reading;
    private SensorManager manager;

    private int length;
    private int sensorType;
    private String typeName;

    // Allocate to return value with double type.
    private double[] currentBuffer;
    private double[] previousBuffer;
    private double[] differenceBuffer;

    public SensorReader(SensorManager manager, int sensorType) {
        initialize(manager, sensorType, null);
    }

    public SensorReader(SensorManager manager,
        int sensorType, ReadingProcess reading) {
        initialize(manager, sensorType, reading);
    }

    public void initialize(SensorManager manager, int sensorType, ReadingProcess reading) {
        Assert.assertTrue(
                "Using all sensors is denied in this class.", Sensor.TYPE_ALL != sensorType);
        Assert.assertTrue(
                "Negative sensor index is input.", 0 <= sensorType);
        Assert.assertTrue(
                "Unsupported sensor index is input.", sensorType < SensorReportingModes.length);
        List<Sensor> sensors = manager.getSensorList(sensorType);
        Assert.assertTrue(
                "Not equipped sensor is being used, but unsupported.", 0 < sensors.size());

        this.manager = manager;
        setReadingProcess(reading);
        this.sensorType = sensorType;

        typeName = SensorReportingModes[sensorType].string;
        length = SensorReportingModes[sensorType].length;
        currentBuffer = new double[length];
        previousBuffer = new double[length];
        differenceBuffer = new double[length];

        // Register sensor to listen to.
        manager.registerListener(
            this, sensors.get(sensors.size() - 1), SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void stopSensor() {
        if (manager != null)
            manager.unregisterListener(this);
        manager = null;
    }

    public int getLength() {
        return length;
    }

    public int getType() {
        return sensorType;
    }

    public double[] getCurrentValue() {
        return currentBuffer;
    }

    public double[] getDifferenceValue() {
        return differenceBuffer;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setReadingProcess(ReadingProcess reading) {
        this.reading = (null != reading) ? reading : defaultProcessor;
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        setEventValue(event);
        reading.process(
            event.sensor.getType(),
            currentBuffer,
            differenceBuffer);
    }

    private void setEventValue(SensorEvent event) {
        Assert.assertTrue(event.values.length <= currentBuffer.length);
        Assert.assertTrue(event.values.length <= previousBuffer.length);
        Assert.assertTrue(event.values.length <= differenceBuffer.length);
        for (int i1=0; i1<event.values.length; ++i1) {
            previousBuffer[i1] = currentBuffer[i1];
            currentBuffer[i1] = event.values[i1];
            differenceBuffer[i1] = currentBuffer[i1] - previousBuffer[i1];
        }
    }

    private class DefaultProcessor implements ReadingProcess {
        @Override
        public void process(int sensorType, double[] current, double[] difference) {
            // Do nothing.
        }
    }
}
