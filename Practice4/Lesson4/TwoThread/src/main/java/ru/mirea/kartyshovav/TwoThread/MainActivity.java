package ru.mirea.kartyshovav.TwoThread;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;

import ru.mirea.kartyshovav.TwoThread.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int threadCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Получаем главный поток
        Thread mainThread = Thread.currentThread();
        binding.textViewThreadInfo.setText("Имя текущего потока: " + mainThread.getName());
        mainThread.setName("ГРУППА: БСБО-09-23, НОМЕР ПО СПИСКУ: 9, МОЙ ЛЮБИМЫЙ ФИЛЬМ: Интерстеллар");
        binding.textViewThreadInfo.append("\nНовое имя потока: " + mainThread.getName());
        binding.textViewThreadInfo.append("\nПриоритет потока: " + mainThread.getPriority());

        // Выводим стек вызовов в Logcat
        Log.d(MainActivity.class.getSimpleName(), "Stack: " + Arrays.toString(mainThread.getStackTrace()));

        binding.btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thread — класс для управления любыми потоками
                // Runnable — интерфейс с методом, в котором пишем задачу
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int total = Integer.parseInt(binding.editTextLessons.getText().toString());
                        int days = Integer.parseInt(binding.editTextDays.getText().toString());
                        double avg = (double) total / days;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.textViewResult.setText("Среднее: " + avg);
                            }
                        });
                    }
                }).start();
            }
        });

        binding.buttonMirea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        int numberThread = threadCounter++;

                        Log.d("ThreadProject", String.format(
                                "Запущен поток № %d студентом группы № %s номер по списку № %d",
                                numberThread, "БСБО-09-23", 9
                        ));

                        long endTime = System.currentTimeMillis() + 20 * 1000;
                        while (System.currentTimeMillis() < endTime) {
                            synchronized (this) {
                                try {
                                    wait(endTime - System.currentTimeMillis());
                                    Log.d(MainActivity.class.getSimpleName(), "Endtime: " + endTime);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        Log.d("ThreadProject", "Выполнен поток № " + numberThread);
                    }
                }).start();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}