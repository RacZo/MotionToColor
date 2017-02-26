/***
 * Copyright (c) 2015 Oscar Salguero www.oscarsalguero.com
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oscarsalguero.motiontocolor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Demonstrates how to use the accelerometer to detect motion (when the device has been shaken) and get colors.
 * Created by RacZo on 9/3/15.
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private TextView textViewColor;
    private TextView textViewAxisX;
    private TextView textViewAxisY;
    private TextView textViewAxisZ;
    private LinearLayout linearLayout;

    private static final String DEFAULT_COLOR = "#FF000000";
    private static final String SPACE = " ";
    private long lastUpdate;
    private static final String LOG_TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();
        textViewColor = (TextView) findViewById(R.id.text_view_color);
        textViewColor.setText(getString(R.string.label_hex) + SPACE + DEFAULT_COLOR);
        textViewAxisX = (TextView) findViewById(R.id.text_view_x_axis);
        textViewAxisY = (TextView) findViewById(R.id.text_view_y_axis);
        textViewAxisZ = (TextView) findViewById(R.id.text_view_z_axis);
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_about:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.git_hub_repo_url)));
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event != null) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                getAccelerometer(event);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    /**
     * Gets coordinates values from accelerometer
     */
    private void getAccelerometer(SensorEvent event) {

        float[] values = event.values;

        // Motion
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelerationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelerationSquareRoot >= 2) {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
            // Coordinates
            x = values[0] * 100;
            y = values[1] * 100;
            z = values[2] * 100;

            updateUI(x, y, z);

        }

    }

    /**
     * Updates the UI with a new color
     *
     * @param x a float value with the accelerometer's X coordinate
     * @param y a float value with the accelerometer's Y coordinate
     * @param z a float value with the accelerometer's Z coordinate
     */
    private void updateUI(float x, float y, float z) {

        textViewAxisX.setText(getString(R.string.label_x) + SPACE + x);

        textViewAxisY.setText(getString(R.string.label_y) + SPACE + y);

        textViewAxisZ.setText(getString(R.string.label_z) + SPACE + z);

        Log.d(LOG_TAG, "X: " + x + ", Y: " + y + ", Z: " + z);

        int color = generateColor(x, y, z);

        linearLayout.setBackgroundColor(color);

        textViewColor.setText(getString(R.string.label_hex) + SPACE + Integer.toHexString(color));

    }

    /**
     * Generates a color using the given accelerometer coordinates
     *
     * @param x a float value with the accelerometer's X coordinate
     * @param y a float value with the accelerometer's Y coordinate
     * @param z a float value with the accelerometer's Z coordinate
     * @return color
     */
    private int generateColor(float x, float y, float z) {

        // RGB values
        int red = Math.abs(Math.round((x * 256) / 256));
        int green = Math.abs(Math.round((y * 256) / 256));
        int blue = Math.abs(Math.round((z * 256) / 256));

        Log.d(LOG_TAG, "R: " + red + ", G: " + green + ", B: " + blue);

        return Color.argb(255, red, green, blue);
    }

}
