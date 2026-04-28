package ru.mirea.kartyshovav.cryptoloader;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MyLoader extends AsyncTaskLoader<String> {

    public static final String ARG_WORD = "word";
    private byte[] cryptText;
    private byte[] keyBytes;

    public MyLoader(@NonNull Context context, Bundle args) {
        super(context);
        if (args != null) {
            cryptText = args.getByteArray(ARG_WORD);
            keyBytes = args.getByteArray("key");
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        // Восстановление ключа
        SecretKey originalKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");

        // Дешифрование
        String decryptedText = AESHelper.decryptMsg(cryptText, originalKey);

        // Имитация длительной операции
        SystemClock.sleep(5000);

        return decryptedText;
    }
}
