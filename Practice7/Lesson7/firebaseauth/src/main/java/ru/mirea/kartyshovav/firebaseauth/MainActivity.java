package ru.mirea.kartyshovav.firebaseauth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FirebaseAuth";
    private EditText editEmail;
    private EditText editPassword;
    private TextView statusTextView;
    private TextView detailTextView;
    private LinearLayout emailPasswordButtons;
    private LinearLayout signedInButtons;
    private Button btnSignIn;
    private Button btnCreateAccount;
    private Button btnSignOut;
    private Button btnVerifyEmail;

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        statusTextView = findViewById(R.id.statusTextView);
        detailTextView = findViewById(R.id.detailTextView);
        emailPasswordButtons = findViewById(R.id.emailPasswordButtons);
        signedInButtons = findViewById(R.id.signedInButtons);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnSignOut = findViewById(R.id.btnSignOut);
        btnVerifyEmail = findViewById(R.id.btnVerifyEmail);

        mAuth = FirebaseAuth.getInstance(); // Получаем экземпляр FirebaseAuth

        btnSignIn.setOnClickListener(v -> {
            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            if (!email.isEmpty() && !password.isEmpty()) {
                signIn(email, password);
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            }
        });

        btnCreateAccount.setOnClickListener(v -> {
            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            if (!email.isEmpty() && !password.isEmpty()) {
                createAccount(email, password);
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            }
        });

        btnSignOut.setOnClickListener(v -> signOut());
        btnVerifyEmail.setOnClickListener(v -> sendEmailVerification());
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI(mAuth.getCurrentUser()); // Проверяем, авторизован ли пользователь
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            statusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            detailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            emailPasswordButtons.setVisibility(View.GONE); // Скрываем форму входа
            signedInButtons.setVisibility(View.VISIBLE); // Показываем кнопки выхода/верификации
            btnVerifyEmail.setEnabled(!user.isEmailVerified());
        } else {
            statusTextView.setText(R.string.signed_out);
            detailTextView.setText(null);

            emailPasswordButtons.setVisibility(View.VISIBLE); // Показываем форму входа
            signedInButtons.setVisibility(View.GONE); // Скрываем кнопки выхода
        }
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Аккаунт создан!", Toast.LENGTH_SHORT).show();
                        updateUI(mAuth.getCurrentUser());
                    } else {
                        Toast.makeText(this, "Ошибка: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        updateUI(null);
                    }
                });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Вход выполнен!", Toast.LENGTH_SHORT).show();
                        updateUI(mAuth.getCurrentUser());
                    } else {
                        Toast.makeText(this, "Ошибка: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        updateUI(null);
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        Toast.makeText(this, "Вы вышли", Toast.LENGTH_SHORT).show();
        updateUI(null);
    }

    private void sendEmailVerification() {
        btnVerifyEmail.setEnabled(false);
        final FirebaseUser user = mAuth.getCurrentUser();
        Objects.requireNonNull(user).sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    btnVerifyEmail.setEnabled(true);
                    Toast.makeText(this, task.isSuccessful() ? "Письмо отправлено" : "Ошибка отправки", Toast.LENGTH_SHORT).show();
                });
    }
}