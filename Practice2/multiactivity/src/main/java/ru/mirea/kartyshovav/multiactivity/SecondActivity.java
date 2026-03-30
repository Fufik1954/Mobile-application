package ru.mirea.kartyshovav.multiactivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SecondActivity extends AppCompatActivity {

    private String text;
    private TextView textView;
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate вызван (SecondActivity)");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);

        textView = findViewById(R.id.textView);
        text = getIntent().getStringExtra("key");
        if (text != null){
            textView.setText(text);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart вызван (SecondActivity)");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume вызван (SecondActivity)");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause вызван (SecondActivity)");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop вызван (SecondActivity)");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart вызван (SecondActivity)");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy вызван (SecondActivity)");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState вызван (SecondActivity)");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState вызван (SecondActivity)");
    }
}