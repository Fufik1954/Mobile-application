package ru.mirea.kartyshovav.mireaproject;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.util.concurrent.TimeUnit;

public class MySimpleWorker extends Worker {

    public MySimpleWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        Log.d("MySimpleWorker", "Фоновая задача запущена!");

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("MySimpleWorker", "Фоновая задача завершена!");
        return Result.success();
    }
}