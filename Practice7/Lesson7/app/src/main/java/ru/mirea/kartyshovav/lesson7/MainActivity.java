package ru.mirea.kartyshovav.lesson7;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SocketActivity";
    private static final String HOST = "time-a.nist.gov";
    private static final int PORT = 13;

    private TextView textViewTime;
    private Button buttonGetTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textViewTime = findViewById(R.id.textViewTime);
        buttonGetTime = findViewById(R.id.buttonGetTime);

        buttonGetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetTimeTask().execute();
            }
        });
    }

    // AsyncTask  для работы в другом потоке
    private class GetTimeTask extends AsyncTask<Void, Void, String> {

        // Метод для севетой операции
        @Override
        protected String doInBackground(Void... params) {
            String timeResult = "";
            try {
                // Создание сокета и подключение
                Socket socket = new Socket(HOST, PORT);
                BufferedReader reader = SocketUtils.getReader(socket);

                // Считываем данные
                reader.readLine();
                timeResult = reader.readLine();

                Log.d(TAG, "Ответ сервера: " + timeResult);
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Ошибка сокета: " + e.getMessage());
            }
            return timeResult;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null && !result.isEmpty()) {
                // Парсинг строки
                String[] parts = result.split(" ");
                if (parts.length > 2) {
                    String date = parts[1]; // Дата
                    String time = parts[2]; // Время
                    textViewTime.setText("Дата: " + date + "\nВремя: " + time);
                } else {
                    textViewTime.setText("Неверный формат данных: " + result);
                }
            } else {
                textViewTime.setText("Ошибка получения данных");
            }
        }
    }
}
