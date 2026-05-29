package ru.mirea.kartyshovav.mireaproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private EditText etName, etAge, etColor;
    private Button btnSave;
    private SharedPreferences sharedPref; // Хранилище для небольших данных

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        etName = view.findViewById(R.id.etName);
        etAge = view.findViewById(R.id.etAge);
        etColor = view.findViewById(R.id.etColor);
        btnSave = view.findViewById(R.id.btnSave);

        // Инициализация
        sharedPref = requireContext().getSharedPreferences("profile_settings", android.content.Context.MODE_PRIVATE);

        // Загружаем сохранённые данные при открытии
        loadData();

        // Обработчик кнопки сохранения
        btnSave.setOnClickListener(v -> saveData());

        return view;
    }

    private void loadData() {
        String name = sharedPref.getString("NAME", "");
        int age = sharedPref.getInt("AGE", 0);
        String color = sharedPref.getString("COLOR", "");

        etName.setText(name);
        etAge.setText(String.valueOf(age));
        etColor.setText(color);
    }

    private void saveData() {
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String color = etColor.getText().toString().trim();

        // Сохраняем в SharedPreferences
        SharedPreferences.Editor editor = sharedPref.edit(); // Интерфейс для редактирования настроек (редактор для записи)
        editor.putString("NAME", name);
        editor.putInt("AGE", ageStr.isEmpty() ? 0 : Integer.parseInt(ageStr));
        editor.putString("COLOR", color.isEmpty() ? "Не указан" : color);
        editor.apply();

        Toast.makeText(getContext(), "Профиль сохранён!", Toast.LENGTH_SHORT).show();
    }
}