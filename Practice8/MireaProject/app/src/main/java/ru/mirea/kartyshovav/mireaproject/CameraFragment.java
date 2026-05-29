package ru.mirea.kartyshovav.mireaproject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraFragment extends Fragment {

    private static final int REQUEST_CODE_PERMISSION = 100;
    private ImageView ivAvatar;
    private Button btnMakePhoto;
    private Uri imageUri;
    private boolean isWork = false;

    private ActivityResultLauncher<Intent> cameraLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        ivAvatar = view.findViewById(R.id.ivAvatar);
        btnMakePhoto = view.findViewById(R.id.btnMakePhoto);

        // Проверка разрешений
        int cameraPermissionStatus = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA);
        int storagePermissionStatus = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (cameraPermissionStatus == PackageManager.PERMISSION_GRANTED && storagePermissionStatus == PackageManager.PERMISSION_GRANTED) {
            isWork = true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        }

        // Обработчик результата съемки
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && imageUri != null) {
                        ivAvatar.setImageURI(imageUri);
                        Toast.makeText(getContext(), "Фото сохранено", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Фото не сделано", Toast.LENGTH_SHORT).show();
                    }
                });

        // Обработчик кнопки
        btnMakePhoto.setOnClickListener(v -> {
            if (!isWork) {
                Toast.makeText(getContext(), "Нет разрешения на камеру", Toast.LENGTH_SHORT).show();
                return;
            }
            dispatchTakePictureIntent();
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            isWork = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!isWork) {
                btnMakePhoto.setEnabled(false);
                Toast.makeText(getContext(), "Нет разрешения на камеру", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Запуск камеры
    private void dispatchTakePictureIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = createImageFile(); // Пустой файл
            String authorities = requireContext().getPackageName() + ".fileprovider"; // Формируем строку авторизации
            imageUri = FileProvider.getUriForFile(requireContext(), authorities, photoFile); // Безопасный URI
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // Куда сохранить фото
            cameraLauncher.launch(cameraIntent); // Запускаем камеру
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка при создании файла", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date()); // Текущая дата и время
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES); // Папка для хранения
        return File.createTempFile("PHOTO_" + timeStamp, ".jpg", storageDir);
    }
}