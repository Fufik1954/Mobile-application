package ru.mirea.kartyshovav.serviceapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

// Сервис - компонент для фоновой работы без UI
public class PlayerService extends Service {

    private MediaPlayer mediaPlayer; // Объкт воспроизведения
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // Вызывается при каждом запуске сервиса
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // Если трек закончился
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopForeground(true);
                stopSelf();
            }
        });
        return super.onStartCommand(intent, flags, startId); // Возвращаем стандартное поведение
    }

    // Инициализация
    @Override
    public void onCreate() {
        super.onCreate();

        // Создание уведомления
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentText("Playing...")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Lose: Brawl Stars"))
                .setContentTitle("Music Player - Картышов Артем");

        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "KartyshovAV", importance);
        channel.setDescription("MIREA Channel");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.createNotificationChannel(channel);

        startForeground(1, builder.build());

        // Инициализация плеера
        mediaPlayer = MediaPlayer.create(this, R.raw.brawl);
        mediaPlayer.setLooping(false);
    }

    // Остановка сервиса
    @Override
    public void onDestroy() {
        stopForeground(true);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    public PlayerService() {
    }
}