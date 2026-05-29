package ru.mirea.kartyshovav.mireaproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private Button btnSignIn, btnCreateAccount;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        progressBar = findViewById(R.id.progressBar);

        // Инициализация Firebase
        mAuth = FirebaseAuth.getInstance();

        // Проверяем, не вошёл ли пользователь уже
        //if (mAuth.getCurrentUser() != null) {
        //    goToMainActivity();
        //}

        btnSignIn.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            if (validateFields(email, password)) {
                signIn(email, password);
            }
        });

        btnCreateAccount.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            if (validateFields(email, password)) {
                createAccount(email, password);
            }
        });
    }

    private boolean validateFields(String email, String password) {
        if (email.isEmpty()) {
            editEmail.setError("Введите email");
            return false;
        }
        if (password.isEmpty()) {
            editPassword.setError("Введите пароль");
            return false;
        }
        if (password.length() < 6) {
            editPassword.setError("Пароль должен быть не менее 6 символов");
            return false;
        }
        return true;
    }

    private void createAccount(String email, String password) {
        setLoading(true);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Аккаунт создан!", Toast.LENGTH_LONG).show();
                        goToMainActivity();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Ошибка регистрации";
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void signIn(String email, String password) {
        setLoading(true);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Вход выполнен!", Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Ошибка входа";
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Запускаем mainactivity в новой задаче (новый стек)
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSignIn.setEnabled(!isLoading);
        btnCreateAccount.setEnabled(!isLoading);
        editEmail.setEnabled(!isLoading);
        editPassword.setEnabled(!isLoading);
    }
}