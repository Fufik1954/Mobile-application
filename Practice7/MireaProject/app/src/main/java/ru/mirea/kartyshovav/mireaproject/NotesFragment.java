package ru.mirea.kartyshovav.mireaproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class NotesFragment extends Fragment {

    private TextView tvTitle, tvContent;
    private Button btnLoad;
    private FloatingActionButton fabAddNote;
    private File notesFile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        tvTitle = view.findViewById(R.id.tvTitle);
        tvContent = view.findViewById(R.id.tvContent);
        btnLoad = view.findViewById(R.id.btnLoad);
        fabAddNote = view.findViewById(R.id.fabAddNote);

        notesFile = new File(requireContext().getFilesDir(), "note.txt");

        loadNote();

        fabAddNote.setOnClickListener(v -> showNoteDialog());
        btnLoad.setOnClickListener(v -> loadNote());

        return view;
    }

    private void showNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Заметка");

        // Используем наш шаблон разметки
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_note, null);
        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etContent = dialogView.findViewById(R.id.etContent);

        // Устанавливаем кастомную разметку в диалог
        builder.setView(dialogView);
        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            // Сохраняем заметку в файл
            saveNoteToFile(title, content);
            Toast.makeText(getContext(), "Заметка сохранена!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void saveNoteToFile(String title, String content) {
        try (FileOutputStream fos = new FileOutputStream(notesFile); // Открываем файл для записи байтов
             OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {

            writer.write(title + "\n");
            writer.write(content);
            writer.flush();  // Принудительно записываем

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadNote() {
        if (!notesFile.exists()) {
            Toast.makeText(getContext(), "Нет сохранённой заметки", Toast.LENGTH_SHORT).show();
            tvTitle.setText("Нет заметки");
            tvContent.setText("Нажмите на +, чтобы создать заметку");
            return;
        }

        try (FileInputStream fis = new FileInputStream(notesFile); // Читаем байты из файла
             InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8); // Преобразовываем
             BufferedReader bufferedReader = new BufferedReader(reader)) { // Временная область памяти, где данные накапливаются перед обработкой

            String title = bufferedReader.readLine();
            String content = bufferedReader.readLine();

            if (title != null && content != null) {
                tvTitle.setText(title);
                tvContent.setText(content);
                Toast.makeText(getContext(), "Заметка загружена", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Файл повреждён", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show();
        }
    }
}