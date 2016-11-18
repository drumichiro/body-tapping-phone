package com.drumichiro.body_tapping_phone;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private static final int ACCELEROMETER_BUFFER_LENGTH = 20;
    private static final int GRAVITY_BUFFER_LENGTH = 20;
    private static final int GYROSCOPE_BUFFER_LENGTH = 20;
    private static final int TAPPING_POSITIONS = 3;
    private static final int NMF_ITERATIONS = 10;

    private static final int[] POSITION_COLOR = {
            Color.RED, Color.GREEN, Color.BLUE,
    };

    private int registerIndex;
    private int defaultColor;

    private NmfBase nmf;
    private final ReadingProcess resisterProcess = new RegisterProcessor();
    private final ReadingProcess recognitionProcess = new RecognitionProcessor();
    private SensorReader[] sensors;
    private FeatureExtractor accelerometerBuffer;
    private FeatureExtractor gravityBuffer;
    private FeatureExtractor gyroscopeBuffer;
    private double[] featureValue;

    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        accelerometerBuffer = new FeatureExtractor(ACCELEROMETER_BUFFER_LENGTH, 3);
        gravityBuffer = new FeatureExtractor(GRAVITY_BUFFER_LENGTH, 3);
        gyroscopeBuffer = new FeatureExtractor(GYROSCOPE_BUFFER_LENGTH, 3);
        featureValue = new double[
                accelerometerBuffer.getFeatures() +
                gravityBuffer.getFeatures() +
                gyroscopeBuffer.getFeatures()];
        // TODO: Optimize weights for feature.
        accelerometerBuffer.setWeight(1.0);
        gravityBuffer.setWeight(1.0);
        gyroscopeBuffer.setWeight(0.2);
        nmf = new NmfWithEuclid(TAPPING_POSITIONS, featureValue.length);

        SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensors = new SensorReader[] {
                new SensorReader(manager, Sensor.TYPE_PROXIMITY),
                new SensorReader(manager, Sensor.TYPE_ACCELEROMETER),
                new SensorReader(manager, Sensor.TYPE_GRAVITY),
                new SensorReader(manager, Sensor.TYPE_GYROSCOPE),
        };

        layout = (RelativeLayout)findViewById(R.id.background);
        defaultColor = layout.getDrawingCacheBackgroundColor();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.register_position_one) {
            registerIndex = 0;
            switchToResisterMode(0);
        }
        else if (id == R.id.register_position_two) {
            registerIndex = 1;
            switchToResisterMode(1);
        }
        else if (id == R.id.register_position_three) {
            registerIndex = 2;
            switchToResisterMode(2);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != sensors) {
            for (SensorReader sensor : sensors) {
                if (null != sensor) {
                    sensor.stopSensor();
                }
            }
        }
    }

    private class RegisterProcessor implements ReadingProcess {
        @Override
        public void process(int sensorType, double[] current, double[] difference) {
            if (Sensor.TYPE_PROXIMITY == sensorType) {
                if (0.0 == current[0]) {
                    Log.d("MainActivity", "Registered position:" + registerIndex + " " + registerIndex);
                    registerTappingFeature(registerIndex);
                }
                else {
                    switchToRecognitionMode();
                }
            }
            else if (Sensor.TYPE_ACCELEROMETER == sensorType) {
                accelerometerBuffer.addObservation(current);
            }
            else if (Sensor.TYPE_GRAVITY == sensorType) {
                gravityBuffer.addObservation(current);
            }
            else if (Sensor.TYPE_GYROSCOPE == sensorType) {
                gyroscopeBuffer.addObservation(current);
            }
        }
    }

    private class RecognitionProcessor implements ReadingProcess {
        @Override
        public void process(int sensorType, double[] current, double[] difference) {
            if (Sensor.TYPE_PROXIMITY == sensorType) {
                if (0.0 == current[0]) {
                    int position = recognizeTappingPosition();
                    Log.d("MainActivity", "Recognized position:" + position);
                    layout.setBackgroundColor(POSITION_COLOR[position]);
                }
            }
            else if (Sensor.TYPE_ACCELEROMETER == sensorType) {
                accelerometerBuffer.addObservation(current);
            }
            else if (Sensor.TYPE_GRAVITY == sensorType) {
                gravityBuffer.addObservation(current);
            }
            else if (Sensor.TYPE_GYROSCOPE == sensorType) {
                gyroscopeBuffer.addObservation(current);
            }
        }
    }

    private void switchToResisterMode(int index) {
        registerIndex = index;
        layout.setBackgroundColor(POSITION_COLOR[index]);
        for (SensorReader sensor : sensors) {
            sensor.setReadingProcess(resisterProcess);
        }
    }

    private void switchToRecognitionMode() {
        layout.setBackgroundColor(defaultColor);
        for (SensorReader sensor : sensors) {
            sensor.setReadingProcess(recognitionProcess);
        }
    }

    private void registerTappingFeature(int index) {
        double[] feature = nmf.getTemplate()[index];
        accelerometerBuffer.extractFeature(feature);
        int offset = accelerometerBuffer.getFeatures();
        gravityBuffer.extractFeature(feature, offset);
        offset += gyroscopeBuffer.getFeatures();
        gyroscopeBuffer.extractFeature(feature, offset);
        Log.d("MainActivity", "------------------------------------------");
        Log.d("MainActivity", "acc p: " + feature[0] + " : "  + feature[1] + " : "  + feature[2]);
        Log.d("MainActivity", "acc n: " + feature[3] + " : "  + feature[4] + " : "  + feature[5]);
        Log.d("MainActivity", "grv p: " + feature[6] + " : "  + feature[7] + " : "  + feature[8]);
        Log.d("MainActivity", "grv n: " + feature[9] + " : "  + feature[10] + " : "  + feature[11]);
        Log.d("MainActivity", "gyr p: " + feature[12] + " : "  + feature[13] + " : "  + feature[14]);
        Log.d("MainActivity", "gyr n: " + feature[15] + " : "  + feature[16] + " : "  + feature[17]);
        // TODO: Add proper weight.
    }

    private int recognizeTappingPosition() {
        accelerometerBuffer.extractFeature(featureValue);
        int offset = accelerometerBuffer.getFeatures();
        gravityBuffer.extractFeature(featureValue, offset);
        offset += gyroscopeBuffer.getFeatures();
        gyroscopeBuffer.extractFeature(featureValue, offset);
        Log.d("MainActivity", "------------------------------------------");
        Log.d("MainActivity", "obs acc p: " + featureValue[0] + " : "  + featureValue[1] + " : "  + featureValue[2]);
        Log.d("MainActivity", "obs acc n: " + featureValue[3] + " : "  + featureValue[4] + " : "  + featureValue[5]);
        Log.d("MainActivity", "obs grv p: " + featureValue[6] + " : "  + featureValue[7] + " : "  + featureValue[8]);
        Log.d("MainActivity", "obs grv n: " + featureValue[9] + " : "  + featureValue[10] + " : "  + featureValue[11]);
        Log.d("MainActivity", "obs gyr p: " + featureValue[12] + " : "  + featureValue[13] + " : "  + featureValue[14]);
        Log.d("MainActivity", "obs gyr n: " + featureValue[15] + " : "  + featureValue[16] + " : "  + featureValue[17]);
        nmf.factorize(featureValue, NMF_ITERATIONS, false);
        return MathAux.argmax(nmf.getWeight());
    }
}
