package ru.mirea.kartyshovav.lesson6;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etGroup, etNumber, etMovie;
    private SharedPreferences sharedPref; // Хранилище для небольших данных

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etGroup = findViewById(R.id.etGroup);
        etNumber = findViewById(R.id.etNumber);
        etMovie = findViewById(R.id.etMovie);

        sharedPref = getSharedPreferences("mirea_settings", MODE_PRIVATE);

        loadSavedData();
    }

    // Загрузка сохраненных данных
    private void loadSavedData() {
        String group = sharedPref.getString("GROUP", "");
        int number = sharedPref.getInt("NUMBER", 0);
        String movie = sharedPref.getString("MOVIE", "");

        etGroup.setText(group);
        etNumber.setText(String.valueOf(number));
        etMovie.setText(movie);
    }

    public void onSaveButtonClick(View view) {
        SharedPreferences.Editor editor = sharedPref.edit();

        // Получаем текст из полей ввода
        String group = etGroup.getText().toString();
        String numberStr = etNumber.getText().toString();
        String movie = etMovie.getText().toString();

        int number = numberStr.isEmpty() ? 0 : Integer.parseInt(numberStr);

        editor.putString("GROUP", group);
        editor.putInt("NUMBER", number);
        editor.putString("MOVIE", movie);
        editor.apply();
    }
}