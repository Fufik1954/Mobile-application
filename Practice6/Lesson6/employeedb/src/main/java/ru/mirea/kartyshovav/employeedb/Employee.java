package ru.mirea.kartyshovav.employeedb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Employee { // Таблица в базе данных
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    public int salary;
}