package ru.mirea.kartyshovav.yandexmaps;

import android.app.Application;
import com.yandex.mapkit.MapKitFactory;

public class App extends Application {
    //private final String MAPKIT_API_KEY = "44633ad4-bde2-47b1-92da-8c25db6878bc";

    @Override
    public void onCreate() {
        super.onCreate();
        // Устанавливаем API-ключ для доступа к серверам Яндекса
        MapKitFactory.setApiKey("44633ad4-bde2-47b1-92da-8c25db6878bc");
    }
}