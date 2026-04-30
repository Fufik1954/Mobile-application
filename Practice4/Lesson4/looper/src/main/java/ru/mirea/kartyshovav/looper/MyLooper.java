package ru.mirea.kartyshovav.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
public class MyLooper extends Thread{
    public Handler mHandler;
    private Handler mainHandler;

    // Сохраняем Handler главного потока, чтобы потом отправить результат обратно
    public MyLooper(Handler mainThreadHandler) {
        mainHandler = mainThreadHandler;
    }

    // Точка входа в поток
    public void run() {
        Log.d("MyLooper", "run");
        Looper.prepare(); // Создаем очередь сообщений

        mHandler = new Handler(Looper.myLooper()) {
            public void handleMessage(Message msg) {
                String data = msg.getData().getString("KEY");
                Log.d("MyLooper get message: ", data);

                // Возраст передаётся в KEY в формате "возраст|работа"
                String[] parts = data.split("\\|");
                String age = parts[0];
                String job = parts[1];

                int delaySeconds = Integer.parseInt(age);
                try {
                    Thread.sleep(delaySeconds * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Отправляем результат обратно в главный поток
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("result", String.format(
                        "Ваш возраст: %s, работа: %s. Задержка составила %d секунд",
                        age, job, delaySeconds
                ));
                message.setData(bundle);
                mainHandler.sendMessage(message);
            }
        };
        Looper.loop();
    }
}
