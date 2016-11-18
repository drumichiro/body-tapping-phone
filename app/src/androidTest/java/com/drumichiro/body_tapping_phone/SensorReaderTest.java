package com.drumichiro.body_tapping_phone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.test.ActivityUnitTestCase;

import junit.framework.Assert;

/**
 * Created by drumichiro on 2016/11/06.
 */
public class SensorReaderTest extends ActivityUnitTestCase<MainActivity> {

    static final private double MARGIN = 10.0;

    private MainActivity activity;
    private Context context;

    public SensorReaderTest() {
        super(MainActivity.class);
    }

    private int accelerometerSamplingCount;
    private int gravitySamplingCount;
    private int gyroscopeSamplingCount;
    private final ReadingProcess listener = new ListeningProcessor();
    private class ListeningProcessor implements ReadingProcess {
        @Override
        public void process(int sensorType, double[] current, double[] difference) {
            if (Sensor.TYPE_ACCELEROMETER == sensorType) {
                ++accelerometerSamplingCount;
            }
            else if (Sensor.TYPE_GRAVITY == sensorType) {
                ++gravitySamplingCount;
            }
            else if (Sensor.TYPE_GYROSCOPE == sensorType) {
                ++gyroscopeSamplingCount;
            }
        }
    }

    @Override
    protected void setActivity(Activity testActivity) {
        if (testActivity != null) {
            testActivity.setTheme(R.style.AppTheme_NoActionBar);
        }
        super.setActivity(testActivity);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        startActivity(new Intent(getInstrumentation()
                .getTargetContext(), MainActivity.class), null, null);
        activity = getActivity();
        Assert.assertNotNull(activity);
        context = getInstrumentation().getContext();
        Assert.assertNotNull(context);
    }

    public void testSensorSetup() {
        SensorManager manager = (SensorManager)activity.getSystemService(context.SENSOR_SERVICE);
        SensorReader sensorProximity = new SensorReader(manager, Sensor.TYPE_PROXIMITY);
        Assert.assertNotNull(sensorProximity);
        SensorReader sensorAccelerometer = new SensorReader(manager, Sensor.TYPE_ACCELEROMETER);
        Assert.assertNotNull(sensorAccelerometer);
        SensorReader sensorGyroscope = new SensorReader(manager, Sensor.TYPE_GYROSCOPE);
        Assert.assertNotNull(sensorGyroscope);
    }

    public void testInvalidSensorSetup() throws Exception {
        SensorManager manager = (SensorManager)activity.getSystemService(context.SENSOR_SERVICE);
        try {
            // -1 is used by Sensor.TYPE_ALL.
            new SensorReader(manager, -2147483648);
            throw new Exception();
        }
        catch (AssertionError e) {
            Assert.assertEquals(e.getMessage(), "Negative sensor index is input.");
        }
        try {
            // 33 is out of range of supported sensor index.
            new SensorReader(manager, Sensor.TYPE_HEART_BEAT + 1);
            throw new Exception();
        }
        catch (AssertionError e) {
            Assert.assertEquals(e.getMessage(), "Unsupported sensor index is input.");
        }
        try {
            // Heart beat sensor is unsupported by my smart phone.
            new SensorReader(manager, Sensor.TYPE_HEART_BEAT);
            throw new Exception();
        }
        catch (AssertionError e) {
            Assert.assertEquals(e.getMessage(),
                    "Not equipped sensor is being used, but unsupported.");
        }
        try {
            new SensorReader(manager, Sensor.TYPE_ALL);
            throw new Exception();
        }
        catch (AssertionError e) {
            Assert.assertEquals(e.getMessage(), "Using all sensors is denied in this class.");
        }
    }

    public void testSensorSampling() throws Exception {
        SensorManager manager = (SensorManager)activity.getSystemService(context.SENSOR_SERVICE);
        SensorReader[] sensors = new SensorReader[] {
                new SensorReader(manager, Sensor.TYPE_ACCELEROMETER),
                new SensorReader(manager, Sensor.TYPE_GRAVITY),
                new SensorReader(manager, Sensor.TYPE_GYROSCOPE),
        };
        // Sampling start.
        for (SensorReader sensor : sensors) {
            sensor.setReadingProcess(listener);
        }
        long sleepingMSec = 3000;
        Thread.sleep(sleepingMSec);
        // Sampling stop.
        for (SensorReader sensor : sensors) {
            sensor.setReadingProcess(null);
        }
        double gravitySamplingBoost = gravitySamplingCount * 2.0;
        Assert.assertTrue(accelerometerSamplingCount - MARGIN < gravitySamplingBoost);
        Assert.assertTrue(accelerometerSamplingCount + MARGIN > gravitySamplingBoost);
        Assert.assertTrue(gyroscopeSamplingCount - MARGIN < gravitySamplingBoost);
        Assert.assertTrue(gyroscopeSamplingCount + MARGIN > gravitySamplingBoost);
        int accelerometerSamplingPrior = accelerometerSamplingCount;
        int gravitySamplingPrior = gravitySamplingCount;
        int gyroscopeSamplingPrior = gyroscopeSamplingCount;
        Thread.sleep(sleepingMSec);
        Assert.assertEquals(accelerometerSamplingPrior, accelerometerSamplingCount);
        Assert.assertEquals(gravitySamplingPrior, gravitySamplingCount);
        Assert.assertEquals(gyroscopeSamplingPrior, gyroscopeSamplingCount);
    }
}
