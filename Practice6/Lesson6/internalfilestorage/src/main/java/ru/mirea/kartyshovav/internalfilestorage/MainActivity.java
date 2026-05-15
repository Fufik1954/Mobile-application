package ru.mirea.kartyshovav.internalfilestorage;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_NAME = "history.txt";
    private EditText editTextDate;
    private TextView textViewResult;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextDate = findViewById(R.id.editTextDate);
        textViewResult = findViewById(R.id.textViewResult);
        buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(v -> {
            String text = editTextDate.getText().toString().trim();
            if (!text.isEmpty()) {
                saveText(text);
            } else {
                Toast.makeText(this, "Введите текст", Toast.LENGTH_SHORT).show();
            }
        });

        loadTextInBackground();
    }

    // Используем абстрактный класс для работы с файлами
    private void saveText(String text) {
        try (FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) { // Открываем файл для записи
            fos.write(text.getBytes()); // Преобразует строку в байты и записываем
            Toast.makeText(this, "Файл сохранён", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTextInBackground() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                String result = getTextFromFile();

                textViewResult.post(() -> textViewResult.setText(result)); // Отправляем в главный поток
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public String getTextFromFile() {
        FileInputStream fin = null; // Класс для чтения байтов из файла
        try {
            fin = openFileInput(FILE_NAME); // Открываем файл
            byte[] bytes = new byte[fin.available()]; // Создаём массив нужного размера
            fin.read(bytes); // Читаем все байты из файла в массив
            return new String(bytes);
        } catch (IOException ex) {
            return "Файл пока пуст. Введите дату и нажмите «Записать в файл».";
        } finally {
            try {
                if (fin != null) fin.close(); // Закрываем поток
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}