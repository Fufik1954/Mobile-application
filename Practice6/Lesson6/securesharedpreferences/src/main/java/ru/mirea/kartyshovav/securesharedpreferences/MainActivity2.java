package ru.mirea.kartyshovav.securesharedpreferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainActivity2 extends AppCompatActivity {

    private EditText etPoetName;
    private TextView tvPoetName, tvStatus;
    private Button btnSave;
    private ImageView ivPoet;
    private SharedPreferences secureSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        etPoetName = findViewById(R.id.etPoetName);
        tvPoetName = findViewById(R.id.tvPoetName);
        tvStatus = findViewById(R.id.tvStatus);
        btnSave = findViewById(R.id.btnSave);
        ivPoet = findViewById(R.id.ivPoet);

        // Инициализация шифрованного SharedPreferences
        initEncryptedSharedPreferences();

        // Загрузка сохранённого имени поэта
        loadPoetName();

        // Обработчик кнопки сохранения
        btnSave.setOnClickListener(v -> savePoetName());
    }

    private void initEncryptedSharedPreferences() {
        try {
            // Создаём ключ
            String mainKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            // Создаём обёртку
            secureSharedPreferences = EncryptedSharedPreferences.create(
                    "secret_shared_prefs",           // имя файла
                    mainKeyAlias,                     // псевдоним ключа
                    getBaseContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,   // шифрование ключей
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM  // шифрование значений
            );

            tvStatus.setText("Статус: шифрованное хранилище готово");

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            tvStatus.setText("Статус: ошибка шифрования");
            Toast.makeText(this, "Ошибка инициализации шифрования", Toast.LENGTH_LONG).show();
        }
    }

    private void loadPoetName() {
        if (secureSharedPreferences != null) {
            String poetName = secureSharedPreferences.getString("POET_NAME", "Александр Пушкин");
            tvPoetName.setText(poetName);
            etPoetName.setText(poetName);
        }
    }

    private void savePoetName() {
        if (secureSharedPreferences != null) {
            String newPoetName = etPoetName.getText().toString().trim();

            if (newPoetName.isEmpty()) {
                Toast.makeText(this, "Введите имя поэта", Toast.LENGTH_SHORT).show();
                return;
            }

            // Сохраняем зашифрованное значение
            secureSharedPreferences.edit()
                    .putString("POET_NAME", newPoetName)
                    .apply();

            tvPoetName.setText(newPoetName);
            tvStatus.setText("Статус: имя сохранено (зашифровано)");
            Toast.makeText(this, "Имя поэта сохранено в зашифрованном виде", Toast.LENGTH_SHORT).show();

            // Очищаем поле ввода
            etPoetName.setText("");
        } else {
            Toast.makeText(this, "Ошибка: хранилище не инициализировано", Toast.LENGTH_SHORT).show();
        }
    }
}