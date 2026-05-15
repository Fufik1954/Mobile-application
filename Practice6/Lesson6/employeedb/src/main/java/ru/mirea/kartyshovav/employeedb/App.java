package ru.mirea.kartyshovav.employeedb;

import android.app.Application;
import androidx.room.Room;

// Singleton для БД
public class App extends Application {
    public static App instance; // Один экземпляр на всё приложение
    private AppDatabase database; // Строитель для создания БД

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, AppDatabase.class, "database")
                .allowMainThreadQueries() // Разрешает запросы в главном потоке
                .build();
    }

    public static App getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}