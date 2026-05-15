package ru.mirea.kartyshovav.mireaproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ru.mirea.kartyshovav.mireaproject.R;

public class WebViewFragment extends Fragment {

    private WebView webView;
    private EditText etUrl;
    private Button btnGo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);

        webView = view.findViewById(R.id.webView);
        etUrl = view.findViewById(R.id.etUrl);
        btnGo = view.findViewById(R.id.btnGo);

        // Включаем JavaScript
        webView.getSettings().setJavaScriptEnabled(true);

        // Чтобы страницы открывались внутри WebView, а не в браузере
        webView.setWebViewClient(new WebViewClient());

        // Загружаем страницу по умолчанию
        String defaultUrl = "https://developer.android.com";
        etUrl.setText(defaultUrl);
        webView.loadUrl(defaultUrl);

        // Обработка нажатия на кнопку Go
        btnGo.setOnClickListener(v -> {
            String url = etUrl.getText().toString().trim();
            if (url.isEmpty()) {
                url = "https://developer.android.com";
            }
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            webView.loadUrl(url);
        });

        return view;
    }
}