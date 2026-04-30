package ru.mirea.kartyshovav.cryptoloader;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.security.InvalidParameterException;

import javax.crypto.SecretKey;

import ru.mirea.kartyshovav.cryptoloader.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private ActivityMainBinding binding;
    public final String TAG = this.getClass().getSimpleName();
    private final int LoaderID = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.editTextMirea.setText("Мой номер по списку №9");

        binding.buttonMirea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = binding.editTextMirea.getText().toString();

                if (text.isEmpty() || text.equals("Мой номер по списку №9")) {
                    Toast.makeText(MainActivity.this, "Введите фразу для шифрования", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Генерация ключа
                SecretKey key = AESHelper.generateKey();

                // Шифрование текста
                byte[] encryptedData = AESHelper.encryptMsg(text, key);

                // Отправка данных в Loader
                // Loader — компонент, который позволяет загружать данные в фоновом потоке
                Bundle bundle = new Bundle();
                bundle.putByteArray(MyLoader.ARG_WORD, encryptedData);
                bundle.putByteArray("key", key.getEncoded());

                // Получаем экземпляр менеджера загрузчика и создаем загрузчик
                LoaderManager.getInstance(MainActivity.this).initLoader(LoaderID, bundle, MainActivity.this);

                Toast.makeText(MainActivity.this, "onCreateLoader: " + LoaderID, Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Создаем загрузчик
    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == LoaderID) {
            Toast.makeText(this, "onCreateLoader: " + id, Toast.LENGTH_SHORT).show();
            return new MyLoader(this, args);
        }
        throw new InvalidParameterException("Invalid loader id");
    }

    // Получение результата
    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if (loader.getId() == LoaderID) {
            Log.d(TAG, "onLoadFinished: " + data);
            Toast.makeText(this, "onLoadFinished: " + data, Toast.LENGTH_LONG).show();
        }
    }

    // Сброс загрузчика
    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
            Log.d(TAG, "onLoaderReset");
        }
}