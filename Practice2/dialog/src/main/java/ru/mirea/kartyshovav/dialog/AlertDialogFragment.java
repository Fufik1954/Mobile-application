package ru.mirea.kartyshovav.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

// DialogFragment - готовый механизм Android для показа диалогов
public class AlertDialogFragment extends DialogFragment {

    // Метод onCreateDialog вызывается системой, когда диалог нужно показать
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Создаем конструктор диалогового окна (сборка)
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Настройка окна
        builder.setTitle("Здравствуй МИРЭА!")
                .setMessage("Успех близок?")
                .setIcon(R.mipmap.ic_launcher_round)

                // Кнопки
                .setPositiveButton("Иду дальше", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Получаем текущую активность, приводим ее к типу MainActivity
                        // Так как getActivity возвращает просто Activity, и вызываем метод
                        ((MainActivity) getActivity()).onOkClicked();
                        dialog.cancel();
                    }
                })
                .setNeutralButton("На паузе", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((MainActivity)getActivity()).onNeutralClicked();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((MainActivity)getActivity()).onCancelClicked();
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}
