package ru.mirea.kartyshovav.mireaproject;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AccelerometerFragment extends Fragment implements SensorEventListener {

    private TextView tvX, tvY, tvZ, tvDirection;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accelerometer, container, false);

        tvX = view.findViewById(R.id.tvX);
        tvY = view.findViewById(R.id.tvY);
        tvZ = view.findViewById(R.id.tvZ);
        tvDirection = view.findViewById(R.id.tvDirection);

        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (magnetometer != null) {
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values.clone();
            updateUI();
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values.clone();
            updateUI();
        }
    }

    private void updateUI() {
        // Обновляем показания акселерометра
        tvX.setText(String.format("X: %.2f", gravity[0]));
        tvY.setText(String.format("Y: %.2f", gravity[1]));
        tvZ.setText(String.format("Z: %.2f", gravity[2]));

        // Вычисляем азимут (угол от севера)
        float[] R = new float[9];
        float[] I = new float[9];
        boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);

        if (success) {
            float[] orientation = new float[3];
            SensorManager.getOrientation(R, orientation);
            float azimuth = (float) Math.toDegrees(orientation[0]);
            azimuth = (azimuth + 360) % 360;

            // Определяем сторону света
            String direction;
            if (azimuth >= 337.5f || azimuth < 22.5f) {
                direction = "СЕВЕР";
            } else if (azimuth >= 22.5f && azimuth < 67.5f) {
                direction = "СЕВЕРО-ВОСТОК";
            } else if (azimuth >= 67.5f && azimuth < 112.5f) {
                direction = "ВОСТОК";
            } else if (azimuth >= 112.5f && azimuth < 157.5f) {
                direction = "ЮГО-ВОСТОК";
            } else if (azimuth >= 157.5f && azimuth < 202.5f) {
                direction = "ЮГ";
            } else if (azimuth >= 202.5f && azimuth < 247.5f) {
                direction = "ЮГО-ЗАПАД";
            } else if (azimuth >= 247.5f && azimuth < 292.5f) {
                direction = "ЗАПАД";
            } else {
                direction = "СЕВЕРО-ЗАПАД";
            }

            tvDirection.setText(String.format("%s\n(%.0f°)", direction, azimuth));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Не используем
    }
}