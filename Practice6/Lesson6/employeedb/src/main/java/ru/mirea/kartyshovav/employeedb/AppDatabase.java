package ru.mirea.kartyshovav.employeedb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

// Класс базы данных
@Database(entities = {Employee.class}, version = 1) // Список таблиц и версия
public abstract class AppDatabase extends RoomDatabase {
    public abstract EmployeeDao employeeDao(); // Абстрактный метод, возвращающий методы для работы с бд
}
