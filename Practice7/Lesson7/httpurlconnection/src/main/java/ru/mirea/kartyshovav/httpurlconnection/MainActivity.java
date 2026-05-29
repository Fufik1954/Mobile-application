package ru.mirea.kartyshovav.httpurlconnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "HttpURLConnection";
    private Button btnGetData;
    private TextView tvIP;
    private TextView tvCity;
    private TextView tvRegion;
    private TextView tvLoc;
    private TextView tvWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGetData = findViewById(R.id.btnGetData);
        tvIP = findViewById(R.id.tvIP);
        tvCity = findViewById(R.id.tvCity);
        tvRegion = findViewById(R.id.tvRegion);
        tvLoc = findViewById(R.id.tvLoc);
        tvWeather = findViewById(R.id.tvWeather);

        btnGetData.setOnClickListener(v -> {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();

            if (networkinfo != null && networkinfo.isConnected()) {
                new DownloadIpTask().execute("https://ipinfo.io/json");
            } else {
                Toast.makeText(this, "Нет интернета", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Получение информации по IP
    private class DownloadIpTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                Log.e(TAG, "Ошибка загрузки IP", e);
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null || result.equals("error")) {
                Toast.makeText(MainActivity.this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject responseJson = new JSONObject(result); // Преобразуем строку JSON в объект
                String ip = responseJson.optString("ip", "Неизвестно");
                String city = responseJson.optString("city", "Неизвестно");
                String region = responseJson.optString("region", "Неизвестно");
                String loc = responseJson.optString("loc", "0,0");

                tvIP.setText("IP: " + ip);
                tvCity.setText("Город: " + city);
                tvRegion.setText("Регион: " + region);
                tvLoc.setText("Координаты: " + loc);

                // Разделяем локацию
                String[] coords = loc.split(",");
                if (coords.length == 2) {
                    String latitude = coords[0];
                    String longitude = coords[1];

                    // Запускаем второй запрос
                    new DownloadWeatherTask().execute(latitude, longitude);
                } else {
                    tvWeather.setText("Ошибка: неверный формат координат");
                }

            } catch (JSONException e) {
                Log.e(TAG, "Ошибка парсинга JSON", e);
                Toast.makeText(MainActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Получение погоды
    private class DownloadWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String lat = params[0];
            String lon = params[1];
            String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + lat +
                    "&longitude=" + lon + "&current_weather=true";
            try {
                return downloadUrl(weatherUrl);
            } catch (IOException e) {
                Log.e(TAG, "Ошибка загрузки погоды", e);
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null || result.equals("error")) {
                tvWeather.setText("Ошибка загрузки погоды");
                return;
            }

            try {
                JSONObject json = new JSONObject(result);
                if (json.has("current_weather")) { // Проверка наличия
                    JSONObject current = json.getJSONObject("current_weather");
                    String temp = current.getString("temperature");
                    String windspeed = current.optString("windspeed", "?");
                    tvWeather.setText("Температура: " + temp + "°C\nВетер: " + windspeed + " км/ч");
                } else {
                    tvWeather.setText("Данные о погоде не найдены");
                }
            } catch (JSONException e) {
                Log.e(TAG, "Ошибка парсинга погоды", e);
                tvWeather.setText("Ошибка обработки данных погоды");
            }
        }
    }

    // Универсальный метод для HTTP-запросов
    private String downloadUrl(String address) throws IOException {
        InputStream inputStream = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(address);
            connection = (HttpURLConnection) url.openConnection(); // Открываем соединение
            connection.setReadTimeout(10000); // Время ожидания чтения
            connection.setConnectTimeout(10000); // Время ожидания соединения
            connection.setRequestMethod("GET"); // Какой запрос
            connection.setInstanceFollowRedirects(true);
            connection.setDoInput(true); // Чтение данных из соединения

            int responseCode = connection.getResponseCode(); // HTTP статус
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream(); // Получаем поток для чтения данных
                ByteArrayOutputStream bos = new ByteArrayOutputStream(); // Буфер для хранения данных
                int read;
                while ((read = inputStream.read()) != -1) {
                    bos.write(read);
                }
                return bos.toString(); // Преобразуем байты в строку
            } else {
                return "HTTP Error: " + responseCode;
            }
        } finally {
            if (inputStream != null) {
                inputStream.close(); // Закрываем поток
            }
            if (connection != null) {
                connection.disconnect(); // Закрываем соединение
            }
        }
    }
}