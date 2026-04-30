package ru.mirea.kartyshovav.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ru.mirea.kartyshovav.looper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Handler - класс, который отправляет и обрабатывает сообщения в очередь (отвечает за организацию очереди)
        // Looper - класс, который создаёт очередь сообщений для потока и крутит бесконечный цикл обработки
        Handler mainThreadHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.d(MainActivity.class.getSimpleName(),
                        "Task execute. This is result: " + msg.getData().getString("result"));
            }
        };

        // Запускаем фоновый поток
        MyLooper myLooper = new MyLooper(mainThreadHandler);
        myLooper.start();

        binding.buttonMirea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String age = binding.editTextMirea.getText().toString();
                String job = binding.editTextJob.getText().toString();

                // Формируем сообщение
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putString("KEY", age + "|" + job);
                msg.setData(bundle);

                // Отправляем сообщение в другой поток
                myLooper.mHandler.sendMessage(msg);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}