package ru.mirea.kartyshovav.favoritebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ShareActivity extends AppCompatActivity {

    private TextView textView;
    private EditText editText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_share);

        textView = findViewById(R.id.textView2);
        editText = findViewById(R.id.editTextText);
        button = findViewById(R.id.buttonText);

        // Intent, с которым открылась активность
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String university = extras.getString(MainActivity.KEY);
            textView.setText(String.format("Моя любимая книга: %s", university));
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onButtonClick(View view) {
        String userBook = editText.getText().toString().trim();
        Intent data = new Intent();
        data.putExtra(MainActivity.USER_MESSAGE, userBook);
        setResult(Activity.RESULT_OK, data); // Результат (код результата, что возвращаем)
        finish(); // Закрываем активность
    }
}