package ru.mirea.kartyshovav.mireaproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;

public class MicrophoneFragment extends Fragment {

    private Button btnRecord, btnPlay;
    private TextView tvStatus;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private String fileName = null;
    private boolean isRecording = false;

    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (!isGranted) {
                    btnRecord.setEnabled(false);
                    tvStatus.setText("Нет разрешения на запись");
                    Toast.makeText(getContext(), "Разрешение не получено", Toast.LENGTH_SHORT).show();
                } else {
                    tvStatus.setText("Диктофон готов");
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_microphone, container, false);

        btnRecord = view.findViewById(R.id.btnRecord);
        btnPlay = view.findViewById(R.id.btnPlay);
        tvStatus = view.findViewById(R.id.tvStatus);

        // Путь для сохранения аудиофайла
        fileName = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC),
                "/voice_note.3gp").getAbsolutePath();

        // Проверка разрешения на запись аудио
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }

        // Кнопка записи
        btnRecord.setOnClickListener(v -> {
            if (!isRecording) {
                startRecording();
                btnRecord.setText("Остановить запись");
                tvStatus.setText("Идет запись");
                btnPlay.setEnabled(false);
            } else {
                stopRecording();
                btnRecord.setText("Начать запись");
                tvStatus.setText("Запись сохранена");
                btnPlay.setEnabled(true);
            }
            isRecording = !isRecording;
        });

        // Кнопка воспроизведения
        btnPlay.setOnClickListener(v -> startPlaying());

        return view;
    }

    private void startRecording() {
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(fileName);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка записи", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    private void startPlaying() {
        if (player != null) {
            player.release();
            player = null;
        }

        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
            tvStatus.setText("Воспроизведение");

            player.setOnCompletionListener(mp -> {
                tvStatus.setText("Воспроизведение завершено");
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> tvStatus.setText("Диктофон готов"));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка воспроизведения", Toast.LENGTH_SHORT).show();
            tvStatus.setText("Файл не найден");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
        if (player != null) {
            player.release();
            player = null;
        }
    }
}