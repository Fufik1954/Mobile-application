package ru.mirea.kartyshovav.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class MyProgressDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Создаём ProgressDialog
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Загрузка");
        progressDialog.setMessage("Пожалуйста, подождите...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  // крутящийся индикатор
        progressDialog.setCancelable(false);

        new Handler().postDelayed(() -> {
            if (progressDialog.isShowing()) {
                dismiss();
            }
        }, 3000);

        return progressDialog;
    }
}
