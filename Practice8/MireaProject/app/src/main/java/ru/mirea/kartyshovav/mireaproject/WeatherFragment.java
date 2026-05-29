package ru.mirea.kartyshovav.mireaproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFragment extends Fragment {

    private TextView tvCity, tvTemperature, tvWindSpeed, tvWeatherDesc;
    private Button btnRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        tvCity = view.findViewById(R.id.tvCity);
        tvTemperature = view.findViewById(R.id.tvTemperature);
        tvWindSpeed = view.findViewById(R.id.tvWindSpeed);
        tvWeatherDesc = view.findViewById(R.id.tvWeatherDesc);
        btnRefresh = view.findViewById(R.id.btnRefresh);

        btnRefresh.setOnClickListener(v -> loadWeather());

        loadWeather();

        return view;
    }

    // Метод загрузки данных
    private void loadWeather() {
        tvTemperature.setText("Загрузка");
        tvWindSpeed.setText("Ветер:");
        tvWeatherDesc.setText("Погода:");
        btnRefresh.setEnabled(false);

        new Thread(() -> {
            String jsonResponse = downloadWeatherData(); // Сетевой запрос

            new Handler(Looper.getMainLooper()).post(() -> { // Отправляем задачу в главнй поток
                btnRefresh.setEnabled(true);

                if (jsonResponse != null && !jsonResponse.equals("error")) {
                    parseAndDisplayWeather(jsonResponse); // Парсим
                } else {
                    tvTemperature.setText("Ошибка");
                    Toast.makeText(getContext(), "Не удалось загрузить погоду", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    // Метод HTTP запроса
    private String downloadWeatherData() {
        HttpURLConnection connection = null;
        try {
            String urlString = "https://api.open-meteo.com/v1/forecast?" +
                    "latitude=55.7558&longitude=37.6173&current_weather=true";

            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection(); // Открываем соединение
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream(); // Получаем поток с данными от сервера
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                return result.toString("UTF-8"); // Преобразуем в строку
            } else {
                return "error";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Парсинг JSON
    private void parseAndDisplayWeather(String json) {
        try {
            // Парсим JSON с помощью Gson
            Gson gson = new Gson();
            JsonObject root = gson.fromJson(json, JsonObject.class); // Преобразуем строку в объект
            JsonObject currentWeather = root.getAsJsonObject("current_weather"); // Достаём вложенный объект

            String temperature = currentWeather.get("temperature").getAsString();
            String windspeed = currentWeather.get("windspeed").getAsString();
            String weathercode = currentWeather.get("weathercode").getAsString();

            tvCity.setText("Москва");
            tvTemperature.setText(temperature + "°C");
            tvWindSpeed.setText("Ветер: " + windspeed + " км/ч");
            tvWeatherDesc.setText("Погода: " + getWeatherDescription(weathercode));

        } catch (Exception e) {
            e.printStackTrace();
            tvTemperature.setText("Ошибка");
            tvWeatherDesc.setText("Ошибка: " + e.getMessage());
        }
    }

    // Получаем погоду
    private String getWeatherDescription(String weatherCode) {
        try {
            int code = Integer.parseInt(weatherCode);
            switch (code) {
                case 0: return "Ясно";
                case 1: return "В основном ясно";
                case 2: return "Переменная облачность";
                case 3: return "Пасмурно";
                case 45: case 48: return "Туман";
                case 51: case 53: case 55: return "Морось";
                case 61: case 63: case 65: return "Дождь";
                case 71: case 73: case 75: return "Снег";
                case 80: case 81: case 82: return "Ливень";
                case 95: return "Гроза";
                default: return "Неизвестно";
            }
        } catch (NumberFormatException e) {
            return "Данные загружены";
        }
    }
}