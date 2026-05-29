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

    private TextView tvDirection;
    private SensorManager sensorManager;
    private Sensor magnetometer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accelerometer, container, false);

        tvDirection = view.findViewById(R.id.tvDirection);

        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
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
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float[] magneticField = event.values; // Массив показаний датчика

            // Получаем азимут (угол от севера) из магнитного поля
            float azimuth = calculateAzimuth(magneticField);

            // Определяем сторону света
            String direction = getDirection(azimuth);

            tvDirection.setText(String.format("%s\n(%.0f°)", direction, azimuth));
        }
    }

    private float calculateAzimuth(float[] magneticField) {
        // Получаем азимут из показаний магнитометра
        // Предполагаем, что телефон лежит горизонтально
        float azimuth = (float) Math.toDegrees(Math.atan2(magneticField[1], magneticField[0]));
        azimuth = (azimuth + 360) % 360; // Приводит угол к диапазону от 0 до 360
        return azimuth;
    }

    private String getDirection(float azimuth) {
        // Только 4 основные стороны света
        if ((azimuth >= 315 && azimuth <= 360) || (azimuth >= 0 && azimuth < 45)) {
            return "СЕВЕР ";
        } else if (azimuth >= 45 && azimuth < 135) {
            return "ВОСТОК";
        } else if (azimuth >= 135 && azimuth < 225) {
            return "ЮГ";
        } else if (azimuth >= 225 && azimuth < 315) {
            return "ЗАПАД";
        }
        return "---";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}