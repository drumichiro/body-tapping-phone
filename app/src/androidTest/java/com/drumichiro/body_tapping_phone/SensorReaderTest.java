package com.drumichiro.body_tapping_phone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.test.ActivityUnitTestCase;

import junit.framework.Assert;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class SensorReaderTest extends ActivityUnitTestCase<MainActivity> {

    private MainActivity activity;
    private Context context;

    public SensorReaderTest() {
        super(MainActivity.class);
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
            // Heart beat sensor is unsupported by my smart phone.
            new SensorReader(manager, 2147483647);
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
}
