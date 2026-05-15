package ru.mirea.kartyshovav.mireaproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

public class TaskFragment extends Fragment {

    private Button btnStartTask;
    private TextView tvStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        btnStartTask = view.findViewById(R.id.btnStartTask);
        tvStatus = view.findViewById(R.id.tvStatus);

        btnStartTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvStatus.setText("Задача запущена");

                // Условия
                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                // Запуск задачи
                WorkRequest workRequest = new OneTimeWorkRequest.Builder(MySimpleWorker.class)
                        .setConstraints(constraints)
                        .build();
                WorkManager.getInstance(requireContext()).enqueue(workRequest);
            }
        });

        return view;
    }
}