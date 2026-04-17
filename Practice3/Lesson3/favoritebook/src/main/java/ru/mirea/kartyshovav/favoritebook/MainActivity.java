package ru.mirea.kartyshovav.favoritebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCallerLauncher;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultLauncherKt;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // Объект, который позволяет запускать активности и передавать данные
    // такие как intent, которые содержат инф. о том, какая активность должна работать
    // интсрумент для запуска активити с ожиданием результата
    private ActivityResultLauncher<Intent> activityResultLauncher;
    static final String KEY = "book_name";
    static final String USER_MESSAGE = "MESSAGE";
    private TextView textViewUserBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        textViewUserBook = findViewById(R.id.textView);

        // onActivityResult вызывается при закрытии второй активности и выполняет дейсвтие в зависимости от результата
        ActivityResultCallback<ActivityResult> callback  = new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result){
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData(); // Контейнер с данными, которая вернула вторая активность
                    String userBook = data.getStringExtra(USER_MESSAGE);
                    textViewUserBook.setText("Название вашей любимой книги: " + userBook);
                }
            }
        };

        // registerForActivityResult - метод для регистрации функции
        // Связка:Launcher (кто запускает) и callback (кто обрабатывает результат)
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                callback);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void getInfoAboutBoock(View view) {
        Intent intent = new Intent(this, ShareActivity.class);
        intent.putExtra(KEY, "Анабасис");
        activityResultLauncher.launch(intent); // Запускаем активность и ждем результат
    }
}