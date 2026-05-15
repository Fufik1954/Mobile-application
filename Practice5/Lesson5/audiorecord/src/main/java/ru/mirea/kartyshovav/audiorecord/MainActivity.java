package ru.mirea.kartyshovav.audiorecord;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 200;
    private final String TAG = "MainActivity";

    private Button recordButton;
    private Button playButton;
    private MediaRecorder recorder = null; // Для записи
    private MediaPlayer player = null; // Для воспроизведения
    private String recordFilePath = null; // Путь к файлу

    private boolean isStartRecording = true; // Идет запись
    private boolean isStartPlaying = true; // Воспроизведение идет
    private boolean isWork = false; // Разрешения

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recordButton = findViewById(R.id.recordButton);
        playButton = findViewById(R.id.playButton);
        playButton.setEnabled(false);

        // Путь к файлу во внутренней папке
        recordFilePath = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "/audiorecordtest.3gp").getAbsolutePath();

        // Проверка разрешений
        int audioRecordPermissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int storagePermissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (audioRecordPermissionStatus == PackageManager.PERMISSION_GRANTED && storagePermissionStatus == PackageManager.PERMISSION_GRANTED) {
            isWork = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        }

        // Обработка кнопки записи
        recordButton.setOnClickListener(v -> {
            if (isStartRecording) {
                recordButton.setText("Остановить");
                playButton.setEnabled(false);
                startRecording();
            } else {
                recordButton.setText("Начать запись");
                playButton.setEnabled(true);
                stopRecording();
            }
            isStartRecording = !isStartRecording;
        });

        // Обработка кнопки воспроизведения
        playButton.setOnClickListener(v -> {
            if (isStartPlaying) {
                playButton.setText("Остановить");
                recordButton.setEnabled(false);
                startPlaying();
            } else {
                playButton.setText("Воспроизвести");
                recordButton.setEnabled(true);
                stopPlaying();
            }
            isStartPlaying = !isStartPlaying;
        });
    }

    private void startRecording() {
        recorder = new MediaRecorder(); // Класс для записи аудио или видео
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // Источник - микрофон
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // Формат записи
        recorder.setOutputFile(recordFilePath); // Папка для хранения
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // Сжатие звука
        try {
            recorder.prepare(); // Подготовка
        } catch (IOException e) {
            Log.e(TAG, "prepare failed");
        }
        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release(); // Освобождаем ресурсы
        recorder = null;
    }

    private void startPlaying() {
        player = new MediaPlayer(); // Для воспроизведения аудио или видео
        try {
            player.setDataSource(recordFilePath); // Откуда брать
            player.prepare(); // Подготовка
            player.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            isWork = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
    }
}